package org.allsparks.echo.adapters;

import org.allsparks.echo.input.EchoSnapshot;
import org.allsparks.echo.input.TargetSource;
import org.allsparks.echo.value.Angles;
import org.allsparks.echo.value.Scalar;

/**
 * Versioned ViDAR DTO. Core does not depend on the ViDAR project.
 * Contract: {@link #CONTRACT} ({@code vidar-echo.v0}) — one explicit selected target.
 * <p>
 * Frame conversion is adapter-side. This v0 DTO stores robot-relative bearing
 * only (0 forward, +right, range (−π, π]). Call {@link #wrapBearingRad(double)}
 * before constructing if the upstream angle may be outside that range.
 */
public final class VidarObservation {
    public static final String CONTRACT = "vidar-echo.v0";
    public static final String DEFAULT_SOURCE_ID = "vidar";

    public final String sourceId;
    public final String targetId;
    public final String category;
    public final Scalar bearingRad;
    public final Scalar distanceM;
    public final Scalar confidence;
    public final long observationNanos;

    public VidarObservation(String targetId, String category, Scalar bearingRad, Scalar distanceM,
                            Scalar confidence, long observationNanos) {
        this(DEFAULT_SOURCE_ID, targetId, category, bearingRad, distanceM, confidence, observationNanos);
    }

    public VidarObservation(String sourceId, String targetId, String category, Scalar bearingRad,
                            Scalar distanceM, Scalar confidence, long observationNanos) {
        this.sourceId = (sourceId == null || sourceId.isEmpty()) ? DEFAULT_SOURCE_ID : sourceId;
        this.targetId = targetId;
        this.category = category;
        this.bearingRad = bearingRad;
        this.distanceM = distanceM;
        this.confidence = confidence;
        this.observationNanos = observationNanos;
    }

    /**
     * Normalize a robot-relative +right bearing into a PRESENT scalar.
     * Does not convert camera or field frames; that stays in the ViDAR adapter.
     */
    public static Scalar wrapBearingRad(double radians) {
        return Scalar.of(Angles.wrapRad(radians));
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
