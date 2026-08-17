package org.allsparks.echo.render;

import org.allsparks.echo.sonify.SonifiedCue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class FakeRenderer implements CueRenderer {
    private final List<SonifiedCue> rendered = new ArrayList<>();
    private int muteCount;

    @Override
    public String name() {
        return "fake";
    }

    @Override
    public void render(SonifiedCue cue) {
        rendered.add(cue);
    }

    @Override
    public void mute() {
        muteCount++;
    }

    public List<SonifiedCue> rendered() {
        return Collections.unmodifiableList(rendered);
    }

    public int muteCount() {
        return muteCount;
    }

    public void clear() {
        rendered.clear();
    }
}
