package org.allsparks.echo.adapters;

import org.allsparks.echo.input.EchoSnapshot;
import org.allsparks.echo.value.Flag;

import java.util.Objects;

/**
 * Versioned AMPER DTO. Core does not depend on the AMPER project.
 * Contract: {@link #CONTRACT} ({@code amper-echo.v0}) — a warning Flag AMPER already classified.
 * <p>
 * v0 does not carry a severity enum. ECHO does not compute brownout; it only
 * presents {@code WARN_AMPER} when {@link Flag#isTrue()} and {@code amperAdapter} is on.
 */
public final class AmperObservation {
    public static final String CONTRACT = "amper-echo.v0";
    public static final String DEFAULT_SOURCE_ID = "amper";

    public final String sourceId;
    public final Flag warning;
    public final Long observationNanos;

    public AmperObservation(Flag warning) {
        this(DEFAULT_SOURCE_ID, warning, null);
    }

    public AmperObservation(Flag warning, Long observationNanos) {
        this(DEFAULT_SOURCE_ID, warning, observationNanos);
    }

    public AmperObservation(String sourceId, Flag warning, Long observationNanos) {
        this.sourceId = (sourceId == null || sourceId.isEmpty()) ? DEFAULT_SOURCE_ID : sourceId;
        this.warning = Objects.requireNonNull(warning);
        this.observationNanos = observationNanos;
    }

    /**
     * Sets {@code amperWarning} only. Does not invent {@code targetId} or guidance.
     */
    public EchoSnapshot.Builder applyTo(EchoSnapshot.Builder builder) {
        return builder.amperWarning(warning);
    }
}
