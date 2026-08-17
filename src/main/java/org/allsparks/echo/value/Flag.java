package org.allsparks.echo.value;

import java.util.Objects;

/** Three-plus-state boolean: unknown/unavailable/stale are not false. */
public final class Flag {
    private final Presence presence;
    private final boolean value;

    private Flag(Presence presence, boolean value) {
        this.presence = Objects.requireNonNull(presence);
        this.value = value;
    }

    public static Flag of(boolean value) {
        return new Flag(Presence.PRESENT, value);
    }

    public static Flag unknown() {
        return new Flag(Presence.UNKNOWN, false);
    }

    public static Flag unavailable() {
        return new Flag(Presence.UNAVAILABLE, false);
    }

    public static Flag stale() {
        return new Flag(Presence.STALE, false);
    }

    public Presence presence() {
        return presence;
    }

    public boolean isPresent() {
        return presence == Presence.PRESENT;
    }

    public boolean isTrue() {
        return presence == Presence.PRESENT && value;
    }

    public boolean isFalse() {
        return presence == Presence.PRESENT && !value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Flag)) {
            return false;
        }
        Flag flag = (Flag) o;
        if (presence != flag.presence) {
            return false;
        }
        return presence != Presence.PRESENT || value == flag.value;
    }

    @Override
    public int hashCode() {
        return presence == Presence.PRESENT ? Objects.hash(presence, value) : presence.hashCode();
    }

    @Override
    public String toString() {
        if (presence != Presence.PRESENT) {
            return presence.name();
        }
        return value ? "TRUE" : "FALSE";
    }
}
