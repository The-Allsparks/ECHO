# Configuration

Schema version: `echo-config.v1`

Human-readable copy: `config/echo-default.json`. The same JSON is shipped on the classpath as `org/allsparks/echo/echo-default.json` and is what JAR consumers load. Validation is performed in `EchoConfig.validate()`.

## Loading

```java
EchoConfig config = EchoConfig.loadDefault();          // classpath copy (works from a JAR)
EchoConfig fromFile = EchoConfig.fromPath(path);       // student-edited JSON on disk
EchoConfig parsed = EchoConfig.parseJson(jsonString);  // already-read text
```

`EchoConfig.defaults()` is the in-code equivalent of the shipped JSON. Tests require `loadDefault()`, `fromPath(config/echo-default.json)`, and `defaults()` to match field-for-field.

Missing files, unreadable resources, unsupported `schemaVersion`, and out-of-range fields return a config with `valid() == false`. They do not throw.

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

`echo-config.v1` is the first version. Future versions must keep a reader that understands v1. The v1 regex parser ignores unknown keys; it does not reject them. Unsupported `schemaVersion` values still make `valid()` false.

## Safe defaults

Competition and Android adapters cannot be turned on from this JSON. Those are code flags, not config keys, on purpose.
