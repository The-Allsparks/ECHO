package org.allsparks.echo.adapters;

import org.allsparks.echo.EchoDecision;
import org.allsparks.echo.EchoEngine;
import org.allsparks.echo.EchoFeatureFlags;
import org.allsparks.echo.clock.FakeClock;
import org.allsparks.echo.config.EchoConfig;
import org.allsparks.echo.cue.CueFamily;
import org.allsparks.echo.cue.RejectionReason;
import org.allsparks.echo.input.AudioDeviceStatus;
import org.allsparks.echo.input.EchoSnapshot;
import org.allsparks.echo.input.TargetSource;
import org.allsparks.echo.render.FakeRenderer;
import org.allsparks.echo.training.Snapshots;
import org.allsparks.echo.value.Flag;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AmperObservationTest {

    private final FakeClock clock = new FakeClock();
    private final EchoConfig config = EchoConfig.defaults();

    @Test
    void warningTrueAndAdapterOnSelectsWarnAmper() {
        FakeRenderer renderer = new FakeRenderer();
        EchoEngine engine = new EchoEngine(clock, config,
                EchoFeatureFlags.builder().amperAdapter(true).build(),
                renderer);
        AmperObservation obs = new AmperObservation(Flag.of(true), clock.nanoTime());
        EchoSnapshot snap = applyToGuidance(obs);
        EchoDecision d = engine.step(snap);
        assertEquals(CueFamily.WARN_AMPER, d.record().selected());
        assertEquals(CueFamily.WARN_AMPER, renderer.rendered().get(0).family());
        assertEquals(AmperObservation.CONTRACT, "amper-echo.v0");
        assertEquals("amper", obs.sourceId);
        assertTrue(snap.amperWarning().isTrue());
    }

    @Test
    void unavailableDoesNotInventWarnAmper() {
        FakeRenderer renderer = new FakeRenderer();
        EchoEngine engine = new EchoEngine(clock, config,
                EchoFeatureFlags.builder().amperAdapter(true).build(),
                renderer);
        AmperObservation obs = new AmperObservation(Flag.unavailable(), clock.nanoTime());
        EchoSnapshot snap = applyToGuidance(obs);
        EchoDecision d = engine.step(snap);
        assertEquals(CueFamily.GUIDANCE, d.record().selected());
        assertNotEquals(CueFamily.WARN_AMPER, d.record().selected());
        assertEquals(CueFamily.GUIDANCE, renderer.rendered().get(0).family());
        assertTrue(d.record().rejected().stream()
                .noneMatch(r -> r.family() == CueFamily.WARN_AMPER));
    }

    @Test
    void adapterOffRejectsFlagDisabledNotWarnAmper() {
        FakeRenderer renderer = new FakeRenderer();
        EchoEngine engine = new EchoEngine(clock, config,
                EchoFeatureFlags.disabled(),
                renderer);
        AmperObservation obs = new AmperObservation(Flag.of(true), clock.nanoTime());
        EchoSnapshot snap = applyToGuidance(obs);
        EchoDecision d = engine.step(snap);
        assertNotEquals(CueFamily.WARN_AMPER, d.record().selected());
        assertEquals(CueFamily.GUIDANCE, d.record().selected());
        assertTrue(d.record().rejected().stream()
                .anyMatch(r -> r.family() == CueFamily.WARN_AMPER
                        && r.reason() == RejectionReason.FLAG_DISABLED));
        assertEquals(CueFamily.GUIDANCE, renderer.rendered().get(0).family());
        assertFalse(engine.flags().amperAdapter());
    }

    @Test
    void applyToSetsWarningOnly() {
        AmperObservation obs = new AmperObservation(Flag.of(true), 42L);
        EchoSnapshot snap = obs.applyTo(EchoSnapshot.builder()
                        .receiptNanos(clock.nanoTime())
                        .driverEnabled(true)
                        .audioDeviceStatus(AudioDeviceStatus.AVAILABLE))
                .build();
        assertTrue(snap.amperWarning().isTrue());
        assertEquals(TargetSource.NONE, snap.targetSource());
        assertNull(snap.targetId());
        assertNull(snap.observationNanos());
        assertEquals(Long.valueOf(42L), obs.observationNanos);
    }

    @Test
    void sourceIdDefaultsToAmper() {
        AmperObservation unnamed = new AmperObservation(Flag.of(false));
        assertEquals(AmperObservation.DEFAULT_SOURCE_ID, unnamed.sourceId);
        AmperObservation named = new AmperObservation("custom-amper", Flag.of(true), 0L);
        assertEquals("custom-amper", named.sourceId);
        AmperObservation blank = new AmperObservation("", Flag.of(true), null);
        assertEquals("amper", blank.sourceId);
    }

    private EchoSnapshot applyToGuidance(AmperObservation obs) {
        return obs.applyTo(Snapshots.guidance(clock, 0.2, 1.0, 0.9).toBuilder()).build();
    }
}
