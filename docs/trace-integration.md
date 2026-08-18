# TRACE integration

TRACE owns recording schemas and replay. ECHO emits `EchoDecisionRecord` with the fields in [testing.md](testing.md) / observability list. Core does not depend on TRACE JARs.

## In-memory record

`EchoEngine.step` always builds an `EchoDecisionRecord`. Minimum fields (Java names): `selected`, `cueSource`, `rejected`, `inputAgeMs`, `inputConfidence`, `selectionLatencyNanos`, `rendererName`, `audioDeviceStatus`, `driverEnabled`, `configVersion`, `silenceReason`, `sonified`, `explanation`.

Replay invariant: identical snapshots + config + `FakeClock` → identical `toExplanation()` per step. Use a **new** `EchoEngine` per session; `CueSelector` holds hysteresis/cooldown state.

## `echo-replay.v0` fixtures

Schema constant: `ReplayRunner.SCHEMA` (`"echo-replay.v0"`). JDK-only parser (same regex style as `EchoConfig.parseJson`). Golden files live under `src/test/resources/replay/`.

Top-level fields:

| JSON key | Java / meaning |
| -------- | -------------- |
| `schemaVersion` | Must be `echo-replay.v0` |
| `clockStartNanos` | `FakeClock` start |
| `vidarAdapter` | `EchoFeatureFlags.vidarAdapter()` (default false) |
| `traceExport` | `EchoFeatureFlags.traceExport()` (default false) |
| `helmTargetSource`, `amperAdapter`, `mimicAdapter`, `beaconAdapter` | matching flag builders (default false) |
| `steps` | array of snapshot objects |

Each step uses the same names as `EchoSnapshot` / `VidarObservation`: `receiptNanos`, `observationNanos`, `driverEnabled`, `audioDeviceStatus`, `targetSource`, `sourceId`, `targetId`, `category`, `bearingRad`, `distanceM`, `confidence`. Optional `bearingPresence` / `distancePresence` / `confidencePresence` (`UNKNOWN`, `STALE`, `UNAVAILABLE`) override a numeric scalar. Optional `amperWarning` (JSON boolean) / `amperWarningPresence` feed `AmperObservation` (`amper-echo.v0`); missing keys leave the Flag unavailable.

When `targetSource` is `BOUNDED_ADAPTER` (the default if omitted), `ReplayRunner` builds the snapshot through `VidarObservation.applyTo`, then applies `AmperObservation`.

Run with `ReplayRunner.run(String)` or `ReplayRunner.run(Path)`.

## `traceExport` JSONL

Flag `traceExport` default **false**. Records still exist in-memory for tests. `EchoEngine.step` calls `TraceExporter.write` **only** when the flag is true.

`TraceExporter.noop()` writes nothing. `TraceExporter.jsonl(Appendable)` writes one JSON object per decision, newline-terminated. Line `schemaVersion` is `echo-decision.v0` (`TraceExporter.SCHEMA`). Field names match Java: `selected`, `cueSource`, `silenceReason`, `explanation`, `inputAgeMs`, `inputConfidence`, `selectionLatencyNanos`, `renderLatencyNanos`, `queueDepth`, `droppedCues`, `rateLimitedCues`, `audioDeviceStatus`, `driverEnabled`, `configVersion`, `rendererName`, `rendererFailure`, `pan`, `pulseIntervalMs`, `rejected` (array of `{family, reason}`).

This is a TRACE-shaped export, not a TRACE compile dependency.
