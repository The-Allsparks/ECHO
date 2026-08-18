package org.allsparks.echo.sonify;

import org.allsparks.echo.config.EchoConfig;

public final class PitchMapper {
    private PitchMapper() {
    }

    public static double hz(EchoConfig config, double normalized) {
        if (!config.pitchEnabled()) {
            return config.pitchHz();
        }
        double t = normalized;
        if (t < 0) {
            t = 0;
        }
        if (t > 1) {
            t = 1;
        }
        double hz = config.pitchMinHz() + t * (config.pitchMaxHz() - config.pitchMinHz());
        if (hz < config.pitchMinHz()) {
            hz = config.pitchMinHz();
        }
        if (hz > config.pitchMaxHz()) {
            hz = config.pitchMaxHz();
        }
        return hz;
    }
}
