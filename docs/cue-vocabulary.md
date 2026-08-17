# Cue vocabulary

Initial competition-oriented vocabulary (**proposal**, not match-approved). Stereo pan is **not** 3D localization.

## Dimension ownership

| Audio dimension | Owns | Must not also encode |
| --------------- | ---- | -------------------- |
| Stereo pan | Horizontal bearing | Distance, confidence, class |
| Pulse interval | Closing distance **or** alignment error (profile chooses one) | Bearing |
| Pitch | Optional, bounded, **default off** | Everything else |
| Timbre / family | Cue category | Fine-grained state |
| Gain envelope | Safety and onsets | Meaning |
| Silence | No useful trusted action | — |

Do not change these mappings between modes without an explicit, communicated profile change.

## Families

| Family | Meaning | Sound character (training) |
| ------ | ------- | -------------------------- |
| `GUIDANCE` | Selected target bearing + proximity/align | Soft pulse, equal-power pan |
| `CONFIRM_ACQUIRE` | Acquisition | Short rising chirp |
| `CONFIRM_ALIGN` | Alignment success | Short rising chirp (same family, shorter) |
| `CONFIRM_READY` | Mechanism ready | Neutral blip |
| `CONFIRM_COMPLETE` | Cycle complete | Neutral double blip |
| `WARN_AMPER` | Power | Distinct low, rate-limited |
| `WARN_MIMIC` | Mechanism fault | Distinct mid, rate-limited |
| `WARN_BEACON` | Communications | Distinct high-sparse, rate-limited |
| `SILENCE` | No cue | Nothing |

Spoken narration is **not** in the initial vocabulary.

## Silence reasons

`DISABLED`, `NO_TARGET`, `STALE`, `LOW_CONFIDENCE`, `UNKNOWN_INPUT`, `MISSING_CAPABILITY`, `MISSING_AUDIO`, `CONTRADICTORY`, `INVALID_CONFIG`, `RATE_LIMITED`, `NO_USEFUL_ACTION`, `RENDERER_FAILURE`.

## Priority (high to low)

1. `WARN_*`
2. `CONFIRM_*`
3. `GUIDANCE`
4. `SILENCE`

One guidance cue at a time. Warnings preempt guidance. Confirms are short and cooldown-gated.
