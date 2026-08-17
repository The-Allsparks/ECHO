package org.allsparks.echo.clock;

/** Monotonic nanoseconds. Tests use {@link FakeClock}. */
public interface EchoClock {
    long nanoTime();
}
