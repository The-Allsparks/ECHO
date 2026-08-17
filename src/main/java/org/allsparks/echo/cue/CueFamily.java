package org.allsparks.echo.cue;

public enum CueFamily {
    SILENCE(0),
    GUIDANCE(1),
    CONFIRM_READY(2),
    CONFIRM_COMPLETE(2),
    CONFIRM_ACQUIRE(3),
    CONFIRM_ALIGN(3),
    WARN_BEACON(4),
    WARN_MIMIC(5),
    WARN_AMPER(6);

    private final int priority;

    CueFamily(int priority) {
        this.priority = priority;
    }

    /** Higher number wins. */
    public int priority() {
        return priority;
    }

    public boolean isWarning() {
        return this == WARN_AMPER || this == WARN_MIMIC || this == WARN_BEACON;
    }

    public boolean isConfirm() {
        return this == CONFIRM_READY || this == CONFIRM_COMPLETE
                || this == CONFIRM_ACQUIRE || this == CONFIRM_ALIGN;
    }
}
