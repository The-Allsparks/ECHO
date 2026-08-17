# ADR 0004: Silence is valid output

- Status: Accepted
- Date: 2026-08-17

## Decision

Missing, stale, unknown, low-confidence, contradictory, disabled, or device-less inputs yield `SILENCE` with an explicit reason. Callers must not treat silence as a bug.

## Why

A confident wrong direction is worse than no sound. Human-factors and safety charter require suppression.

## Consequences

Tests assert silence reasons. Renderers must be able to output true quiet.
