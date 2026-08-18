package org.allsparks.echo.config;

import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EchoConfigTest {

    @Test
    void defaultsMatchShippedExampleAndValidate() throws Exception {
        EchoConfig defaults = EchoConfig.defaults();
        assertTrue(defaults.valid());
        String json = new String(Files.readAllBytes(Path.of("config", "echo-default.json")), StandardCharsets.UTF_8);
        EchoConfig parsed = EchoConfig.parseJson(json);
        assertTrue(parsed.valid());
        assertTrue(Math.abs(parsed.minConfidence() - defaults.minConfidence()) < 1e-9);
        assertTrue(parsed.maxObservationAgeMs() == defaults.maxObservationAgeMs());
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
}
