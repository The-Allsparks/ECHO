package org.allsparks.echo.value;

/** Robot-relative bearing: 0 forward, positive right, range (-π, π]. */
public final class Angles {
    public static final double TAU = 2.0 * Math.PI;

    private Angles() {
    }

    public static double wrapRad(double radians) {
        if (!Double.isFinite(radians)) {
            throw new IllegalArgumentException("bearing must be finite");
        }
        double x = radians % TAU;
        if (x <= -Math.PI) {
            x += TAU;
        }
        if (x > Math.PI) {
            x -= TAU;
        }
        return x;
    }

    public static double fromDegrees(double degrees) {
        return wrapRad(Math.toRadians(degrees));
    }
}
