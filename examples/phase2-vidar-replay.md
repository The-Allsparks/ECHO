# Phase 2: ViDAR fixture → replay

This sketch does not talk to a Control Hub, does not load a ViDAR JAR, and does not play speakers.

Vertical slice: **ViDAR-shaped fixture → `VidarObservation` → flag-gated `EchoSnapshot` → `EchoEngine` → `EchoDecisionRecord` → golden replay.**

## 1. Build an observation

```java
FakeClock clock = new FakeClock();
VidarObservation obs = new VidarObservation(
        "tag-3",
        "sample",
        VidarObservation.wrapBearingRad(0.5),
        Scalar.of(1.1),
        Scalar.of(0.9),
        clock.nanoTime());
// obs.sourceId == "vidar"
// VidarObservation.CONTRACT == "vidar-echo.v0"
EchoSnapshot snap = obs.applyTo(EchoSnapshot.builder()
                .receiptNanos(clock.nanoTime())
                .driverEnabled(true)
                .audioDeviceStatus(AudioDeviceStatus.AVAILABLE))
        .build();
// snap.targetSource() == TargetSource.BOUNDED_ADAPTER
```

## 2. Flag gate

`EchoEngine.phase0(clock)` leaves `vidarAdapter` **false**. Stepping `snap` yields `SilenceReason.MISSING_CAPABILITY` and `RejectionReason.VIDAR_ADAPTER_DISABLED`.

Enable the adapter only for this desktop/sim path:

```java
EchoEngine engine = new EchoEngine(
        clock,
        EchoConfig.defaults(),
        EchoFeatureFlags.builder().vidarAdapter(true).build(),
        new NoOpRenderer());
EchoDecision decision = engine.step(snap);
System.out.println(decision.record().toExplanation());
```

Expect `selected=GUIDANCE` when confidence, age, and bearing are PRESENT.

## 3. File replay (`echo-replay.v0`)

Fixtures: `src/test/resources/replay/vidar-guidance.json` and `vidar-stale.json`.

```java
ReplayRunner.ReplayResult result = ReplayRunner.run(json);
EchoDecisionRecord record = result.records().get(0);
// guidance fixture: record.selected() == CueFamily.GUIDANCE
// stale fixture: record.silenceReason() == SilenceReason.STALE
```

Run the same JSON twice with a fresh runner each time. `record.toExplanation()` must match per step.

## 4. Optional JSONL export

`traceExport` stays false unless you opt in. When true, `EchoEngine.step` writes one `echo-decision.v0` line per decision through `TraceExporter.jsonl(Appendable)`. Field names match `EchoDecisionRecord` (`selected`, `cueSource`, `silenceReason`, `rejected`, …).
