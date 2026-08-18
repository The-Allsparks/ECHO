package org.allsparks.echo.replay;

import org.allsparks.echo.EchoEngine;
import org.allsparks.echo.EchoFeatureFlags;
import org.allsparks.echo.adapters.VidarObservation;
import org.allsparks.echo.clock.FakeClock;
import org.allsparks.echo.config.EchoConfig;
import org.allsparks.echo.input.AudioDeviceStatus;
import org.allsparks.echo.input.EchoSnapshot;
import org.allsparks.echo.input.TargetSource;
import org.allsparks.echo.observe.EchoDecisionRecord;
import org.allsparks.echo.observe.TraceExporter;
import org.allsparks.echo.render.NoOpRenderer;
import org.allsparks.echo.value.Presence;
import org.allsparks.echo.value.Scalar;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * File-based deterministic replay. Parses {@code echo-replay.v0} with JDK-only
 * regex helpers in the same style as {@link EchoConfig#parseJson(String)}.
 * No FTC SDK, ViDAR, or TRACE JARs.
 */
public final class ReplayRunner {
    public static final String SCHEMA = "echo-replay.v0";

    private ReplayRunner() {
    }

    public static ReplayResult run(Path path) throws IOException {
        String json = new String(Files.readAllBytes(path), StandardCharsets.UTF_8);
        return run(json);
    }

    public static ReplayResult run(String json) {
        String version = stringField(json, "schemaVersion");
        if (!SCHEMA.equals(version)) {
            throw new IllegalArgumentException("unsupported schemaVersion: " + version);
        }
        long clockStart = (long) numberField(json, "clockStartNanos", 0);
        EchoFeatureFlags flags = EchoFeatureFlags.builder()
                .vidarAdapter(booleanField(json, "vidarAdapter", false))
                .helmTargetSource(booleanField(json, "helmTargetSource", false))
                .traceExport(booleanField(json, "traceExport", false))
                .amperAdapter(booleanField(json, "amperAdapter", false))
                .mimicAdapter(booleanField(json, "mimicAdapter", false))
                .beaconAdapter(booleanField(json, "beaconAdapter", false))
                .build();
        EchoConfig config = EchoConfig.defaults();
        FakeClock clock = new FakeClock(clockStart);
        StringBuilder exported = new StringBuilder();
        TraceExporter exporter = flags.traceExport() ? TraceExporter.jsonl(exported) : TraceExporter.noop();
        EchoEngine engine = new EchoEngine(clock, config, flags, new NoOpRenderer(), exporter);
        List<EchoSnapshot> snapshots = new ArrayList<>();
        List<EchoDecisionRecord> records = new ArrayList<>();
        for (String step : objectArray(json, "steps")) {
            EchoSnapshot snap = snapshotFromStep(step);
            snapshots.add(snap);
            records.add(engine.step(snap).record());
        }
        return new ReplayResult(version, flags, snapshots, records, exported.toString());
    }

    private static EchoSnapshot snapshotFromStep(String step) {
        TargetSource targetSource = parseTargetSource(stringField(step, "targetSource"));
        long receipt = (long) numberField(step, "receiptNanos", 0);
        long observation = (long) numberField(step, "observationNanos", receipt);
        boolean driverEnabled = booleanField(step, "driverEnabled", true);
        AudioDeviceStatus audio = parseAudio(stringField(step, "audioDeviceStatus"));
        String targetId = stringField(step, "targetId");
        String category = stringField(step, "category");
        if (category == null) {
            category = "unspecified";
        }
        Scalar bearing = scalarField(step, "bearingRad", "bearingPresence");
        Scalar distance = scalarField(step, "distanceM", "distancePresence");
        Scalar confidence = scalarField(step, "confidence", "confidencePresence");
        EchoSnapshot.Builder builder = EchoSnapshot.builder()
                .receiptNanos(receipt)
                .driverEnabled(driverEnabled)
                .audioDeviceStatus(audio);
        if (targetSource == TargetSource.BOUNDED_ADAPTER) {
            String sourceId = stringField(step, "sourceId");
            VidarObservation obs = new VidarObservation(
                    sourceId, targetId, category, bearing, distance, confidence, observation);
            return obs.applyTo(builder).build();
        }
        return builder
                .targetSource(targetSource)
                .targetId(targetId)
                .targetCategory(category)
                .bearingRad(bearing)
                .distanceM(distance)
                .confidence(confidence)
                .observationNanos(observation)
                .build();
    }

    private static TargetSource parseTargetSource(String raw) {
        if (raw == null || raw.isEmpty()) {
            return TargetSource.BOUNDED_ADAPTER;
        }
        return TargetSource.valueOf(raw);
    }

    private static AudioDeviceStatus parseAudio(String raw) {
        if (raw == null || raw.isEmpty()) {
            return AudioDeviceStatus.AVAILABLE;
        }
        return AudioDeviceStatus.valueOf(raw);
    }

    private static Scalar scalarField(String json, String numberKey, String presenceKey) {
        String presenceName = stringField(json, presenceKey);
        if (presenceName != null && !presenceName.isEmpty() && !"PRESENT".equals(presenceName)) {
            Presence presence = Presence.valueOf(presenceName);
            if (presence == Presence.STALE) {
                return Scalar.stale();
            }
            if (presence == Presence.UNAVAILABLE) {
                return Scalar.unavailable();
            }
            return Scalar.unknown();
        }
        Matcher m = Pattern.compile("\"" + numberKey + "\"\\s*:\\s*(-?\\d+(?:\\.\\d+)?)").matcher(json);
        return m.find() ? Scalar.of(Double.parseDouble(m.group(1))) : Scalar.unknown();
    }

    static List<String> objectArray(String json, String key) {
        Matcher header = Pattern.compile("\"" + key + "\"\\s*:\\s*\\[").matcher(json);
        if (!header.find()) {
            return Collections.emptyList();
        }
        int i = header.end();
        List<String> objects = new ArrayList<>();
        int depth = 0;
        int start = -1;
        boolean inString = false;
        boolean escape = false;
        for (; i < json.length(); i++) {
            char c = json.charAt(i);
            if (inString) {
                if (escape) {
                    escape = false;
                    continue;
                }
                if (c == '\\') {
                    escape = true;
                    continue;
                }
                if (c == '"') {
                    inString = false;
                }
                continue;
            }
            if (c == '"') {
                inString = true;
                continue;
            }
            if (c == '{') {
                if (depth == 0) {
                    start = i;
                }
                depth++;
            } else if (c == '}') {
                depth--;
                if (depth == 0 && start >= 0) {
                    objects.add(json.substring(start, i + 1));
                    start = -1;
                }
            } else if (c == ']' && depth == 0) {
                break;
            }
        }
        return objects;
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

    public static final class ReplayResult {
        private final String schemaVersion;
        private final EchoFeatureFlags flags;
        private final List<EchoSnapshot> snapshots;
        private final List<EchoDecisionRecord> records;
        private final String exportedJsonl;

        ReplayResult(String schemaVersion, EchoFeatureFlags flags, List<EchoSnapshot> snapshots,
                     List<EchoDecisionRecord> records, String exportedJsonl) {
            this.schemaVersion = schemaVersion;
            this.flags = flags;
            this.snapshots = Collections.unmodifiableList(new ArrayList<>(snapshots));
            this.records = Collections.unmodifiableList(new ArrayList<>(records));
            this.exportedJsonl = exportedJsonl;
        }

        public String schemaVersion() {
            return schemaVersion;
        }

        public EchoFeatureFlags flags() {
            return flags;
        }

        public List<EchoSnapshot> snapshots() {
            return snapshots;
        }

        public List<EchoDecisionRecord> records() {
            return records;
        }

        public String exportedJsonl() {
            return exportedJsonl;
        }
    }
}
