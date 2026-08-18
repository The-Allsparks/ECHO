package org.allsparks.echo.sonify;

import org.allsparks.echo.cue.CueFamily;
import org.allsparks.echo.cue.SilenceReason;

public final class SonifiedCue {
    private final CueFamily family;
    private final double pan;
    private final double leftGain;
    private final double rightGain;
    private final double pulseIntervalMs;
    private final double pitchHz;
    private final double masterGain;
    private final SilenceReason silenceReason;

    public SonifiedCue(CueFamily family, double pan, double leftGain, double rightGain,
                       double pulseIntervalMs, double pitchHz, double masterGain,
                       SilenceReason silenceReason) {
        this.family = family;
        this.pan = pan;
        this.leftGain = leftGain;
        this.rightGain = rightGain;
        this.pulseIntervalMs = pulseIntervalMs;
        this.pitchHz = pitchHz;
        this.masterGain = masterGain;
        this.silenceReason = silenceReason;
    }

    public static SonifiedCue silence(SilenceReason reason) {
        return new SonifiedCue(CueFamily.SILENCE, 0, 0, 0, 0, 0, 0, reason);
    }

    public CueFamily family() {
        return family;
    }

    public double pan() {
        return pan;
    }

    public double leftGain() {
        return leftGain;
    }

    public double rightGain() {
        return rightGain;
    }

    public double pulseIntervalMs() {
        return pulseIntervalMs;
    }

    public double pitchHz() {
        return pitchHz;
    }

    public double masterGain() {
        return masterGain;
    }

    public SilenceReason silenceReason() {
        return silenceReason;
    }

    public boolean isSilent() {
        return family == CueFamily.SILENCE || masterGain <= 0.0;
    }
}
