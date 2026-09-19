package org.allsparks.echo.observe;

/**
 * Optional observer of ECHO cue decisions. TRACE or tests implement this.
 * ECHO does not import TRACE. Default is {@link #NOOP}.
 *
 * <p>Called on the presentation thread from {@code EchoEngine.step()}. Must not
 * block or write files. JSONL {@link TraceExporter} is a desktop path, not
 * the Hub TRACE path.
 */
public interface EchoDecisionSink {
    void onDecision(EchoDecisionRecord record);

    EchoDecisionSink NOOP = new EchoDecisionSink() {
        @Override
        public void onDecision(EchoDecisionRecord record) {
            // intentionally empty
        }
    };
}
