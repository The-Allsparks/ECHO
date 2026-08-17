# Human factors

See research: [research/hearing-and-human-factors.md](research/hearing-and-human-factors.md).

Design rules in software:

- One guidance cue; warnings preempt; confirms are short.
- No continuous tone in the default profile.
- Immediate mute/disable on the snapshot (`driverEnabled=false`) and on the training UI.
- Conservative gain; hard cap; raised-cosine onsets.
- Distinct warning families (AMPER / MIMIC / BEACON).
- Rate limits and cooldowns to prevent chatter.
- Cue-recognition exercises in Phase 1 before any robot use.
- Do not claim performance benefit.

Venue noise is not solved by volume. If the gym is too loud for conservative gain, **silence** (and eyes on the field) is the correct fallback.
