# Testing

Do not rely only on listening.

## Automated (Phase 0–2)

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
- `vidarAdapter` gate for `TargetSource.BOUNDED_ADAPTER` (`VIDAR_ADAPTER_DISABLED` → `MISSING_CAPABILITY`)
- ViDAR adapter negatives: stale, low confidence, unknown bearing, empty `targetId`
- File replay: `echo-replay.v0` fixtures under `src/test/resources/replay/` via `ReplayRunner`
- Golden replay run twice → identical `EchoDecisionRecord.toExplanation()` per step
- `TraceExporter` no-op unless `traceExport=true`

Property-style: bearing normalization is tested across a grid of offsets (not a full PBT library, to keep JDK-only).

## What tests cannot prove

- Driver Hub USB behavior
- Match legality
- That cues improve cycle time
- Live ViDAR on a Control Hub

## Running

```powershell
.\gradlew.bat test
```
