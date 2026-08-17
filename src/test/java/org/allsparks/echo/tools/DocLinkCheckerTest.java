package org.allsparks.echo.tools;

import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

class DocLinkCheckerTest {

    @Test
    void requiredDocsExist() {
        List<String> required = Arrays.asList(
                "README.md", "LICENSE", "CONTRIBUTING.md", "CODE_OF_CONDUCT.md", "SECURITY.md",
                "docs/architecture.md", "docs/feasibility-decision.md", "docs/cue-vocabulary.md",
                "docs/human-factors.md", "docs/hearing-safety.md", "docs/driver-training.md",
                "docs/vidar-integration.md", "docs/mimic-integration.md", "docs/amper-integration.md",
                "docs/beacon-integration.md", "docs/trace-integration.md", "docs/helm-integration.md",
                "docs/configuration.md", "docs/testing.md", "docs/hardware-validation.md",
                "docs/competition-readiness.md", "docs/troubleshooting.md",
                "docs/student-learning-path.md", "docs/mentor-guide.md", "docs/glossary.md",
                "docs/references.md",
                "docs/research/ftc-rules-and-platform.md", "docs/research/android-audio.md",
                "docs/research/controller-audio.md", "docs/research/driver-hub-audio.md",
                "docs/research/hearing-and-human-factors.md",
                "docs/research/output-path-comparison.md", "docs/research/build-versus-adopt.md",
                "docs/adr/0001-conditional-go.md", "examples/README.md", "config/echo-default.json");
        for (String rel : required) {
            assertTrue(Files.exists(Path.of(rel)), "missing " + rel);
        }
    }
}
