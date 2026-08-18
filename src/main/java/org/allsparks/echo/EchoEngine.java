package org.allsparks.echo;

import org.allsparks.echo.clock.EchoClock;
import org.allsparks.echo.config.EchoConfig;
import org.allsparks.echo.cue.CueFamily;
import org.allsparks.echo.cue.SilenceReason;
import org.allsparks.echo.input.EchoSnapshot;
import org.allsparks.echo.observe.EchoDecisionRecord;
import org.allsparks.echo.observe.RejectedCue;
import org.allsparks.echo.observe.TraceExporter;
import org.allsparks.echo.render.CueRenderer;
import org.allsparks.echo.render.NoOpRenderer;
import org.allsparks.echo.render.RendererException;
import org.allsparks.echo.select.CueSelector;
import org.allsparks.echo.select.SelectionResult;
import org.allsparks.echo.sonify.SonifiedCue;

/**
 * Presentation engine. Never commands robot hardware.
 */
public final class EchoEngine {
    private final EchoClock clock;
    private final EchoConfig config;
    private final EchoFeatureFlags flags;
    private final CueRenderer renderer;
    private final TraceExporter traceExporter;
    private final CueSelector selector = new CueSelector();

    public EchoEngine(EchoClock clock, EchoConfig config, EchoFeatureFlags flags, CueRenderer renderer) {
        this(clock, config, flags, renderer, TraceExporter.noop());
    }

    public EchoEngine(EchoClock clock, EchoConfig config, EchoFeatureFlags flags, CueRenderer renderer,
                      TraceExporter traceExporter) {
        this.clock = clock;
        this.config = config;
        this.flags = flags;
        this.renderer = renderer == null ? new NoOpRenderer() : renderer;
        this.traceExporter = traceExporter == null ? TraceExporter.noop() : traceExporter;
    }

    public static EchoEngine phase0(EchoClock clock) {
        return new EchoEngine(clock, EchoConfig.defaults(), EchoFeatureFlags.disabled(), new NoOpRenderer());
    }

    public EchoDecision step(EchoSnapshot snapshot) {
        long t0 = clock.nanoTime();
        SelectionResult selected = selector.select(snapshot, config, flags);
        long t1 = clock.nanoTime();
        SonifiedCue cue = selected.cue();
        boolean renderFail = false;
        SilenceReason silence = cue.silenceReason();
        try {
            renderer.render(cue);
        } catch (RendererException e) {
            renderFail = true;
            silence = SilenceReason.RENDERER_FAILURE;
            renderer.mute();
        } catch (RuntimeException e) {
            renderFail = true;
            silence = SilenceReason.RENDERER_FAILURE;
            renderer.mute();
        }
        long t2 = clock.nanoTime();
        Long age = null;
        if (snapshot.observationNanos() != null) {
            age = (snapshot.receiptNanos() - snapshot.observationNanos()) / 1_000_000L;
        }
        Double conf = snapshot.confidence().isPresent() ? snapshot.confidence().si() : null;
        int rateLimited = 0;
        for (RejectedCue r : selected.rejected()) {
            if (r.reason().name().contains("RATE")) {
                rateLimited++;
            }
        }
        CueFamily family = renderFail ? CueFamily.SILENCE : cue.family();
        EchoDecisionRecord record = new EchoDecisionRecord(
                family,
                snapshot.targetSource().name(),
                selected.rejected(),
                age,
                conf,
                t1 - t0,
                t2 - t1,
                0,
                0,
                rateLimited,
                snapshot.audioDeviceStatus(),
                snapshot.driverEnabled(),
                config.schemaVersion(),
                renderer.name(),
                renderFail,
                silence,
                renderFail ? SonifiedCue.silence(SilenceReason.RENDERER_FAILURE) : cue,
                selected.explanation());
        if (flags.traceExport()) {
            try {
                traceExporter.write(record);
            } catch (RuntimeException ignored) {
                // export must not affect presentation
            }
        }
        return new EchoDecision(record);
    }

    public void mute() {
        renderer.mute();
    }

    public EchoConfig config() {
        return config;
    }

    public EchoFeatureFlags flags() {
        return flags;
    }
}
