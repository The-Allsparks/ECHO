package org.allsparks.echo.clock;

public final class FakeClock implements EchoClock {
    private long nanos;

    public FakeClock() {
        this(0L);
    }

    public FakeClock(long nanos) {
        this.nanos = nanos;
    }

    @Override
    public long nanoTime() {
        return nanos;
    }

    public void setNanos(long nanos) {
        this.nanos = nanos;
    }

    public void advanceMs(long ms) {
        if (ms < 0) {
            throw new IllegalArgumentException("advance must be >= 0");
        }
        this.nanos += ms * 1_000_000L;
    }
}
