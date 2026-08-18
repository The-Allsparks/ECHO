# TRACE integration

TRACE owns recording schemas and replay. ECHO emits `EchoDecisionRecord` with the fields in [testing.md](testing.md) / observability list. Core does not depend on TRACE JARs.

Minimum record: selected cue, source, rejections, input age/confidence, selection nanos, renderer name, device status, driver enable, config version, flags.

Replay: feed recorded snapshots into `EchoEngine` with `FakeClock`. Identical snapshots + config + clock → identical decisions (Phase 0 invariant).

Flag `traceExport` default **false** (records still exist in-memory for tests).
