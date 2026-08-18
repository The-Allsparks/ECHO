# Architecture

ECHO is a **presentation** layer. It converts an already-selected, timestamped snapshot into at most one guidance cue plus rare warnings. It never commands hardware and never silently chooses a strategic field object.

```
Snapshot (immutable)
    → Eligibility
    → Cue selection (priority, hysteresis, silence)
    → Sonification (pan / pulse / timbre / gain numbers)
    → CueRenderer (no-op | fake | desktop | gated Android/FTC)
    → Decision record (TRACE-shaped, no TRACE compile dependency)
```

Core compile-time dependencies: **JDK 11 + JUnit**. No FTC SDK. No Android. No ViDAR/AMPER/MIMIC/BEACON/TRACE/HELM JARs.

## Layers

| Layer | Package | Allowed to do | Forbidden |
| ----- | ------- | ------------- | --------- |
| Input model | `input`, `value`, `clock` | Hold SI units, presence, timestamps | Invent missing data |
| Eligibility | `eligibility` | Reject untrusted inputs | Coerce unknown → 0 |
| Selection | `select`, `cue` | Pick one cue or silence | Scan all ViDAR tracks for a new objective |
| Sonification | `sonify` | Map to pan/pulse/pitch/gain | Play audio |
| Rendering | `render` | Device I/O | Throw into robot control |
| Adapters | `adapters` | Optional DTO mapping | Compile-time sibling deps |
| Training | `training` | Desktop sim | Match audio |

## Coordinate and unit conventions

| Quantity | Unit | Convention |
| -------- | ---- | ---------- |
| Time | nanoseconds on `EchoClock` | Monotonic fake or system nano |
| Observation age | milliseconds | `receipt - observation` using the same clock domain when possible |
| Bearing | radians | Robot-relative; **0 = forward**; **positive = right**; normalized to (−π, π] |
| Distance | meters | ≥ 0 when PRESENT |
| Alignment error | radians | Absolute heading error; 0 = aligned |
| Confidence | 0–1 dimensionless | PRESENT only |
| Pan | −1…+1 | −1 full left, +1 full right, 0 center |
| Pulse interval | milliseconds | Larger = farther / less aligned |
| Pitch | hertz | Default unused (constant); optional bounded map |
| Gain | 0–1 linear amplitude | Default 0.08, cap 0.20 |

Unknown, unavailable, stale, false, and zero are distinct (`Presence`).

## Target selection policy

The snapshot’s `selectedTarget` must already identify the object. Sources allowed by contract:

- Driver-selected id
- Current explicit robot task id
- Bounded targeting adapter (team-owned, separately approved)
- HELM, **only** if `EchoFeatureFlags.helmTargetSource()` is true

ECHO must not iterate ViDAR’s full observation list to pick a game strategy.

## Feature flags

All active I/O flags default **false**. Selection logic may run in tests without enabling audio.

## Failure isolation

Renderer exceptions become `RendererFailure` on the decision record. They do not propagate as robot commands. Missing audio device → silence, not a crash.

## Related ADRs

- [0001](adr/0001-conditional-go.md)
- [0002](adr/0002-simple-stereo-not-hrtf.md)
- [0003](adr/0003-no-ftc-sdk-in-core.md)
- [0004](adr/0004-silence-is-valid.md)
- [0005](adr/0005-explicit-target-selection.md)
- [0006](adr/0006-feature-flags.md)
