# ADR 0003: No FTC SDK in core

- Status: Accepted
- Date: 2026-08-17

## Decision

`org.allsparks.echo` compiles against JDK 11 only. FTC, Android, and sibling Allsparks libraries are optional adapters, not Gradle dependencies of the core artifact.

## Why

Matches Allsparks AMPER/ViDAR “pure Java first” practice. Allows CI on desktop. Prevents accidental robot-control APIs.

## Consequences

TeamCode bridges live in examples or future adapter modules behind flags.
