# MIMIC integration

MIMIC owns mechanism lifecycle. ECHO may sonify **events**, not command actuators.

Optional snapshot fields: `mimicReady`, `mimicAcquire`, `mimicComplete`, `mimicFault` (booleans with presence).

| Event | Cue family |
| ----- | ---------- |
| Ready edge | `CONFIRM_READY` |
| Acquire edge | `CONFIRM_ACQUIRE` |
| Complete edge | `CONFIRM_COMPLETE` |
| Fault | `WARN_MIMIC` |

Missing MIMIC → those candidates absent. Faults preempt guidance. Cooldown applies. Flag `mimicAdapter` default **false**.
