# ViDAR integration

ViDAR owns detection, frames, confidence, and observation age. ECHO owns presentation.

## Contract (version `vidar-echo.v0`)

ECHO accepts a **single selected target**, not the full track list:

| Field | Type | Notes |
| ----- | ---- | ----- |
| `sourceId` | string | e.g. `vidar` |
| `targetId` | string | Explicit id |
| `category` | string | Opaque to sonification |
| `bearingRad` | Scalar | Robot-relative, +right |
| `distanceM` | Scalar | meters |
| `confidence` | Scalar | 0–1 |
| `observationNanos` | long | ViDAR clock domain documented by adapter |
| `frame` | string | Adapter must convert to ECHO bearing convention |

## Rules

- Confidence below threshold → silence.
- Age above threshold → silence (`STALE`).
- Unknown bearing → silence (`UNKNOWN_INPUT`).
- No selected id → silence (`NO_TARGET`).
- Adapter is optional; missing ViDAR → no guidance candidate, not a crash.

Compile-time: **no** `org.allsparks.vidar` dependency in core. See `org.allsparks.echo.adapters.VidarObservation`.

HELM or the driver must choose `targetId`. ECHO will not scan.
