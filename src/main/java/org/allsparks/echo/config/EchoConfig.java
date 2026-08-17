package org.allsparks.echo.config;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class EchoConfig {
    public static final String SCHEMA_V1 = "echo-config.v1";

    private final String schemaVersion;
    private final double minConfidence;
    private final long maxObservationAgeMs;
    private final double panSaturationBearingRad;
    private final double pulseNearM;
    private final double pulseFarM;
    private final double pulseNearMs;
    private final double pulseFarMs;
    private final boolean alignmentPulse;
    private final boolean pitchEnabled;
    private final double pitchHz;
    private final double pitchMinHz;
    private final double pitchMaxHz;
    private final double defaultGain;
    private final double maxGain;
    private final double hysteresisPan;
    private final long commitmentWindowMs;
    private final long confirmCooldownMs;
    private final long warnCooldownMs;
    private final double warnRateLimitPerSec;
    private final List<String> errors;

    private EchoConfig(String schemaVersion, double minConfidence, long maxObservationAgeMs,
                       double panSaturationBearingRad, double pulseNearM, double pulseFarM,
                       double pulseNearMs, double pulseFarMs, boolean alignmentPulse,
                       boolean pitchEnabled, double pitchHz, double pitchMinHz, double pitchMaxHz,
                       double defaultGain, double maxGain, double hysteresisPan,
                       long commitmentWindowMs, long confirmCooldownMs, long warnCooldownMs,
                       double warnRateLimitPerSec, List<String> errors) {
        this.schemaVersion = schemaVersion;
        this.minConfidence = minConfidence;
        this.maxObservationAgeMs = maxObservationAgeMs;
        this.panSaturationBearingRad = panSaturationBearingRad;
        this.pulseNearM = pulseNearM;
        this.pulseFarM = pulseFarM;
        this.pulseNearMs = pulseNearMs;
        this.pulseFarMs = pulseFarMs;
        this.alignmentPulse = alignmentPulse;
        this.pitchEnabled = pitchEnabled;
        this.pitchHz = pitchHz;
        this.pitchMinHz = pitchMinHz;
        this.pitchMaxHz = pitchMaxHz;
        this.defaultGain = defaultGain;
        this.maxGain = maxGain;
        this.hysteresisPan = hysteresisPan;
        this.commitmentWindowMs = commitmentWindowMs;
        this.confirmCooldownMs = confirmCooldownMs;
        this.warnCooldownMs = warnCooldownMs;
        this.warnRateLimitPerSec = warnRateLimitPerSec;
        this.errors = Collections.unmodifiableList(new ArrayList<>(errors));
    }

    public static EchoConfig defaults() {
        return new EchoConfig(
                SCHEMA_V1, 0.60, 250L, Math.PI / 2.0,
                0.25, 2.50, 90, 700, false,
                false, 440.0, 350.0, 520.0,
                0.08, 0.20, 0.08,
                150L, 400L, 1500L, 0.5,
                Collections.emptyList());
    }

    public static EchoConfig parseJson(String json) {
        String version = stringField(json, "schemaVersion");
        List<String> errors = new ArrayList<>();
        if (!SCHEMA_V1.equals(version)) {
            errors.add("unsupported schemaVersion: " + version);
        }
        EchoConfig c = new EchoConfig(
                version == null ? "" : version,
                numberField(json, "minConfidence", 0.60),
                (long) numberField(json, "maxObservationAgeMs", 250),
                numberField(json, "panSaturationBearingRad", Math.PI / 2),
                numberField(json, "pulseNearM", 0.25),
                numberField(json, "pulseFarM", 2.50),
                numberField(json, "pulseNearMs", 90),
                numberField(json, "pulseFarMs", 700),
                booleanField(json, "alignmentPulse", false),
                booleanField(json, "pitchEnabled", false),
                numberField(json, "pitchHz", 440),
                numberField(json, "pitchMinHz", 350),
                numberField(json, "pitchMaxHz", 520),
                numberField(json, "defaultGain", 0.08),
                numberField(json, "maxGain", 0.20),
                numberField(json, "hysteresisPan", 0.08),
                (long) numberField(json, "commitmentWindowMs", 150),
                (long) numberField(json, "confirmCooldownMs", 400),
                (long) numberField(json, "warnCooldownMs", 1500),
                numberField(json, "warnRateLimitPerSec", 0.5),
                errors);
        errors.addAll(c.computeErrors());
        return new EchoConfig(c.schemaVersion, c.minConfidence, c.maxObservationAgeMs,
                c.panSaturationBearingRad, c.pulseNearM, c.pulseFarM, c.pulseNearMs, c.pulseFarMs,
                c.alignmentPulse, c.pitchEnabled, c.pitchHz, c.pitchMinHz, c.pitchMaxHz,
                c.defaultGain, c.maxGain, c.hysteresisPan, c.commitmentWindowMs,
                c.confirmCooldownMs, c.warnCooldownMs, c.warnRateLimitPerSec, errors);
    }

    public EchoConfig withErrors(List<String> extra) {
        List<String> all = new ArrayList<>(errors);
        all.addAll(extra);
        return new EchoConfig(schemaVersion, minConfidence, maxObservationAgeMs,
                panSaturationBearingRad, pulseNearM, pulseFarM, pulseNearMs, pulseFarMs,
                alignmentPulse, pitchEnabled, pitchHz, pitchMinHz, pitchMaxHz,
                defaultGain, maxGain, hysteresisPan, commitmentWindowMs,
                confirmCooldownMs, warnCooldownMs, warnRateLimitPerSec, all);
    }

    public List<String> validate() {
        List<String> e = new ArrayList<>(errors);
        e.addAll(computeErrors());
        return e;
    }

    public boolean valid() {
        return validate().isEmpty();
    }

    private List<String> computeErrors() {
        List<String> e = new ArrayList<>();
        if (!SCHEMA_V1.equals(schemaVersion)) {
            e.add("schemaVersion must be " + SCHEMA_V1);
        }
        if (minConfidence < 0 || minConfidence > 1) {
            e.add("minConfidence out of range");
        }
        if (maxObservationAgeMs <= 0) {
            e.add("maxObservationAgeMs must be positive");
        }
        if (pulseFarM <= pulseNearM) {
            e.add("pulseFarM must exceed pulseNearM");
        }
        if (pulseFarMs <= pulseNearMs) {
            e.add("pulseFarMs must exceed pulseNearMs");
        }
        if (defaultGain < 0 || maxGain < defaultGain || maxGain > 1) {
            e.add("gain bounds invalid");
        }
        if (pitchMinHz >= pitchMaxHz) {
            e.add("pitch bounds invalid");
        }
        if (hysteresisPan < 0 || hysteresisPan > 1) {
            e.add("hysteresisPan out of range");
        }
        return e;
    }

    public String schemaVersion() {
        return schemaVersion;
    }

    public double minConfidence() {
        return minConfidence;
    }

    public long maxObservationAgeMs() {
        return maxObservationAgeMs;
    }

    public double panSaturationBearingRad() {
        return panSaturationBearingRad;
    }

    public double pulseNearM() {
        return pulseNearM;
    }

    public double pulseFarM() {
        return pulseFarM;
    }

    public double pulseNearMs() {
        return pulseNearMs;
    }

    public double pulseFarMs() {
        return pulseFarMs;
    }

    public boolean alignmentPulse() {
        return alignmentPulse;
    }

    public boolean pitchEnabled() {
        return pitchEnabled;
    }

    public double pitchHz() {
        return pitchHz;
    }

    public double pitchMinHz() {
        return pitchMinHz;
    }

    public double pitchMaxHz() {
        return pitchMaxHz;
    }

    public double defaultGain() {
        return defaultGain;
    }

    public double maxGain() {
        return maxGain;
    }

    public double hysteresisPan() {
        return hysteresisPan;
    }

    public long commitmentWindowMs() {
        return commitmentWindowMs;
    }

    public long confirmCooldownMs() {
        return confirmCooldownMs;
    }

    public long warnCooldownMs() {
        return warnCooldownMs;
    }

    public double warnRateLimitPerSec() {
        return warnRateLimitPerSec;
    }

    private static String stringField(String json, String key) {
        Matcher m = Pattern.compile("\"" + key + "\"\\s*:\\s*\"([^\"]*)\"").matcher(json);
        return m.find() ? m.group(1) : null;
    }

    private static double numberField(String json, String key, double dflt) {
        Matcher m = Pattern.compile("\"" + key + "\"\\s*:\\s*(-?\\d+(?:\\.\\d+)?)").matcher(json);
        return m.find() ? Double.parseDouble(m.group(1)) : dflt;
    }

    private static boolean booleanField(String json, String key, boolean dflt) {
        Matcher m = Pattern.compile("\"" + key + "\"\\s*:\\s*(true|false)").matcher(json);
        return m.find() ? Boolean.parseBoolean(m.group(1)) : dflt;
    }
}
