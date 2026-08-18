package org.allsparks.echo;

/**
 * Runtime enables. I/O and integrations default off. Selection logic does not
 * require a flag; producing physical audio does.
 */
public final class EchoFeatureFlags {

    private final boolean desktopAudioPlayback;
    private final boolean androidRenderer;
    private final boolean ftcOutputAdapter;
    private final boolean vidarAdapter;
    private final boolean mimicAdapter;
    private final boolean amperAdapter;
    private final boolean beaconAdapter;
    private final boolean helmTargetSource;
    private final boolean traceExport;
    private final boolean competitionProfile;

    private EchoFeatureFlags(Builder b) {
        this.desktopAudioPlayback = b.desktopAudioPlayback;
        this.androidRenderer = b.androidRenderer;
        this.ftcOutputAdapter = b.ftcOutputAdapter;
        this.vidarAdapter = b.vidarAdapter;
        this.mimicAdapter = b.mimicAdapter;
        this.amperAdapter = b.amperAdapter;
        this.beaconAdapter = b.beaconAdapter;
        this.helmTargetSource = b.helmTargetSource;
        this.traceExport = b.traceExport;
        this.competitionProfile = b.competitionProfile;
    }

    public static EchoFeatureFlags disabled() {
        return new Builder().build();
    }

    public static Builder builder() {
        return new Builder();
    }

    public boolean desktopAudioPlayback() {
        return desktopAudioPlayback;
    }

    public boolean androidRenderer() {
        return androidRenderer;
    }

    public boolean ftcOutputAdapter() {
        return ftcOutputAdapter;
    }

    public boolean vidarAdapter() {
        return vidarAdapter;
    }

    public boolean mimicAdapter() {
        return mimicAdapter;
    }

    public boolean amperAdapter() {
        return amperAdapter;
    }

    public boolean beaconAdapter() {
        return beaconAdapter;
    }

    public boolean helmTargetSource() {
        return helmTargetSource;
    }

    public boolean traceExport() {
        return traceExport;
    }

    public boolean competitionProfile() {
        return competitionProfile;
    }

    public static final class Builder {
        private boolean desktopAudioPlayback;
        private boolean androidRenderer;
        private boolean ftcOutputAdapter;
        private boolean vidarAdapter;
        private boolean mimicAdapter;
        private boolean amperAdapter;
        private boolean beaconAdapter;
        private boolean helmTargetSource;
        private boolean traceExport;
        private boolean competitionProfile;

        public Builder desktopAudioPlayback(boolean v) {
            this.desktopAudioPlayback = v;
            return this;
        }

        public Builder mimicAdapter(boolean v) {
            this.mimicAdapter = v;
            return this;
        }

        public Builder amperAdapter(boolean v) {
            this.amperAdapter = v;
            return this;
        }

        public Builder beaconAdapter(boolean v) {
            this.beaconAdapter = v;
            return this;
        }

        public Builder helmTargetSource(boolean v) {
            this.helmTargetSource = v;
            return this;
        }

        public Builder vidarAdapter(boolean v) {
            this.vidarAdapter = v;
            return this;
        }

        public Builder traceExport(boolean v) {
            this.traceExport = v;
            return this;
        }

        public EchoFeatureFlags build() {
            if (competitionProfile) {
                throw new IllegalStateException("competitionProfile cannot be enabled: no approved match path");
            }
            if (androidRenderer || ftcOutputAdapter) {
                throw new IllegalStateException("Android/FTC output adapters are gated by Phase 4");
            }
            return new EchoFeatureFlags(this);
        }
    }
}
