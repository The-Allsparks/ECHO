# ADR 0006: Feature flags and approval gates

- Status: Accepted
- Date: 2026-08-17

## Decision

`EchoFeatureFlags` defaults all I/O and integration enables to false. Phase 4–7 are documentation gates, not automatic code paths in default config.

## Why

Creating a library is not approval to use it in a MATCH. Matches Allsparks AMPER/MIMIC/HELM flag style.

## Consequences

Desktop `--audio` is an explicit opt-in for training only. Competition profile does not exist as an on-by-default file.
