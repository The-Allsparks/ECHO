# ADR 0002: Simple stereo, not HRTF

- Status: Accepted
- Date: 2026-08-17

## Decision

Use equal-power intensity panning and pulse timing. Do not adopt HRTF, OpenAL, or binaural rendering in the core.

## Why

Teachability, deterministic tests, and no documented 3D audio device on the Driver Hub. Stereo pan is a left/right intensity cue, not full spatialization.

## Consequences

Documentation must not claim 3D localization. One-ear listening cannot use pan as designed.
