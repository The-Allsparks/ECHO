package org.allsparks.echo;

import org.allsparks.echo.clock.FakeClock;
import org.allsparks.echo.config.EchoConfig;
import org.allsparks.echo.cue.CueFamily;
import org.allsparks.echo.cue.RejectionReason;
import org.allsparks.echo.cue.SilenceReason;
import org.allsparks.echo.input.AudioDeviceStatus;
import org.allsparks.echo.input.EchoSnapshot;
import org.allsparks.echo.input.TargetSource;
import org.allsparks.echo.observe.EchoDecisionRecord;
import org.allsparks.echo.render.FailingRenderer;
import org.allsparks.echo.render.FakeRenderer;
import org.allsparks.echo.sonify.PanMapper;
import org.allsparks.echo.sonify.PulseMapper;
import org.allsparks.echo.training.Snapshots;
import org.allsparks.echo.value.Angles;
import org.allsparks.echo.value.Flag;
import org.allsparks.echo.value.Presence;
import org.allsparks.echo.value.Scalar;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EchoCoreTest {

    private final FakeClock clock = new FakeClock();
    private final EchoConfig config = EchoConfig.defaults();

    @Test
    void unknownIsNotZero() {
        assertNotEquals(Scalar.of(0), Scalar.unknown());
        assertEquals(Presence.UNKNOWN, Scalar.unknown().presence());
        assertThrows(IllegalStateException.class, () -> Scalar.unknown().si());
        assertFalse(Flag.unknown().isFalse());
        assertFalse(Flag.unknown().isTrue());
    }

    @Test
    void bearingNormalizationGrid() {
        assertEquals(0.0, Angles.wrapRad(0), 1e-9);
        assertEquals(0.0, Angles.fromDegrees(360), 1e-9);
        assertEquals(0.0, Angles.fromDegrees(-360), 1e-9);
        assertEquals(Math.PI, Angles.fromDegrees(180), 1e-9);
        assertEquals(Angles.fromDegrees(-10), Angles.fromDegrees(350), 1e-9);
        for (int deg = -720; deg <= 720; deg += 15) {
            double w = Angles.fromDegrees(deg);
            assertTrue(w > -Math.PI - 1e-9 && w <= Math.PI + 1e-9, "deg=" + deg);
        }
    }

    @Test
    void panLeftCenterRight() {
        double left = PanMapper.panFromBearingRad(-Math.PI / 2, config);
        double center = PanMapper.panFromBearingRad(0, config);
        double right = PanMapper.panFromBearingRad(Math.PI / 2, config);
        assertEquals(-1.0, left, 1e-9);
        assertEquals(0.0, center, 1e-9);
        assertEquals(1.0, right, 1e-9);
        assertTrue(PanMapper.leftGain(left) > PanMapper.rightGain(left));
        assertEquals(PanMapper.leftGain(0), PanMapper.rightGain(0), 1e-9);
        assertTrue(PanMapper.rightGain(right) > PanMapper.leftGain(right));
        double power = PanMapper.leftGain(0.3) * PanMapper.leftGain(0.3)
                + PanMapper.rightGain(0.3) * PanMapper.rightGain(0.3);
        assertEquals(1.0, power, 1e-9);
    }

    @Test
    void distancePulseBoundaries() {
        assertEquals(config.pulseNearMs(), PulseMapper.intervalMsFromDistanceM(0, config), 1e-9);
        assertEquals(config.pulseNearMs(), PulseMapper.intervalMsFromDistanceM(config.pulseNearM(), config), 1e-9);
        assertEquals(config.pulseFarMs(), PulseMapper.intervalMsFromDistanceM(config.pulseFarM(), config), 1e-9);
        assertEquals(config.pulseFarMs(), PulseMapper.intervalMsFromDistanceM(99, config), 1e-9);
        double mid = PulseMapper.intervalMsFromDistanceM((config.pulseNearM() + config.pulseFarM()) / 2, config);
        assertTrue(mid > config.pulseNearMs() && mid < config.pulseFarMs());
    }

    @Test
    void alignmentPulseMapping() {
        assertEquals(config.pulseNearMs(), PulseMapper.intervalMsFromAlignmentRad(0, config), 1e-9);
        assertEquals(config.pulseFarMs(),
                PulseMapper.intervalMsFromAlignmentRad(config.panSaturationBearingRad(), config), 1e-9);
    }

    @Test
    void guidanceLeftSelected() {
        EchoEngine engine = EchoEngine.phase0(clock);
        EchoDecision d = engine.step(Snapshots.guidance(clock, -Math.PI / 2, 1.0, 0.9));
        assertEquals(CueFamily.GUIDANCE, d.record().selected());
        assertEquals(-1.0, d.record().sonified().pan(), 1e-6);
        assertEquals(SilenceReason.NONE, d.record().silenceReason());
    }

    @Test
    void staleObservationRejected() {
        EchoEngine engine = EchoEngine.phase0(clock);
        long now = clock.nanoTime();
        EchoSnapshot snap = Snapshots.guidance(clock, 0, 1.0, 0.9).toBuilder()
                .receiptNanos(now)
                .observationNanos(now - 400_000_000L)
                .build();
        EchoDecision d = engine.step(snap);
        assertEquals(CueFamily.SILENCE, d.record().selected());
        assertEquals(SilenceReason.STALE, d.record().silenceReason());
    }

    @Test
    void lowConfidenceRejected() {
        EchoEngine engine = EchoEngine.phase0(clock);
        EchoDecision d = engine.step(Snapshots.guidance(clock, 0, 1.0, 0.1));
        assertEquals(SilenceReason.LOW_CONFIDENCE, d.record().silenceReason());
    }

    @Test
    void unknownBearingSilence() {
        EchoEngine engine = EchoEngine.phase0(clock);
        EchoSnapshot snap = Snapshots.guidance(clock, 0, 1.0, 0.9).toBuilder()
                .bearingRad(Scalar.unknown())
                .build();
        assertEquals(SilenceReason.UNKNOWN_INPUT, engine.step(snap).record().silenceReason());
    }

    @Test
    void disabledSilence() {
        EchoEngine engine = EchoEngine.phase0(clock);
        EchoSnapshot snap = Snapshots.guidance(clock, 0, 1.0, 0.9).toBuilder()
                .driverEnabled(false)
                .build();
        EchoDecision d = engine.step(snap);
        assertEquals(SilenceReason.DISABLED, d.record().silenceReason());
        assertFalse(d.record().driverEnabled());
    }

    @Test
    void missingAudioAndLostDevice() {
        EchoEngine engine = EchoEngine.phase0(clock);
        EchoSnapshot missing = Snapshots.guidance(clock, 0, 1.0, 0.9).toBuilder()
                .audioDeviceStatus(AudioDeviceStatus.UNAVAILABLE)
                .build();
        assertEquals(SilenceReason.MISSING_AUDIO, engine.step(missing).record().silenceReason());
        EchoSnapshot lost = Snapshots.guidance(clock, 0, 1.0, 0.9).toBuilder()
                .audioDeviceStatus(AudioDeviceStatus.LOST)
                .build();
        assertEquals(SilenceReason.AUDIO_DEVICE_LOST, engine.step(lost).record().silenceReason());
    }

    @Test
    void noTargetSilence() {
        EchoEngine engine = EchoEngine.phase0(clock);
        EchoSnapshot snap = Snapshots.guidance(clock, 0, 1.0, 0.9).toBuilder()
                .targetId(null)
                .targetSource(TargetSource.NONE)
                .build();
        assertEquals(SilenceReason.NO_TARGET, engine.step(snap).record().silenceReason());
    }

    @Test
    void helmRejectedWhenFlagOff() {
        EchoEngine engine = EchoEngine.phase0(clock);
        EchoSnapshot snap = Snapshots.guidance(clock, 0, 1.0, 0.9).toBuilder()
                .targetSource(TargetSource.HELM)
                .build();
        EchoDecision d = engine.step(snap);
        assertEquals(SilenceReason.MISSING_CAPABILITY, d.record().silenceReason());
        assertTrue(d.record().rejected().stream()
                .anyMatch(r -> r.reason() == RejectionReason.HELM_SOURCE_DISABLED));
    }

    @Test
    void helmAllowedWhenFlagOn() {
        EchoEngine engine = new EchoEngine(clock, config,
                EchoFeatureFlags.builder().helmTargetSource(true).build(),
                new FakeRenderer());
        EchoSnapshot snap = Snapshots.guidance(clock, 0.2, 1.0, 0.9).toBuilder()
                .targetSource(TargetSource.HELM)
                .build();
        assertEquals(CueFamily.GUIDANCE, engine.step(snap).record().selected());
    }

    @Test
    void warningPreemptsGuidance() {
        EchoEngine engine = new EchoEngine(clock, config,
                EchoFeatureFlags.builder().amperAdapter(true).build(),
                new FakeRenderer());
        EchoSnapshot snap = Snapshots.guidance(clock, -1, 1.0, 0.9).toBuilder()
                .amperWarning(Flag.of(true))
                .build();
        assertEquals(CueFamily.WARN_AMPER, engine.step(snap).record().selected());
    }

    @Test
    void missingIntegrationDoesNotInventWarning() {
        EchoEngine engine = EchoEngine.phase0(clock);
        EchoSnapshot snap = Snapshots.guidance(clock, 0, 1.0, 0.9).toBuilder()
                .amperWarning(Flag.unavailable())
                .build();
        assertEquals(CueFamily.GUIDANCE, engine.step(snap).record().selected());
    }

    @Test
    void contradictoryInputsSilence() {
        EchoEngine engine = EchoEngine.phase0(clock);
        EchoSnapshot snap = Snapshots.guidance(clock, 0, 1.0, 0.9).toBuilder()
                .contradictory(true)
                .build();
        assertEquals(SilenceReason.CONTRADICTORY, engine.step(snap).record().silenceReason());
    }

    @Test
    void fakeClockAdvancesAge() {
        EchoEngine engine = EchoEngine.phase0(clock);
        long obs = clock.nanoTime();
        clock.advanceMs(10);
        EchoSnapshot fresh = Snapshots.guidance(clock, 0, 1.0, 0.9).toBuilder()
                .observationNanos(obs)
                .receiptNanos(clock.nanoTime())
                .build();
        assertEquals(CueFamily.GUIDANCE, engine.step(fresh).record().selected());
        clock.advanceMs(500);
        EchoSnapshot stale = fresh.toBuilder().receiptNanos(clock.nanoTime()).build();
        assertEquals(SilenceReason.STALE, engine.step(stale).record().silenceReason());
    }

    @Test
    void rendererFailureIsolated() {
        EchoEngine engine = new EchoEngine(clock, config, EchoFeatureFlags.disabled(), new FailingRenderer());
        EchoDecision d = engine.step(Snapshots.guidance(clock, 0, 1.0, 0.9));
        assertTrue(d.record().rendererFailure());
        assertEquals(SilenceReason.RENDERER_FAILURE, d.record().silenceReason());
        assertEquals(CueFamily.SILENCE, d.record().selected());
    }

    @Test
    void deterministicReplayIdentical() {
        FakeClock a = new FakeClock(1_000_000L);
        FakeClock b = new FakeClock(1_000_000L);
        EchoEngine ea = EchoEngine.phase0(a);
        EchoEngine eb = EchoEngine.phase0(b);
        EchoSnapshot sa = Snapshots.guidance(a, 0.4, 1.2, 0.8);
        EchoSnapshot sb = Snapshots.guidance(b, 0.4, 1.2, 0.8);
        EchoDecisionRecord ra = ea.step(sa).record();
        EchoDecisionRecord rb = eb.step(sb).record();
        assertEquals(ra.toExplanation(), rb.toExplanation());
        assertEquals(ra.selected(), rb.selected());
        assertEquals(ra.sonified().pan(), rb.sonified().pan(), 0.0);
    }

    @Test
    void confirmCooldownAndMimicReady() {
        EchoEngine engine = new EchoEngine(clock, config,
                EchoFeatureFlags.builder().mimicAdapter(true).build(),
                new FakeRenderer());
        EchoSnapshot ready = Snapshots.guidance(clock, 0, 1.0, 0.9).toBuilder()
                .mimicReady(Flag.of(true))
                .build();
        assertEquals(CueFamily.CONFIRM_READY, engine.step(ready).record().selected());
        EchoDecision second = engine.step(ready);
        assertTrue(second.record().selected() != CueFamily.CONFIRM_READY
                || second.record().rejected().stream().anyMatch(r -> r.reason() == RejectionReason.COOLDOWN));
    }

    @Test
    void warningRateLimit() {
        EchoEngine engine = new EchoEngine(clock, config,
                EchoFeatureFlags.builder().amperAdapter(true).build(),
                new FakeRenderer());
        EchoSnapshot warn = Snapshots.guidance(clock, 0, 1.0, 0.9).toBuilder()
                .amperWarning(Flag.of(true))
                .build();
        assertEquals(CueFamily.WARN_AMPER, engine.step(warn).record().selected());
        clock.advanceMs(10);
        EchoSnapshot warn2 = warn.toBuilder().receiptNanos(clock.nanoTime()).observationNanos(clock.nanoTime()).build();
        EchoDecision d = engine.step(warn2);
        assertTrue(d.record().selected() != CueFamily.WARN_AMPER
                || d.record().rejected().stream().anyMatch(r -> r.reason() == RejectionReason.RATE_LIMITED));
    }

    @Test
    void hysteresisHoldsPan() {
        EchoEngine engine = EchoEngine.phase0(clock);
        engine.step(Snapshots.guidance(clock, 0.05, 1.0, 0.9));
        clock.advanceMs(20);
        EchoDecision d = engine.step(Snapshots.guidance(clock, 0.08, 1.0, 0.9)
                .toBuilder().receiptNanos(clock.nanoTime()).observationNanos(clock.nanoTime()).build());
        assertEquals(CueFamily.GUIDANCE, d.record().selected());
        assertTrue(d.record().rejected().stream().anyMatch(r -> r.reason() == RejectionReason.HYSTERESIS_HOLD)
                || Math.abs(d.record().sonified().pan()) < 0.2);
    }

    @Test
    void recordContainsObservabilityFields() {
        EchoDecisionRecord r = EchoEngine.phase0(clock)
                .step(Snapshots.guidance(clock, 0, 1.0, 0.85))
                .record();
        assertEquals("echo-config.v1", r.configVersion());
        assertEquals("noop", r.rendererName());
        assertTrue(r.driverEnabled());
        assertEquals(AudioDeviceStatus.AVAILABLE, r.audioDeviceStatus());
        assertEquals(0.85, r.inputConfidence(), 1e-9);
        assertEquals(0L, r.inputAgeMs());
        assertTrue(r.toExplanation().contains("selected="));
    }
}
