# Configuration

Schema version: `echo-config.v1`

File: `config/echo-default.json` (human-readable example). Validation is performed in `EchoConfig.validate()`.

## Fields (SI units in comments)

| Key | Unit | Default | Notes |
| --- | ---- | ------- | ----- |
| `schemaVersion` | string | `echo-config.v1` | Unknown version → invalid |
| `minConfidence` | 0–1 | `0.60` | Below → silence |
| `maxObservationAgeMs` | ms | `250` | Above → stale |
| `panSaturationBearingRad` | rad | `1.5708` (π/2) | Bearing mapped to pan ±1 |
| `pulseNearM` / `pulseFarM` | m | `0.25` / `2.50` | Distance pulse ends |
| `pulseNearMs` / `pulseFarMs` | ms | `90` / `700` | Interval range |
| `alignmentPulse` | bool | `false` | If true, pulse maps `|align|` instead of distance |
| `pitchEnabled` | bool | `false` | Default off |
| `pitchHz` | Hz | `440` | Constant when pitch disabled |
| `pitchMinHz` / `pitchMaxHz` | Hz | `350` / `520` | Hard bounds if enabled |
| `defaultGain` / `maxGain` | 0–1 | `0.08` / `0.20` | max ≥ default |
| `hysteresisPan` | 0–1 | `0.08` | Chatter reduction |
| `commitmentWindowMs` | ms | `150` | Keep cue briefly |
| `confirmCooldownMs` | ms | `400` | |
| `warnCooldownMs` | ms | `1500` | |
| `warnRateLimitPerSec` | 1/s | `0.5` | |

## Failure behavior

Invalid config → engine refuses to emit non-silence (`INVALID_CONFIG`). No silent clamp of illegal values except documented bearing wrap.

## Migration

`echo-config.v1` is the first version. Future versions must keep a reader that understands v1. Unknown keys are rejected.

## Safe defaults

Competition and Android adapters cannot be turned on from this JSON. Those are code flags, not config keys, on purpose.
