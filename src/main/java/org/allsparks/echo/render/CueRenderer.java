package org.allsparks.echo.render;

import org.allsparks.echo.sonify.SonifiedCue;

public interface CueRenderer {
    String name();

    void render(SonifiedCue cue) throws RendererException;

    void mute();
}
