package org.allsparks.echo.render;

import org.allsparks.echo.sonify.SonifiedCue;

public final class FailingRenderer implements CueRenderer {
    @Override
    public String name() {
        return "failing";
    }

    @Override
    public void render(SonifiedCue cue) throws RendererException {
        throw new RendererException("intentional renderer failure");
    }

    @Override
    public void mute() {
    }
}
