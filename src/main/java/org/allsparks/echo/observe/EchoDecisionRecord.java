package org.allsparks.echo.observe;

import org.allsparks.echo.cue.CueFamily;
import org.allsparks.echo.cue.SilenceReason;
import org.allsparks.echo.input.AudioDeviceStatus;
import org.allsparks.echo.sonify.SonifiedCue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class EchoDecisionRecord {
    private final CueFamily selected;
    private final String cueSource;
    private final List<RejectedCue> rejected;
    private final Long inputAgeMs;
    private final Double inputConfidence;
    private final long selectionLatencyNanos;
    private final long renderLatencyNanos;
    private final int queueDepth;
    private final int droppedCues;
    private final int rateLimitedCues;
    private final AudioDeviceStatus audioDeviceStatus;
    private final boolean driverEnabled;
    private final String configVersion;
    private final String rendererName;
    private final boolean rendererFailure;
    private final SilenceReason silenceReason;
    private final SonifiedCue sonified;
    private final String explanation;

    public EchoDecisionRecord(CueFamily selected, String cueSource, List<RejectedCue> rejected,
                              Long inputAgeMs, Double inputConfidence, long selectionLatencyNanos,
                              long renderLatencyNanos, int queueDepth, int droppedCues, int rateLimitedCues,
                              AudioDeviceStatus audioDeviceStatus, boolean driverEnabled,
                              String configVersion, String rendererName, boolean rendererFailure,
                              SilenceReason silenceReason, SonifiedCue sonified, String explanation) {
        this.selected = selected;
        this.cueSource = cueSource;
        this.rejected = Collections.unmodifiableList(new ArrayList<>(rejected));
        this.inputAgeMs = inputAgeMs;
        this.inputConfidence = inputConfidence;
        this.selectionLatencyNanos = selectionLatencyNanos;
        this.renderLatencyNanos = renderLatencyNanos;
        this.queueDepth = queueDepth;
        this.droppedCues = droppedCues;
        this.rateLimitedCues = rateLimitedCues;
        this.audioDeviceStatus = audioDeviceStatus;
        this.driverEnabled = driverEnabled;
        this.configVersion = configVersion;
        this.rendererName = rendererName;
        this.rendererFailure = rendererFailure;
        this.silenceReason = silenceReason;
        this.sonified = sonified;
        this.explanation = explanation;
    }

    public CueFamily selected() {
        return selected;
    }

    public String cueSource() {
        return cueSource;
    }

    public List<RejectedCue> rejected() {
        return rejected;
    }

    public Long inputAgeMs() {
        return inputAgeMs;
    }

    public Double inputConfidence() {
        return inputConfidence;
    }

    public long selectionLatencyNanos() {
        return selectionLatencyNanos;
    }

    public long renderLatencyNanos() {
        return renderLatencyNanos;
    }

    public AudioDeviceStatus audioDeviceStatus() {
        return audioDeviceStatus;
    }

    public boolean driverEnabled() {
        return driverEnabled;
    }

    public String configVersion() {
        return configVersion;
    }

    public String rendererName() {
        return rendererName;
    }

    public boolean rendererFailure() {
        return rendererFailure;
    }

    public SilenceReason silenceReason() {
        return silenceReason;
    }

    public SonifiedCue sonified() {
        return sonified;
    }

    public String explanation() {
        return explanation;
    }

    public int queueDepth() {
        return queueDepth;
    }

    public int droppedCues() {
        return droppedCues;
    }

    public int rateLimitedCues() {
        return rateLimitedCues;
    }

    public String toExplanation() {
        StringBuilder sb = new StringBuilder();
        sb.append("selected=").append(selected);
        sb.append(" source=").append(cueSource);
        sb.append(" silence=").append(silenceReason);
        sb.append(" pan=").append(sonified.pan());
        sb.append(" pulseMs=").append(sonified.pulseIntervalMs());
        sb.append(" why=").append(explanation);
        sb.append(" rejected=");
        for (RejectedCue r : rejected) {
            sb.append(r.family()).append(':').append(r.reason()).append(',');
        }
        sb.append(" renderer=").append(rendererName);
        sb.append(" fail=").append(rendererFailure);
        sb.append(" driverEnabled=").append(driverEnabled);
        sb.append(" audio=").append(audioDeviceStatus);
        sb.append(" ageMs=").append(inputAgeMs);
        sb.append(" conf=").append(inputConfidence);
        sb.append(" selNs=").append(selectionLatencyNanos);
        sb.append(" rndNs=").append(renderLatencyNanos);
        sb.append(" q=").append(queueDepth);
        sb.append(" drop=").append(droppedCues);
        sb.append(" rateLimited=").append(rateLimitedCues);
        sb.append(" cfg=").append(configVersion);
        return sb.toString();
    }
}
