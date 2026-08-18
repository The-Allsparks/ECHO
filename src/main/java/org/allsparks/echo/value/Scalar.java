package org.allsparks.echo.value;

import java.util.Objects;

/** SI quantity with explicit presence. Zero is a value, not a missing flag. */
public final class Scalar {
    private final Presence presence;
    private final double si;

    private Scalar(Presence presence, double si) {
        this.presence = Objects.requireNonNull(presence);
        this.si = si;
    }

    public static Scalar of(double si) {
        if (!Double.isFinite(si)) {
            throw new IllegalArgumentException("PRESENT scalar must be finite");
        }
        return new Scalar(Presence.PRESENT, si);
    }

    public static Scalar unknown() {
        return new Scalar(Presence.UNKNOWN, Double.NaN);
    }

    public static Scalar unavailable() {
        return new Scalar(Presence.UNAVAILABLE, Double.NaN);
    }

    public static Scalar stale() {
        return new Scalar(Presence.STALE, Double.NaN);
    }

    public Presence presence() {
        return presence;
    }

    public boolean isPresent() {
        return presence == Presence.PRESENT;
    }

    public double si() {
        if (presence != Presence.PRESENT) {
            throw new IllegalStateException("scalar is " + presence + ", not PRESENT");
        }
        return si;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Scalar)) {
            return false;
        }
        Scalar scalar = (Scalar) o;
        if (presence != scalar.presence) {
            return false;
        }
        if (presence != Presence.PRESENT) {
            return true;
        }
        return Double.compare(scalar.si, si) == 0;
    }

    @Override
    public int hashCode() {
        return presence == Presence.PRESENT ? Objects.hash(presence, si) : presence.hashCode();
    }

    @Override
    public String toString() {
        return presence == Presence.PRESENT ? "PRESENT(" + si + ")" : presence.name();
    }
}
