package org.allsparks.echo.render;

import org.allsparks.echo.sonify.SonifiedCue;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;

/**
 * Desktop training renderer. Does not open an audio line unless {@code enabled}.
 * Not a Driver Hub or competition path.
 */
public final class DesktopToneRenderer implements CueRenderer {
    private static final int SAMPLE_RATE = 22050;
    private final boolean enabled;
    private SourceDataLine line;

    public DesktopToneRenderer(boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public String name() {
        return enabled ? "desktop-tone" : "desktop-muted";
    }

    @Override
    public synchronized void render(SonifiedCue cue) throws RendererException {
        if (!enabled || cue.isSilent()) {
            return;
        }
        try {
            ensureLine();
            byte[] pcm = tone(cue);
            line.write(pcm, 0, pcm.length);
        } catch (LineUnavailableException e) {
            throw new RendererException("desktop line unavailable", e);
        }
    }

    @Override
    public synchronized void mute() {
        if (line != null) {
            line.flush();
        }
    }

    private void ensureLine() throws LineUnavailableException {
        if (line != null) {
            return;
        }
        AudioFormat format = new AudioFormat(SAMPLE_RATE, 16, 2, true, false);
        line = AudioSystem.getSourceDataLine(format);
        line.open(format, SAMPLE_RATE / 5);
        line.start();
    }

    static byte[] tone(SonifiedCue cue) {
        int durationMs = 40;
        int n = SAMPLE_RATE * durationMs / 1000;
        byte[] out = new byte[n * 4];
        double amp = Math.min(cue.masterGain(), 0.20);
        double left = amp * cue.leftGain();
        double right = amp * cue.rightGain();
        double hz = cue.pitchHz() <= 0 ? 440.0 : cue.pitchHz();
        for (int i = 0; i < n; i++) {
            double env = envelope(i, n);
            double s = Math.sin(2 * Math.PI * hz * i / SAMPLE_RATE) * env;
            writeS16(out, i * 4, s * left);
            writeS16(out, i * 4 + 2, s * right);
        }
        return out;
    }

    private static double envelope(int i, int n) {
        int fade = Math.max(1, n / 8);
        if (i < fade) {
            return (double) i / fade;
        }
        if (i > n - fade) {
            return (double) (n - i) / fade;
        }
        return 1.0;
    }

    private static void writeS16(byte[] out, int offset, double sample) {
        int v = (int) Math.round(Math.max(-1.0, Math.min(1.0, sample)) * 32767.0);
        out[offset] = (byte) (v & 0xff);
        out[offset + 1] = (byte) ((v >> 8) & 0xff);
    }
}
