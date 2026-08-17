package org.allsparks.echo.select;

import org.allsparks.echo.EchoFeatureFlags;
import org.allsparks.echo.config.EchoConfig;
import org.allsparks.echo.cue.CueFamily;
import org.allsparks.echo.cue.RejectionReason;
import org.allsparks.echo.cue.SilenceReason;
import org.allsparks.echo.input.AudioDeviceStatus;
import org.allsparks.echo.input.EchoSnapshot;
import org.allsparks.echo.input.TargetSource;
import org.allsparks.echo.observe.RejectedCue;
import org.allsparks.echo.sonify.PanMapper;
import org.allsparks.echo.sonify.PitchMapper;
import org.allsparks.echo.sonify.PulseMapper;
import org.allsparks.echo.sonify.SonifiedCue;
import org.allsparks.echo.value.Presence;
import org.allsparks.echo.value.Scalar;

import java.util.ArrayList;
import java.util.List;

public final class CueSelector {
    private CueFamily lastFamily = CueFamily.SILENCE;
    private double lastPan;
    private long lastChangeNanos;
    private long lastWarnNanos = Long.MIN_VALUE / 4;
    private long lastConfirmNanos = Long.MIN_VALUE / 4;
    private int warnsInWindow;
    private long warnWindowStartNanos;

    public SelectionResult select(EchoSnapshot snap, EchoConfig config, EchoFeatureFlags flags) {
        List<RejectedCue> rejected = new ArrayList<>();
        long now = snap.receiptNanos();

        if (!config.valid()) {
            return SelectionResult.silence(SilenceReason.INVALID_CONFIG, rejected, "invalid configuration");
        }
        if (!snap.driverEnabled()) {
            lastFamily = CueFamily.SILENCE;
            return SelectionResult.silence(SilenceReason.DISABLED, rejected, "driver disabled");
        }
        if (snap.audioDeviceStatus() == AudioDeviceStatus.LOST) {
            lastFamily = CueFamily.SILENCE;
            return SelectionResult.silence(SilenceReason.AUDIO_DEVICE_LOST, rejected, "audio device lost");
        }
        if (snap.audioDeviceStatus() != AudioDeviceStatus.AVAILABLE) {
            lastFamily = CueFamily.SILENCE;
            return SelectionResult.silence(SilenceReason.MISSING_AUDIO, rejected, "audio device not available");
        }
        if (snap.contradictory()) {
            lastFamily = CueFamily.SILENCE;
            return SelectionResult.silence(SilenceReason.CONTRADICTORY, rejected, "contradictory inputs");
        }
        if (snap.requiredCapabilityMissing()) {
            lastFamily = CueFamily.SILENCE;
            return SelectionResult.silence(SilenceReason.MISSING_CAPABILITY, rejected, "required capability missing");
        }

        CueFamily best = CueFamily.SILENCE;
        SilenceReason guidanceSilence = SilenceReason.NO_USEFUL_ACTION;
        String why = "no useful driver action";

        if (flags.amperAdapter() && snap.amperWarning().isTrue()) {
            if (warningAllowed(now, config)) {
                best = CueFamily.WARN_AMPER;
                why = "AMPER warning preempts guidance";
            } else {
                rejected.add(new RejectedCue(CueFamily.WARN_AMPER, RejectionReason.RATE_LIMITED));
            }
        } else if (snap.amperWarning().isTrue()) {
            rejected.add(new RejectedCue(CueFamily.WARN_AMPER, RejectionReason.FLAG_DISABLED));
        }

        if (best.priority() < CueFamily.WARN_MIMIC.priority()
                && flags.mimicAdapter() && snap.mimicFault().isTrue()) {
            if (warningAllowed(now, config)) {
                best = CueFamily.WARN_MIMIC;
                why = "MIMIC fault preempts guidance";
            } else {
                rejected.add(new RejectedCue(CueFamily.WARN_MIMIC, RejectionReason.RATE_LIMITED));
            }
        } else if (snap.mimicFault().isTrue() && !flags.mimicAdapter()) {
            rejected.add(new RejectedCue(CueFamily.WARN_MIMIC, RejectionReason.FLAG_DISABLED));
        }

        if (best.priority() < CueFamily.WARN_BEACON.priority()
                && flags.beaconAdapter() && snap.beaconWarning().isTrue()) {
            if (warningAllowed(now, config)) {
                best = CueFamily.WARN_BEACON;
                why = "BEACON warning preempts guidance";
            } else {
                rejected.add(new RejectedCue(CueFamily.WARN_BEACON, RejectionReason.RATE_LIMITED));
            }
        } else if (snap.beaconWarning().isTrue() && !flags.beaconAdapter()) {
            rejected.add(new RejectedCue(CueFamily.WARN_BEACON, RejectionReason.FLAG_DISABLED));
        }

        if (best == CueFamily.SILENCE && flags.mimicAdapter()) {
            CueFamily confirm = confirmFrom(snap, now, config, rejected);
            if (confirm != CueFamily.SILENCE) {
                best = confirm;
                why = "MIMIC confirmation";
            }
        }

        if (best.isWarning()) {
            noteWarn(now);
            lastFamily = best;
            lastChangeNanos = now;
            return SelectionResult.cue(sonifyDiscrete(best, config), rejected, why);
        }
        if (best.isConfirm()) {
            lastConfirmNanos = now;
            lastFamily = best;
            lastChangeNanos = now;
            return SelectionResult.cue(sonifyDiscrete(best, config), rejected, why);
        }

        GuidanceEval g = evaluateGuidance(snap, config, flags, rejected);
        if (g.cue != null) {
            double pan = g.cue.pan();
            if (lastFamily == CueFamily.GUIDANCE
                    && Math.abs(pan - lastPan) < config.hysteresisPan()
                    && (now - lastChangeNanos) <= config.commitmentWindowMs() * 1_000_000L) {
                pan = lastPan;
                rejected.add(new RejectedCue(CueFamily.GUIDANCE, RejectionReason.HYSTERESIS_HOLD));
            }
            SonifiedCue held = new SonifiedCue(
                    CueFamily.GUIDANCE, pan,
                    PanMapper.leftGain(pan), PanMapper.rightGain(pan),
                    g.cue.pulseIntervalMs(), g.cue.pitchHz(), g.cue.masterGain(),
                    SilenceReason.NONE);
            if (lastFamily != CueFamily.GUIDANCE) {
                lastChangeNanos = now;
            }
            lastFamily = CueFamily.GUIDANCE;
            lastPan = pan;
            return SelectionResult.cue(held, rejected, g.why);
        }

        lastFamily = CueFamily.SILENCE;
        return SelectionResult.silence(g.reason, rejected, g.why);
    }

    private CueFamily confirmFrom(EchoSnapshot snap, long now, EchoConfig config, List<RejectedCue> rejected) {
        if (now - lastConfirmNanos < config.confirmCooldownMs() * 1_000_000L) {
            if (snap.mimicAcquire().isTrue() || snap.mimicComplete().isTrue()
                    || snap.mimicReady().isTrue()) {
                rejected.add(new RejectedCue(CueFamily.CONFIRM_ACQUIRE, RejectionReason.COOLDOWN));
            }
            return CueFamily.SILENCE;
        }
        if (snap.mimicFault().isTrue()) {
            return CueFamily.SILENCE;
        }
        if (snap.mimicAcquire().isTrue()) {
            return CueFamily.CONFIRM_ACQUIRE;
        }
        if (snap.mimicComplete().isTrue()) {
            return CueFamily.CONFIRM_COMPLETE;
        }
        if (snap.mimicReady().isTrue()) {
            return CueFamily.CONFIRM_READY;
        }
        return CueFamily.SILENCE;
    }

    private boolean warningAllowed(long now, EchoConfig config) {
        if (now - lastWarnNanos < config.warnCooldownMs() * 1_000_000L) {
            return false;
        }
        long window = 1_000_000_000L;
        if (now - warnWindowStartNanos > window) {
            warnWindowStartNanos = now;
            warnsInWindow = 0;
        }
        double max = config.warnRateLimitPerSec();
        if (warnsInWindow >= Math.max(1.0, max)) {
            return false;
        }
        return true;
    }

    private void noteWarn(long now) {
        lastWarnNanos = now;
        warnsInWindow++;
    }

    private GuidanceEval evaluateGuidance(EchoSnapshot snap, EchoConfig config, EchoFeatureFlags flags,
                                          List<RejectedCue> rejected) {
        if (snap.targetSource() == TargetSource.HELM && !flags.helmTargetSource()) {
            rejected.add(new RejectedCue(CueFamily.GUIDANCE, RejectionReason.HELM_SOURCE_DISABLED));
            return GuidanceEval.silence(SilenceReason.MISSING_CAPABILITY, "HELM target source disabled");
        }
        if (snap.targetSource() == TargetSource.NONE || snap.targetId() == null || snap.targetId().isEmpty()) {
            rejected.add(new RejectedCue(CueFamily.GUIDANCE, RejectionReason.NO_EXPLICIT_TARGET));
            return GuidanceEval.silence(SilenceReason.NO_TARGET, "no explicit selected target");
        }
        Scalar bearing = snap.bearingRad();
        if (bearing.presence() == Presence.STALE || snap.confidence().presence() == Presence.STALE
                || snap.distanceM().presence() == Presence.STALE) {
            return GuidanceEval.silence(SilenceReason.STALE, "stale observation");
        }
        if (!bearing.isPresent()) {
            return GuidanceEval.silence(SilenceReason.UNKNOWN_INPUT, "bearing not PRESENT");
        }
        if (!snap.confidence().isPresent()) {
            return GuidanceEval.silence(SilenceReason.UNKNOWN_INPUT, "confidence not PRESENT");
        }
        if (snap.confidence().si() < config.minConfidence()) {
            return GuidanceEval.silence(SilenceReason.LOW_CONFIDENCE, "confidence below threshold");
        }
        if (snap.observationNanos() == null) {
            return GuidanceEval.silence(SilenceReason.UNKNOWN_INPUT, "observation timestamp unknown");
        }
        long ageMs = (snap.receiptNanos() - snap.observationNanos()) / 1_000_000L;
        if (ageMs < 0) {
            return GuidanceEval.silence(SilenceReason.CONTRADICTORY, "observation after receipt");
        }
        if (ageMs > config.maxObservationAgeMs()) {
            return GuidanceEval.silence(SilenceReason.STALE, "observation age " + ageMs + " ms");
        }

        double pan = PanMapper.panFromBearingRad(bearing.si(), config);
        double pulse;
        if (config.alignmentPulse()) {
            if (!snap.alignmentRad().isPresent()) {
                return GuidanceEval.silence(SilenceReason.UNKNOWN_INPUT, "alignment required by profile");
            }
            pulse = PulseMapper.intervalMsFromAlignmentRad(Math.abs(snap.alignmentRad().si()), config);
        } else {
            if (!snap.distanceM().isPresent()) {
                return GuidanceEval.silence(SilenceReason.UNKNOWN_INPUT, "distance not PRESENT");
            }
            pulse = PulseMapper.intervalMsFromDistanceM(snap.distanceM().si(), config);
        }
        double pitch = PitchMapper.hz(config, 0.5);
        double gain = Math.min(config.defaultGain(), config.maxGain());
        SonifiedCue cue = new SonifiedCue(
                CueFamily.GUIDANCE, pan,
                PanMapper.leftGain(pan), PanMapper.rightGain(pan),
                pulse, pitch, gain, SilenceReason.NONE);
        return new GuidanceEval(cue, SilenceReason.NONE, "explicit target " + snap.targetId());
    }

    private static SonifiedCue sonifyDiscrete(CueFamily family, EchoConfig config) {
        double gain = Math.min(config.defaultGain(), config.maxGain());
        return new SonifiedCue(family, 0, PanMapper.leftGain(0), PanMapper.rightGain(0),
                0, config.pitchHz(), gain, SilenceReason.NONE);
    }

    private static final class GuidanceEval {
        final SonifiedCue cue;
        final SilenceReason reason;
        final String why;

        GuidanceEval(SonifiedCue cue, SilenceReason reason, String why) {
            this.cue = cue;
            this.reason = reason;
            this.why = why;
        }

        static GuidanceEval silence(SilenceReason reason, String why) {
            return new GuidanceEval(null, reason, why);
        }
    }
}
