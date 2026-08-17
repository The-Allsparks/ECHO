package org.allsparks.echo.adapters;

import org.allsparks.echo.input.EchoSnapshot;
import org.allsparks.echo.input.TargetSource;
import org.allsparks.echo.value.Scalar;

/**
 * Versioned ViDAR DTO. Core does not depend on the ViDAR project.
 * Contract: vidar-echo.v0 — one explicit selected target.
 */
public final class VidarObservation {
    public static final String CONTRACT = "vidar-echo.v0";

    public final String targetId;
    public final String category;
    public final Scalar bearingRad;
    public final Scalar distanceM;
    public final Scalar confidence;
    public final long observationNanos;

    public VidarObservation(String targetId, String category, Scalar bearingRad, Scalar distanceM,
                            Scalar confidence, long observationNanos) {
        this.targetId = targetId;
        this.category = category;
        this.bearingRad = bearingRad;
        this.distanceM = distanceM;
        this.confidence = confidence;
        this.observationNanos = observationNanos;
    }

    public EchoSnapshot.Builder applyTo(EchoSnapshot.Builder builder) {
        return builder
                .targetSource(TargetSource.BOUNDED_ADAPTER)
                .targetId(targetId)
                .targetCategory(category)
                .bearingRad(bearingRad)
                .distanceM(distanceM)
                .confidence(confidence)
                .observationNanos(observationNanos);
    }
}
