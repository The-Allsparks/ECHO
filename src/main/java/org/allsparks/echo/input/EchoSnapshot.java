package org.allsparks.echo.input;

import org.allsparks.echo.value.Flag;
import org.allsparks.echo.value.Scalar;

import java.util.Objects;

/** Immutable timestamped inputs. Unknown stays distinct from zero/false. */
public final class EchoSnapshot {
    private final long receiptNanos;
    private final boolean driverEnabled;
    private final AudioDeviceStatus audioDeviceStatus;
    private final TargetSource targetSource;
    private final String targetId;
    private final String targetCategory;
    private final Scalar bearingRad;
    private final Scalar distanceM;
    private final Scalar alignmentRad;
    private final Scalar confidence;
    private final Long observationNanos;
    private final Flag mimicReady;
    private final Flag mimicAcquire;
    private final Flag mimicComplete;
    private final Flag mimicFault;
    private final Flag amperWarning;
    private final Flag beaconWarning;
    private final boolean contradictory;
    private final boolean requiredCapabilityMissing;

    private EchoSnapshot(Builder b) {
        this.receiptNanos = b.receiptNanos;
        this.driverEnabled = b.driverEnabled;
        this.audioDeviceStatus = b.audioDeviceStatus;
        this.targetSource = b.targetSource;
        this.targetId = b.targetId;
        this.targetCategory = b.targetCategory;
        this.bearingRad = b.bearingRad;
        this.distanceM = b.distanceM;
        this.alignmentRad = b.alignmentRad;
        this.confidence = b.confidence;
        this.observationNanos = b.observationNanos;
        this.mimicReady = b.mimicReady;
        this.mimicAcquire = b.mimicAcquire;
        this.mimicComplete = b.mimicComplete;
        this.mimicFault = b.mimicFault;
        this.amperWarning = b.amperWarning;
        this.beaconWarning = b.beaconWarning;
        this.contradictory = b.contradictory;
        this.requiredCapabilityMissing = b.requiredCapabilityMissing;
    }

    public static Builder builder() {
        return new Builder();
    }

    public long receiptNanos() {
        return receiptNanos;
    }

    public boolean driverEnabled() {
        return driverEnabled;
    }

    public AudioDeviceStatus audioDeviceStatus() {
        return audioDeviceStatus;
    }

    public TargetSource targetSource() {
        return targetSource;
    }

    public String targetId() {
        return targetId;
    }

    public String targetCategory() {
        return targetCategory;
    }

    public Scalar bearingRad() {
        return bearingRad;
    }

    public Scalar distanceM() {
        return distanceM;
    }

    public Scalar alignmentRad() {
        return alignmentRad;
    }

    public Scalar confidence() {
        return confidence;
    }

    public Long observationNanos() {
        return observationNanos;
    }

    public Flag mimicReady() {
        return mimicReady;
    }

    public Flag mimicAcquire() {
        return mimicAcquire;
    }

    public Flag mimicComplete() {
        return mimicComplete;
    }

    public Flag mimicFault() {
        return mimicFault;
    }

    public Flag amperWarning() {
        return amperWarning;
    }

    public Flag beaconWarning() {
        return beaconWarning;
    }

    public boolean contradictory() {
        return contradictory;
    }

    public boolean requiredCapabilityMissing() {
        return requiredCapabilityMissing;
    }

    public static final class Builder {
        private long receiptNanos;
        private boolean driverEnabled;
        private AudioDeviceStatus audioDeviceStatus = AudioDeviceStatus.UNKNOWN;
        private TargetSource targetSource = TargetSource.NONE;
        private String targetId;
        private String targetCategory = "unspecified";
        private Scalar bearingRad = Scalar.unknown();
        private Scalar distanceM = Scalar.unknown();
        private Scalar alignmentRad = Scalar.unknown();
        private Scalar confidence = Scalar.unknown();
        private Long observationNanos;
        private Flag mimicReady = Flag.unavailable();
        private Flag mimicAcquire = Flag.unavailable();
        private Flag mimicComplete = Flag.unavailable();
        private Flag mimicFault = Flag.unavailable();
        private Flag amperWarning = Flag.unavailable();
        private Flag beaconWarning = Flag.unavailable();
        private boolean contradictory;
        private boolean requiredCapabilityMissing;

        public Builder receiptNanos(long v) {
            this.receiptNanos = v;
            return this;
        }

        public Builder driverEnabled(boolean v) {
            this.driverEnabled = v;
            return this;
        }

        public Builder audioDeviceStatus(AudioDeviceStatus v) {
            this.audioDeviceStatus = Objects.requireNonNull(v);
            return this;
        }

        public Builder targetSource(TargetSource v) {
            this.targetSource = Objects.requireNonNull(v);
            return this;
        }

        public Builder targetId(String v) {
            this.targetId = v;
            return this;
        }

        public Builder targetCategory(String v) {
            this.targetCategory = v == null ? "unspecified" : v;
            return this;
        }

        public Builder bearingRad(Scalar v) {
            this.bearingRad = Objects.requireNonNull(v);
            return this;
        }

        public Builder distanceM(Scalar v) {
            this.distanceM = Objects.requireNonNull(v);
            return this;
        }

        public Builder alignmentRad(Scalar v) {
            this.alignmentRad = Objects.requireNonNull(v);
            return this;
        }

        public Builder confidence(Scalar v) {
            this.confidence = Objects.requireNonNull(v);
            return this;
        }

        public Builder observationNanos(Long v) {
            this.observationNanos = v;
            return this;
        }

        public Builder mimicReady(Flag v) {
            this.mimicReady = Objects.requireNonNull(v);
            return this;
        }

        public Builder mimicAcquire(Flag v) {
            this.mimicAcquire = Objects.requireNonNull(v);
            return this;
        }

        public Builder mimicComplete(Flag v) {
            this.mimicComplete = Objects.requireNonNull(v);
            return this;
        }

        public Builder mimicFault(Flag v) {
            this.mimicFault = Objects.requireNonNull(v);
            return this;
        }

        public Builder amperWarning(Flag v) {
            this.amperWarning = Objects.requireNonNull(v);
            return this;
        }

        public Builder beaconWarning(Flag v) {
            this.beaconWarning = Objects.requireNonNull(v);
            return this;
        }

        public Builder contradictory(boolean v) {
            this.contradictory = v;
            return this;
        }

        public Builder requiredCapabilityMissing(boolean v) {
            this.requiredCapabilityMissing = v;
            return this;
        }

        public EchoSnapshot build() {
            return new EchoSnapshot(this);
        }
    }

    public Builder toBuilder() {
        return builder()
                .receiptNanos(receiptNanos)
                .driverEnabled(driverEnabled)
                .audioDeviceStatus(audioDeviceStatus)
                .targetSource(targetSource)
                .targetId(targetId)
                .targetCategory(targetCategory)
                .bearingRad(bearingRad)
                .distanceM(distanceM)
                .alignmentRad(alignmentRad)
                .confidence(confidence)
                .observationNanos(observationNanos)
                .mimicReady(mimicReady)
                .mimicAcquire(mimicAcquire)
                .mimicComplete(mimicComplete)
                .mimicFault(mimicFault)
                .amperWarning(amperWarning)
                .beaconWarning(beaconWarning)
                .contradictory(contradictory)
                .requiredCapabilityMissing(requiredCapabilityMissing);
    }
}
