package org.allsparks.echo.observe;

import org.allsparks.echo.cue.CueFamily;
import org.allsparks.echo.cue.RejectionReason;

public final class RejectedCue {
    private final CueFamily family;
    private final RejectionReason reason;

    public RejectedCue(CueFamily family, RejectionReason reason) {
        this.family = family;
        this.reason = reason;
    }

    public CueFamily family() {
        return family;
    }

    public RejectionReason reason() {
        return reason;
    }
}
