package org.allsparks.echo.sonify;

import org.allsparks.echo.config.EchoConfig;
import org.allsparks.echo.value.Angles;

public final class PanMapper {
    private PanMapper() {
    }

    public static double panFromBearingRad(double bearingRad, EchoConfig config) {
        double wrapped = Angles.wrapRad(bearingRad);
        double sat = config.panSaturationBearingRad();
        double pan = wrapped / sat;
        if (pan > 1.0) {
            pan = 1.0;
        }
        if (pan < -1.0) {
            pan = -1.0;
        }
        return pan;
    }

    /** Equal-power stereo: pan -1 left … +1 right. */
    public static double leftGain(double pan) {
        double p = clampPan(pan);
        return Math.cos((p + 1.0) * Math.PI / 4.0);
    }

    public static double rightGain(double pan) {
        double p = clampPan(pan);
        return Math.sin((p + 1.0) * Math.PI / 4.0);
    }

    private static double clampPan(double pan) {
        if (pan < -1.0) {
            return -1.0;
        }
        if (pan > 1.0) {
            return 1.0;
        }
        return pan;
    }
}
