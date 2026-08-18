package org.allsparks.echo.training;

import org.allsparks.echo.clock.FakeClock;
import org.allsparks.echo.input.AudioDeviceStatus;
import org.allsparks.echo.input.EchoSnapshot;
import org.allsparks.echo.input.TargetSource;
import org.allsparks.echo.value.Scalar;

public final class Snapshots {
    private Snapshots() {
    }

    public static EchoSnapshot guidance(FakeClock clock, double bearingRad, double distanceM, double confidence) {
        long now = clock.nanoTime();
        return EchoSnapshot.builder()
                .receiptNanos(now)
                .observationNanos(now)
                .driverEnabled(true)
                .audioDeviceStatus(AudioDeviceStatus.AVAILABLE)
                .targetSource(TargetSource.DRIVER)
                .targetId("driver-selected")
                .targetCategory("sample")
                .bearingRad(Scalar.of(bearingRad))
                .distanceM(Scalar.of(distanceM))
                .confidence(Scalar.of(confidence))
                .build();
    }
}
