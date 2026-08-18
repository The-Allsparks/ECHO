package org.allsparks.echo.select;

import org.allsparks.echo.cue.SilenceReason;
import org.allsparks.echo.observe.RejectedCue;
import org.allsparks.echo.sonify.SonifiedCue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class SelectionResult {
    private final SonifiedCue cue;
    private final List<RejectedCue> rejected;
    private final String explanation;

    public SelectionResult(SonifiedCue cue, List<RejectedCue> rejected, String explanation) {
        this.cue = cue;
        this.rejected = Collections.unmodifiableList(new ArrayList<>(rejected));
        this.explanation = explanation;
    }

    public static SelectionResult silence(SilenceReason reason, List<RejectedCue> rejected, String explanation) {
        return new SelectionResult(SonifiedCue.silence(reason), rejected, explanation);
    }

    public static SelectionResult cue(SonifiedCue cue, List<RejectedCue> rejected, String explanation) {
        return new SelectionResult(cue, rejected, explanation);
    }

    public SonifiedCue cue() {
        return cue;
    }

    public List<RejectedCue> rejected() {
        return rejected;
    }

    public String explanation() {
        return explanation;
    }
}
