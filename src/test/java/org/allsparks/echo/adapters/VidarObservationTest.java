package org.allsparks.echo.adapters;

import org.allsparks.echo.EchoDecision;
import org.allsparks.echo.EchoEngine;
import org.allsparks.echo.EchoFeatureFlags;
import org.allsparks.echo.clock.FakeClock;
import org.allsparks.echo.config.EchoConfig;
import org.allsparks.echo.cue.CueFamily;
import org.allsparks.echo.cue.RejectionReason;
import org.allsparks.echo.cue.SilenceReason;
import org.allsparks.echo.input.AudioDeviceStatus;
import org.allsparks.echo.input.EchoSnapshot;
import org.allsparks.echo.input.TargetSource;
import org.allsparks.echo.render.FakeRenderer;
import org.allsparks.echo.value.Angles;
import org.allsparks.echo.value.Presence;
import org.allsparks.echo.value.Scalar;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class VidarObservationTest {

    private final FakeClock clock = new FakeClock();
    private final EchoConfig config = EchoConfig.defaults();

    @Test
    void adapterFillsExplicitTarget() {
        VidarObservation obs = new VidarObservation(
                "tag-3", "sample", Scalar.of(0.5), Scalar.of(1.1), Scalar.of(0.9), clock.nanoTime());
        EchoSnapshot snap = apply(obs);
        assertEquals("tag-3", snap.targetId());
        assertEquals(TargetSource.BOUNDED_ADAPTER, snap.targetSource());
        assertEquals("vidar", obs.sourceId);
        assertEquals(VidarObservation.CONTRACT, "vidar-echo.v0");
        assertEquals(CueFamily.GUIDANCE, engineWithVidar().step(snap).record().selected());
    }

    @Test
    void boundedAdapterRejectedWhenFlagOff() {
        VidarObservation obs = new VidarObservation(
                "tag-3", "sample", Scalar.of(0.5), Scalar.of(1.1), Scalar.of(0.9), clock.nanoTime());
        EchoSnapshot snap = apply(obs);
        EchoEngine engine = EchoEngine.phase0(clock);
        EchoDecision d = engine.step(snap);
        assertEquals(CueFamily.SILENCE, d.record().selected());
        assertEquals(SilenceReason.MISSING_CAPABILITY, d.record().silenceReason());
        assertTrue(d.record().rejected().stream()
                .anyMatch(r -> r.reason() == RejectionReason.VIDAR_ADAPTER_DISABLED));
    }

    @Test
    void adapterStaleObservationSilences() {
        long now = clock.nanoTime();
        VidarObservation obs = new VidarObservation(
                "tag-3", "sample", Scalar.of(0.2), Scalar.of(1.0), Scalar.of(0.9),
                now - 400_000_000L);
        EchoSnapshot snap = obs.applyTo(EchoSnapshot.builder()
                        .receiptNanos(now)
                        .driverEnabled(true)
                        .audioDeviceStatus(AudioDeviceStatus.AVAILABLE))
                .build();
        assertEquals(SilenceReason.STALE, engineWithVidar().step(snap).record().silenceReason());
    }

    @Test
    void adapterLowConfidenceSilences() {
        VidarObservation obs = new VidarObservation(
                "tag-3", "sample", Scalar.of(0.2), Scalar.of(1.0), Scalar.of(0.1), clock.nanoTime());
        assertEquals(SilenceReason.LOW_CONFIDENCE,
                engineWithVidar().step(apply(obs)).record().silenceReason());
    }

    @Test
    void adapterUnknownBearingSilences() {
        VidarObservation obs = new VidarObservation(
                "tag-3", "sample", Scalar.unknown(), Scalar.of(1.0), Scalar.of(0.9), clock.nanoTime());
        assertEquals(SilenceReason.UNKNOWN_INPUT,
                engineWithVidar().step(apply(obs)).record().silenceReason());
        assertEquals(Presence.UNKNOWN, obs.bearingRad.presence());
    }

    @Test
    void adapterEmptyTargetIdSilences() {
        VidarObservation obs = new VidarObservation(
                "", "sample", Scalar.of(0.2), Scalar.of(1.0), Scalar.of(0.9), clock.nanoTime());
        assertEquals(SilenceReason.NO_TARGET,
                engineWithVidar().step(apply(obs)).record().silenceReason());
    }

    @Test
    void sourceIdDefaultsToVidar() {
        VidarObservation unnamed = new VidarObservation(
                "tag-1", "sample", Scalar.of(0), Scalar.of(1), Scalar.of(0.9), 0L);
        assertEquals(VidarObservation.DEFAULT_SOURCE_ID, unnamed.sourceId);
        VidarObservation named = new VidarObservation(
                "custom-vidar", "tag-1", "sample", Scalar.of(0), Scalar.of(1), Scalar.of(0.9), 0L);
        assertEquals("custom-vidar", named.sourceId);
        VidarObservation blank = new VidarObservation(
                "", "tag-1", "sample", Scalar.of(0), Scalar.of(1), Scalar.of(0.9), 0L);
        assertEquals("vidar", blank.sourceId);
    }

    @Test
    void wrapBearingRadUsesAngles() {
        Scalar wrapped = VidarObservation.wrapBearingRad(Math.toRadians(350));
        assertEquals(Presence.PRESENT, wrapped.presence());
        assertEquals(Angles.fromDegrees(-10), wrapped.si(), 1e-9);
        assertEquals(Angles.wrapRad(3 * Math.PI), VidarObservation.wrapBearingRad(3 * Math.PI).si(), 1e-9);
    }

    private EchoSnapshot apply(VidarObservation obs) {
        return obs.applyTo(EchoSnapshot.builder()
                        .receiptNanos(clock.nanoTime())
                        .driverEnabled(true)
                        .audioDeviceStatus(AudioDeviceStatus.AVAILABLE))
                .build();
    }

    private EchoEngine engineWithVidar() {
        return new EchoEngine(clock, config,
                EchoFeatureFlags.builder().vidarAdapter(true).build(),
                new FakeRenderer());
    }
}
