package org.allsparks.echo.adapters;

import org.allsparks.echo.EchoEngine;
import org.allsparks.echo.clock.FakeClock;
import org.allsparks.echo.cue.CueFamily;
import org.allsparks.echo.input.AudioDeviceStatus;
import org.allsparks.echo.input.EchoSnapshot;
import org.allsparks.echo.value.Scalar;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class VidarObservationTest {

    @Test
    void adapterFillsExplicitTarget() {
        FakeClock clock = new FakeClock();
        VidarObservation obs = new VidarObservation(
                "tag-3", "sample", Scalar.of(0.5), Scalar.of(1.1), Scalar.of(0.9), clock.nanoTime());
        EchoSnapshot snap = obs.applyTo(EchoSnapshot.builder()
                        .receiptNanos(clock.nanoTime())
                        .driverEnabled(true)
                        .audioDeviceStatus(AudioDeviceStatus.AVAILABLE))
                .build();
        assertEquals("tag-3", snap.targetId());
        assertEquals(CueFamily.GUIDANCE, EchoEngine.phase0(clock).step(snap).record().selected());
    }
}
