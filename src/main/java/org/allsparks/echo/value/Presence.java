package org.allsparks.echo.value;

/**
 * Distinguishes missing information from a measured zero/false.
 */
public enum Presence {
    UNKNOWN,
    UNAVAILABLE,
    STALE,
    PRESENT
}
