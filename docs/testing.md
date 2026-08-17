# Testing

Do not rely only on listening.

## Automated (Phase 0–1)

See `src/test/java/org/allsparks/echo`. Coverage includes:

- Left / center / right pan and bearing wrap
- Distance and alignment pulse maps, boundaries
- Confidence, age, stale, unknown
- Fake clock
- Eligibility, priority, warning preemption
- Hysteresis, commitment, cooldowns, rate limits
- Silence policy, contradictory inputs, missing integrations
- Missing/lost audio device
- Renderer failure isolation
- Config validation
- Deterministic replay (two runs, same record)
- TRACE-shaped record fields present

Property-style: bearing normalization is tested across a grid of offsets (not a full PBT library, to keep JDK-only).

## What tests cannot prove

- Driver Hub USB behavior
- Match legality
- That cues improve cycle time

## Running

```powershell
.\gradlew.bat test
```
