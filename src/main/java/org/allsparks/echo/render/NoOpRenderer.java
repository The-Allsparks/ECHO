package org.allsparks.echo.render;

import org.allsparks.echo.sonify.SonifiedCue;

public final class NoOpRenderer implements CueRenderer {
    @Override
    public String name() {
        return "noop";
    }

    @Override
    public void render(SonifiedCue cue) {
    }

    @Override
    public void mute() {
    }
}
