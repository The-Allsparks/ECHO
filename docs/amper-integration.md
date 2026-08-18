# AMPER integration

AMPER owns electrical observation and power warnings. ECHO must not override AMPER limits. ECHO does not compute brownout.

## Contract (version `amper-echo.v0`)

Java constant: `AmperObservation.CONTRACT` (`"amper-echo.v0"`).

Mapping class: `org.allsparks.echo.adapters.AmperObservation`. Core does not depend on an AMPER JAR.

| Field | Java | Type | Notes |
| ----- | ---- | ---- | ----- |
| `sourceId` | `sourceId` | `String` | Defaults to `"amper"` (`AmperObservation.DEFAULT_SOURCE_ID`) |
| `warning` | `warning` | `Flag` | PRESENT + true means AMPER already classified a warning. v0 is Flag, not a severity enum |
| `observationNanos` | `observationNanos` | `Long` | Optional; same clock domain as snapshot `receiptNanos` when present |

`AmperObservation.applyTo(EchoSnapshot.Builder)` sets `amperWarning` only. It does not invent `targetId` or guidance.

Replay steps may set `"amperWarning": true` (or `"amperWarningPresence"` for non-PRESENT states). `ReplayRunner` applies that Flag through `AmperObservation`.

## Feature flag

`EchoFeatureFlags.amperAdapter()` defaults **false**. When the flag is off and `amperWarning` is true, `CueSelector` rejects `WARN_AMPER` with `RejectionReason.FLAG_DISABLED` and continues selection (guidance may still play). When the flag is on and the warning Flag is true, `WARN_AMPER` preempts guidance.

Unavailable or unknown AMPER does not invent `WARN_AMPER`.

## Rules

- Maps to `WARN_AMPER` only when AMPER already classified a warning (`Flag.isTrue()`).
- ECHO does not compute brownout itself.
- Rate-limited. Distinct timbre from MIMIC/BEACON.
- Adapter is optional; missing AMPER → no warning candidate, not a crash.

Compile-time: **no** `org.allsparks.amper` dependency in core.
