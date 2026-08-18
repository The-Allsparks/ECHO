package org.allsparks.echo.sonify;

import org.allsparks.echo.config.EchoConfig;

public final class PulseMapper {
    private PulseMapper() {
    }

    public static double intervalMsFromDistanceM(double distanceM, EchoConfig config) {
        double d = distanceM;
        if (d < 0) {
            d = 0;
        }
        double t = (d - config.pulseNearM()) / (config.pulseFarM() - config.pulseNearM());
        if (t < 0) {
            t = 0;
        }
        if (t > 1) {
            t = 1;
        }
        return config.pulseNearMs() + t * (config.pulseFarMs() - config.pulseNearMs());
    }

    public static double intervalMsFromAlignmentRad(double absAlignRad, EchoConfig config) {
        double max = config.panSaturationBearingRad();
        double t = absAlignRad / max;
        if (t < 0) {
            t = 0;
        }
        if (t > 1) {
            t = 1;
        }
        return config.pulseNearMs() + t * (config.pulseFarMs() - config.pulseNearMs());
    }
}
