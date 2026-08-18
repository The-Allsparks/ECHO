package org.allsparks.echo.observe;

import org.allsparks.echo.EchoEngine;
import org.allsparks.echo.EchoFeatureFlags;
import org.allsparks.echo.clock.FakeClock;
import org.allsparks.echo.config.EchoConfig;
import org.allsparks.echo.cue.CueFamily;
import org.allsparks.echo.render.FakeRenderer;
import org.allsparks.echo.training.Snapshots;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TraceExporterTest {

    @Test
    void defaultEngineDoesNotExport() {
        FakeClock clock = new FakeClock();
        StringBuilder sink = new StringBuilder();
        EchoEngine engine = new EchoEngine(clock, EchoConfig.defaults(), EchoFeatureFlags.disabled(),
                new FakeRenderer(), TraceExporter.jsonl(sink));
        engine.step(Snapshots.guidance(clock, 0, 1.0, 0.9));
        assertEquals("", sink.toString());
        assertTrue(TraceExporter.noop().isNoop());
    }

    @Test
    void flagOnWritesJsonlDecisionRecord() {
        FakeClock clock = new FakeClock();
        StringBuilder sink = new StringBuilder();
        EchoEngine engine = new EchoEngine(clock, EchoConfig.defaults(),
                EchoFeatureFlags.builder().traceExport(true).build(),
                new FakeRenderer(), TraceExporter.jsonl(sink));
        EchoDecisionRecord record = engine.step(Snapshots.guidance(clock, 0, 1.0, 0.9)).record();
        String jsonl = sink.toString();
        assertTrue(jsonl.endsWith("\n"));
        assertTrue(jsonl.contains("\"schemaVersion\":\"echo-decision.v0\""));
        assertTrue(jsonl.contains("\"selected\":\"" + CueFamily.GUIDANCE.name() + "\""));
        assertTrue(jsonl.contains("\"cueSource\":\"DRIVER\""));
        assertTrue(jsonl.contains("\"silenceReason\":\"NONE\""));
        assertTrue(jsonl.contains("\"configVersion\":\"echo-config.v1\""));
        assertTrue(jsonl.contains("\"rendererName\""));
        assertTrue(jsonl.contains("\"driverEnabled\":true"));
        assertTrue(jsonl.contains("\"rejected\":["));
        assertFalse(jsonl.contains("\n\n"));
        String line = TraceExporter.toJsonLine(record);
        assertTrue(jsonl.startsWith(line));
    }
}
