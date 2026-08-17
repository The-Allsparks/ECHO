# ADR 0005: Explicit selected target

- Status: Accepted
- Date: 2026-08-17

## Decision

ECHO renders only `EchoSnapshot.selectedTarget()`. It does not choose among ViDAR tracks unless a separately approved bounded policy is enabled in a future adapter.

## Why

Strategic target choice belongs to the driver or HELM. Scanning every observation would make ECHO a planner.

## Consequences

Adapters must pass an explicit id. HELM source is flag-gated and default off.
