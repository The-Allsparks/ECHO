package org.allsparks.echo.clock;

public final class SystemNanoClock implements EchoClock {
    @Override
    public long nanoTime() {
        return System.nanoTime();
    }
}
