# Phase 0: select a snapshot

```java
FakeClock clock = new FakeClock();
EchoEngine engine = EchoEngine.phase0(clock);
EchoSnapshot snap = EchoSnapshot.guidanceExample(clock, -Math.PI / 2, 1.0, 0.9);
EchoDecision decision = engine.step(snap);
System.out.println(decision.record().toExplanation());
```

Expect a left-panned `GUIDANCE` cue when the driver is enabled, audio device is marked available, and confidence/age pass. This prints numbers; it does not play speakers unless you construct a desktop renderer with the audio flag.
