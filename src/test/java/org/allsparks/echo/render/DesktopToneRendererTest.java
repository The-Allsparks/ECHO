package org.allsparks.echo.render;

import org.allsparks.echo.cue.CueFamily;
import org.allsparks.echo.cue.SilenceReason;
import org.allsparks.echo.sonify.SonifiedCue;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DesktopToneRendererTest {

    @Test
    void pcmIsStereoAndBounded() {
        SonifiedCue cue = new SonifiedCue(CueFamily.GUIDANCE, -1, 1, 0, 100, 440, 0.08, SilenceReason.NONE);
        byte[] pcm = DesktopToneRenderer.tone(cue);
        assertTrue(pcm.length > 0);
        assertEquals(0, pcm.length % 4);
        int max = 0;
        for (int i = 0; i < pcm.length; i += 2) {
            int s = (pcm[i] & 0xff) | (pcm[i + 1] << 8);
            max = Math.max(max, Math.abs(s));
        }
        assertTrue(max < 32767);
        assertTrue(max > 0);
    }

    @Test
    void disabledRendererDoesNotThrow() throws Exception {
        DesktopToneRenderer r = new DesktopToneRenderer(false);
        r.render(new SonifiedCue(CueFamily.GUIDANCE, 0, 0.7, 0.7, 100, 440, 0.08, SilenceReason.NONE));
        r.mute();
        assertEquals("desktop-muted", r.name());
    }
}
