# Phase 3: AMPER warning → replay

This sketch does not talk to a Control Hub, does not load an AMPER JAR, and does not play speakers.

Vertical slice: **AMPER-shaped Flag → `AmperObservation` → flag-gated `EchoSnapshot` → `EchoEngine` → `WARN_AMPER` preemption → golden replay.**

ECHO does not compute brownout. v0 is a `Flag`, not a severity enum.

## 1. Apply a warning onto a valid guidance snapshot

```java
FakeClock clock = new FakeClock();
EchoSnapshot guidance = Snapshots.guidance(clock, 0.2, 1.0, 0.9);
AmperObservation obs = new AmperObservation(Flag.of(true), clock.nanoTime());
// obs.sourceId == "amper"
// AmperObservation.CONTRACT == "amper-echo.v0"
EchoSnapshot snap = obs.applyTo(guidance.toBuilder()).build();
// snap.amperWarning().isTrue()
// snap.targetId() is still the guidance target; applyTo does not invent one
```

## 2. Flag gate

`EchoFeatureFlags` leaves `amperAdapter` **false**. Stepping `snap` yields `GUIDANCE` (the target is still valid) and `RejectionReason.FLAG_DISABLED` on `WARN_AMPER`.

`Flag.unavailable()` never invents `WARN_AMPER`, even when the adapter flag is on.

Enable the adapter only for this desktop/sim path:

```java
EchoEngine engine = new EchoEngine(
        clock,
        EchoConfig.defaults(),
        EchoFeatureFlags.builder().amperAdapter(true).build(),
        new FakeRenderer());
EchoDecision decision = engine.step(snap);
System.out.println(decision.record().toExplanation());
```

Expect `selected=WARN_AMPER`. The same snapshot without a true warning Flag would have been `GUIDANCE`.

## 3. File replay (`echo-replay.v0`)

Fixture: `src/test/resources/replay/amper-preempt.json`.

```java
ReplayRunner.ReplayResult result = ReplayRunner.run(json);
EchoDecisionRecord record = result.records().get(0);
// amper-preempt: record.selected() == CueFamily.WARN_AMPER
```

Run the same JSON twice with a fresh runner each time. `record.toExplanation()` must match per step.
