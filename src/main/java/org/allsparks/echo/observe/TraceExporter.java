package org.allsparks.echo.observe;

import org.allsparks.echo.sonify.SonifiedCue;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Objects;

/**
 * Optional TRACE-shaped JSONL writer for {@link EchoDecisionRecord}.
 * No-op unless constructed with an {@link Appendable}. {@code EchoEngine}
 * invokes {@link #write(EchoDecisionRecord)} only when {@code traceExport} is true.
 */
public final class TraceExporter {
    public static final String SCHEMA = "echo-decision.v0";

    private final Appendable out;

    private TraceExporter(Appendable out) {
        this.out = out;
    }

    public static TraceExporter noop() {
        return new TraceExporter(null);
    }

    public static TraceExporter jsonl(Appendable out) {
        return new TraceExporter(Objects.requireNonNull(out, "out"));
    }

    public boolean isNoop() {
        return out == null;
    }

    public void write(EchoDecisionRecord record) {
        if (out == null || record == null) {
            return;
        }
        try {
            out.append(toJsonLine(record));
            out.append('\n');
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public static String toJsonLine(EchoDecisionRecord record) {
        StringBuilder sb = new StringBuilder(256);
        SonifiedCue cue = record.sonified();
        sb.append('{');
        field(sb, "schemaVersion", SCHEMA, true);
        field(sb, "selected", record.selected().name(), false);
        field(sb, "cueSource", record.cueSource(), false);
        field(sb, "silenceReason", record.silenceReason().name(), false);
        field(sb, "explanation", record.explanation(), false);
        nullableLong(sb, "inputAgeMs", record.inputAgeMs());
        nullableDouble(sb, "inputConfidence", record.inputConfidence());
        number(sb, "selectionLatencyNanos", record.selectionLatencyNanos());
        number(sb, "renderLatencyNanos", record.renderLatencyNanos());
        number(sb, "queueDepth", record.queueDepth());
        number(sb, "droppedCues", record.droppedCues());
        number(sb, "rateLimitedCues", record.rateLimitedCues());
        field(sb, "audioDeviceStatus", record.audioDeviceStatus().name(), false);
        bool(sb, "driverEnabled", record.driverEnabled());
        field(sb, "configVersion", record.configVersion(), false);
        field(sb, "rendererName", record.rendererName(), false);
        bool(sb, "rendererFailure", record.rendererFailure());
        number(sb, "pan", cue.pan());
        number(sb, "pulseIntervalMs", cue.pulseIntervalMs());
        sb.append(",\"rejected\":[");
        boolean first = true;
        for (RejectedCue r : record.rejected()) {
            if (!first) {
                sb.append(',');
            }
            first = false;
            sb.append("{\"family\":\"").append(escape(r.family().name()));
            sb.append("\",\"reason\":\"").append(escape(r.reason().name())).append("\"}");
        }
        sb.append("]}");
        return sb.toString();
    }

    private static void field(StringBuilder sb, String key, String value, boolean first) {
        if (!first) {
            sb.append(',');
        }
        sb.append('"').append(key).append("\":\"").append(escape(value == null ? "" : value)).append('"');
    }

    private static void bool(StringBuilder sb, String key, boolean value) {
        sb.append(",\"").append(key).append("\":").append(value);
    }

    private static void number(StringBuilder sb, String key, long value) {
        sb.append(",\"").append(key).append("\":").append(value);
    }

    private static void number(StringBuilder sb, String key, double value) {
        sb.append(",\"").append(key).append("\":").append(value);
    }

    private static void nullableLong(StringBuilder sb, String key, Long value) {
        sb.append(",\"").append(key).append("\":");
        if (value == null) {
            sb.append("null");
        } else {
            sb.append(value);
        }
    }

    private static void nullableDouble(StringBuilder sb, String key, Double value) {
        sb.append(",\"").append(key).append("\":");
        if (value == null) {
            sb.append("null");
        } else {
            sb.append(value);
        }
    }

    private static String escape(String s) {
        StringBuilder out = new StringBuilder(s.length() + 8);
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c == '\\' || c == '"') {
                out.append('\\').append(c);
            } else if (c == '\n') {
                out.append("\\n");
            } else if (c == '\r') {
                out.append("\\r");
            } else if (c == '\t') {
                out.append("\\t");
            } else {
                out.append(c);
            }
        }
        return out.toString();
    }
}
