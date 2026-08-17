# Driver training

Off-field only. Desktop audio is optional (`--audio`).

## Goals

Students can identify left / center / right and approximate near / far from pan and pulse, and can explain why a trial was silence.

## Exercises

1. **Pan check:** target at −90°, 0°, +90°. Report left/center/right. Visual panel must agree.
2. **Pulse check:** distance 0.3 m vs 2.0 m. Report which is nearer.
3. **Stale check:** age above threshold → expect silence and reason `STALE`.
4. **Disable check:** uncheck driver enable → silence, robot-control-free.
5. **Warning preemption:** inject AMPER warning over guidance → warning family wins.

Record recognition accuracy in the training UI metrics panel. These metrics are **training diagnostics**, not evidence of match benefit.

## Profiles

`training-default.json` uses the same mappings as the core defaults. Do not create a “competition loud” profile.
