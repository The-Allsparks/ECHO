# ViDAR integration

ViDAR owns detection, frames, confidence, and observation age. ECHO owns presentation.

## Contract (version `vidar-echo.v0`)

Java constant: `VidarObservation.CONTRACT` (`"vidar-echo.v0"`).

ECHO accepts a **single selected target**, not the full track list. Mapping class: `org.allsparks.echo.adapters.VidarObservation`.

| Field | Java | Type | Notes |
| ----- | ---- | ---- | ----- |
| `sourceId` | `sourceId` | `String` | Defaults to `"vidar"` (`VidarObservation.DEFAULT_SOURCE_ID`) |
| `targetId` | `targetId` | `String` | Explicit id; empty or null → `NO_TARGET` |
| `category` | `category` | `String` | Opaque to sonification |
| `bearingRad` | `bearingRad` | `Scalar` | Robot-relative, **+right**; 0 forward; range (−π, π] |
| `distanceM` | `distanceM` | `Scalar` | meters |
| `confidence` | `confidence` | `Scalar` | 0–1 |
| `observationNanos` | `observationNanos` | `long` | Same clock domain as snapshot `receiptNanos` when possible |
| `frame` | (not on DTO) | — | Adapter must convert to ECHO bearing convention **before** this DTO |

`VidarObservation.applyTo(EchoSnapshot.Builder)` sets `targetSource` to `TargetSource.BOUNDED_ADAPTER`.

## Frames

Frame conversion is adapter-side. The v0 DTO does **not** store camera, turret, or field frames. Call `VidarObservation.wrapBearingRad(double)` (uses `Angles.wrapRad`) if the upstream robot-relative angle may be outside (−π, π]. Do not pass field-centric bearings into ECHO.

## Feature flag

`EchoFeatureFlags.vidarAdapter()` defaults **false**. When `targetSource` is `BOUNDED_ADAPTER` and the flag is off, `CueSelector` rejects guidance with `RejectionReason.VIDAR_ADAPTER_DISABLED` and `SilenceReason.MISSING_CAPABILITY`. Enable the flag only for desktop/sim replay or an approved adapter path.

## Rules

- Confidence below `EchoConfig.minConfidence()` → silence (`LOW_CONFIDENCE`).
- Age above `EchoConfig.maxObservationAgeMs()` → silence (`STALE`).
- Unknown bearing → silence (`UNKNOWN_INPUT`).
- No selected id → silence (`NO_TARGET`).
- `vidarAdapter=false` with `BOUNDED_ADAPTER` → silence (`MISSING_CAPABILITY`).
- Adapter is optional; missing ViDAR → no guidance candidate, not a crash.

Compile-time: **no** `org.allsparks.vidar` dependency in core.

HELM or the driver must choose `targetId`. ECHO will not scan.
