package org.allsparks.echo.replay;

import org.allsparks.echo.cue.CueFamily;
import org.allsparks.echo.cue.SilenceReason;
import org.allsparks.echo.input.TargetSource;
import org.allsparks.echo.observe.EchoDecisionRecord;
import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ReplayGoldenTest {

    @Test
    void vidarGuidanceFixtureSelectsGuidance() {
        ReplayRunner.ReplayResult result = ReplayRunner.run(resource("/replay/vidar-guidance.json"));
        assertEquals(ReplayRunner.SCHEMA, result.schemaVersion());
        assertTrue(result.flags().vidarAdapter());
        assertEquals(1, result.records().size());
        assertEquals(TargetSource.BOUNDED_ADAPTER, result.snapshots().get(0).targetSource());
        assertEquals("tag-3", result.snapshots().get(0).targetId());
        EchoDecisionRecord record = result.records().get(0);
        assertEquals(CueFamily.GUIDANCE, record.selected());
        assertEquals(SilenceReason.NONE, record.silenceReason());
        assertEquals("BOUNDED_ADAPTER", record.cueSource());
    }

    @Test
    void staleObservationFixtureSilences() {
        ReplayRunner.ReplayResult result = ReplayRunner.run(resource("/replay/vidar-stale.json"));
        assertEquals(1, result.records().size());
        EchoDecisionRecord record = result.records().get(0);
        assertEquals(CueFamily.SILENCE, record.selected());
        assertEquals(SilenceReason.STALE, record.silenceReason());
    }

    @Test
    void amperWarningFixturePreemptsGuidance() {
        ReplayRunner.ReplayResult result = ReplayRunner.run(resource("/replay/amper-preempt.json"));
        assertEquals(ReplayRunner.SCHEMA, result.schemaVersion());
        assertTrue(result.flags().amperAdapter());
        assertEquals(1, result.records().size());
        assertTrue(result.snapshots().get(0).amperWarning().isTrue());
        assertEquals(TargetSource.DRIVER, result.snapshots().get(0).targetSource());
        EchoDecisionRecord record = result.records().get(0);
        assertEquals(CueFamily.WARN_AMPER, record.selected());
        assertEquals(SilenceReason.NONE, record.silenceReason());
    }

    @Test
    void replayRunTwiceYieldsIdenticalExplanations() {
        String guidance = resource("/replay/vidar-guidance.json");
        String stale = resource("/replay/vidar-stale.json");
        String amper = resource("/replay/amper-preempt.json");
        assertIdenticalReplays(guidance);
        assertIdenticalReplays(stale);
        assertIdenticalReplays(amper);
    }

    @Test
    void unsupportedSchemaRejected() {
        assertThrows(IllegalArgumentException.class,
                () -> ReplayRunner.run("{\"schemaVersion\":\"echo-replay.v9\",\"steps\":[]}"));
    }

    private static void assertIdenticalReplays(String json) {
        ReplayRunner.ReplayResult first = ReplayRunner.run(json);
        ReplayRunner.ReplayResult second = ReplayRunner.run(json);
        assertEquals(first.records().size(), second.records().size());
        List<String> a = explanations(first);
        List<String> b = explanations(second);
        assertEquals(a, b);
    }

    private static List<String> explanations(ReplayRunner.ReplayResult result) {
        List<String> out = new ArrayList<>();
        for (EchoDecisionRecord record : result.records()) {
            out.add(record.toExplanation());
        }
        return out;
    }

    private static String resource(String path) {
        try (InputStream in = ReplayGoldenTest.class.getResourceAsStream(path)) {
            if (in == null) {
                throw new IllegalStateException("missing fixture " + path);
            }
            byte[] bytes = in.readAllBytes();
            return new String(bytes, StandardCharsets.UTF_8);
        } catch (java.io.IOException e) {
            throw new IllegalStateException(path, e);
        }
    }
}
