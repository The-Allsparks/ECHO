package org.allsparks.echo.config;

import org.allsparks.echo.EchoDecision;
import org.allsparks.echo.EchoEngine;
import org.allsparks.echo.EchoFeatureFlags;
import org.allsparks.echo.clock.FakeClock;
import org.allsparks.echo.cue.CueFamily;
import org.allsparks.echo.cue.SilenceReason;
import org.allsparks.echo.render.NoOpRenderer;
import org.allsparks.echo.training.Snapshots;
import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EchoConfigTest {

    @Test
    void loadDefaultMatchesDefaultsAndShippedExample() throws Exception {
        EchoConfig defaults = EchoConfig.defaults();
        EchoConfig loaded = EchoConfig.loadDefault();
        assertTrue(defaults.valid());
        assertTrue(loaded.valid());
        assertSameFields(defaults, loaded);

        EchoConfig fromTree = EchoConfig.fromPath(Path.of("config", "echo-default.json"));
        assertTrue(fromTree.valid());
        assertSameFields(defaults, fromTree);

        String treeJson = new String(Files.readAllBytes(Path.of("config", "echo-default.json")),
                StandardCharsets.UTF_8).replace("\r\n", "\n");
        String classpathJson = resourceText(EchoConfig.DEFAULT_RESOURCE).replace("\r\n", "\n");
        assertEquals(treeJson, classpathJson);
    }

    @Test
    void unknownSchemaRejected() {
        EchoConfig parsed = EchoConfig.parseJson("{\"schemaVersion\":\"echo-config.v0\",\"minConfidence\":0.5}");
        assertFalse(parsed.valid());
    }

    @Test
    void invalidGainsRejected() {
        EchoConfig parsed = EchoConfig.parseJson(
                "{\"schemaVersion\":\"echo-config.v1\",\"defaultGain\":0.5,\"maxGain\":0.1,\"minConfidence\":0.5,"
                        + "\"maxObservationAgeMs\":250,\"pulseNearM\":0.2,\"pulseFarM\":2,\"pulseNearMs\":90,"
                        + "\"pulseFarMs\":700,\"pitchMinHz\":100,\"pitchMaxHz\":200,\"hysteresisPan\":0.1}");
        assertFalse(parsed.valid());
    }

    @Test
    void missingPathIsInvalid() {
        EchoConfig parsed = EchoConfig.fromPath(Path.of("config", "does-not-exist.json"));
        assertFalse(parsed.valid());
    }

    @Test
    void missingResourceIsInvalid() {
        EchoConfig parsed = EchoConfig.fromResource("/org/allsparks/echo/no-such-config.json");
        assertFalse(parsed.valid());
    }

    @Test
    void invalidJsonIsInvalid() {
        EchoConfig parsed = EchoConfig.parseJson("{ this is not json");
        assertFalse(parsed.valid());
    }

    @Test
    void unknownKeysAreIgnored() {
        EchoConfig parsed = EchoConfig.parseJson(
                "{\"schemaVersion\":\"echo-config.v1\",\"minConfidence\":0.60,\"unknownKey\":true}");
        assertTrue(parsed.valid());
        assertEquals(0.60, parsed.minConfidence(), 1e-9);
    }

    @Test
    void invalidConfigIsSilence() {
        EchoConfig bad = EchoConfig.parseJson("{\"schemaVersion\":\"echo-config.v0\"}");
        assertFalse(bad.valid());
        FakeClock clock = new FakeClock();
        EchoEngine engine = new EchoEngine(clock, bad, EchoFeatureFlags.disabled(), new NoOpRenderer());
        EchoDecision decision = engine.step(Snapshots.guidance(clock, 0, 1.0, 0.9));
        assertEquals(CueFamily.SILENCE, decision.record().selected());
        assertEquals(SilenceReason.INVALID_CONFIG, decision.record().silenceReason());
    }

    private static void assertSameFields(EchoConfig expected, EchoConfig actual) {
        assertEquals(expected.schemaVersion(), actual.schemaVersion());
        assertEquals(expected.minConfidence(), actual.minConfidence(), 1e-9);
        assertEquals(expected.maxObservationAgeMs(), actual.maxObservationAgeMs());
        assertEquals(expected.panSaturationBearingRad(), actual.panSaturationBearingRad(), 1e-9);
        assertEquals(expected.pulseNearM(), actual.pulseNearM(), 1e-9);
        assertEquals(expected.pulseFarM(), actual.pulseFarM(), 1e-9);
        assertEquals(expected.pulseNearMs(), actual.pulseNearMs(), 1e-9);
        assertEquals(expected.pulseFarMs(), actual.pulseFarMs(), 1e-9);
        assertEquals(expected.alignmentPulse(), actual.alignmentPulse());
        assertEquals(expected.pitchEnabled(), actual.pitchEnabled());
        assertEquals(expected.pitchHz(), actual.pitchHz(), 1e-9);
        assertEquals(expected.pitchMinHz(), actual.pitchMinHz(), 1e-9);
        assertEquals(expected.pitchMaxHz(), actual.pitchMaxHz(), 1e-9);
        assertEquals(expected.defaultGain(), actual.defaultGain(), 1e-9);
        assertEquals(expected.maxGain(), actual.maxGain(), 1e-9);
        assertEquals(expected.hysteresisPan(), actual.hysteresisPan(), 1e-9);
        assertEquals(expected.commitmentWindowMs(), actual.commitmentWindowMs());
        assertEquals(expected.confirmCooldownMs(), actual.confirmCooldownMs());
        assertEquals(expected.warnCooldownMs(), actual.warnCooldownMs());
        assertEquals(expected.warnRateLimitPerSec(), actual.warnRateLimitPerSec(), 1e-9);
    }

    private static String resourceText(String path) throws Exception {
        try (InputStream in = EchoConfig.class.getResourceAsStream(path)) {
            assertTrue(in != null, "missing classpath resource " + path);
            return new String(in.readAllBytes(), StandardCharsets.UTF_8);
        }
    }
}
