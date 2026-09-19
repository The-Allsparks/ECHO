package org.allsparks.echo.observe;

import java.util.Objects;
import org.allsparks.echo.cue.CueFamily;

/**
 * Maps {@link EchoDecisionRecord} onto TRACE channel names. ECHO does not
 * import TRACE and does not write JSONL on this path. TeamCode supplies the
 * {@link Emitter} that calls TRACE when ECHO is composed.
 */
public final class EchoToTraceAdapter implements EchoDecisionSink {
    public interface Emitter {
        void event(String name, String message);

        void record(String name, double value);
    }

    private final Emitter emitter;

    public EchoToTraceAdapter(Emitter emitter) {
        this.emitter = Objects.requireNonNull(emitter, "emitter");
    }

    @Override
    public void onDecision(EchoDecisionRecord record) {
        if (record == null) {
            return;
        }
        CueFamily selected = record.selected();
        String family = selected == null ? "SILENCE" : selected.name();
        emitter.event("ECHO/Decision/" + family, record.toExplanation());
        if (record.inputAgeMs() != null) {
            emitter.record("ECHO/Input/AgeMs", record.inputAgeMs().doubleValue());
        }
        if (record.inputConfidence() != null) {
            emitter.record("ECHO/Input/Confidence", record.inputConfidence());
        }
        emitter.record("ECHO/Select/LatencyNs", record.selectionLatencyNanos());
        emitter.record("ECHO/Render/LatencyNs", record.renderLatencyNanos());
        emitter.record("ECHO/Queue/Depth", record.queueDepth());
        emitter.record("ECHO/DroppedCues", record.droppedCues());
        emitter.record("ECHO/RendererFailure", record.rendererFailure() ? 1.0 : 0.0);
    }
}
