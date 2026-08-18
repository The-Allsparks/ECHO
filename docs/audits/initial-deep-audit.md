# ECHO — Initial Deep Audit

**Date:** 2026-08-17  
**Audited commit:** `084d8b0df641f9761af26913e5045b269e45dea7`  
**Branch:** `0.1-feasibility-and-core` (DRAFT PR #1 → `main`)  
**Auditor identity:** TA-C-GHill  
**Repository:** [The-Allsparks/ECHO](https://github.com/The-Allsparks/ECHO)

---

## Executive summary

ECHO is a well-scoped **presentation-only** auditory guidance framework for FTC drivers. Phase -1 research, Phase 0 deterministic core, and Phase 1 desktop training are implemented on branch `0.1-feasibility-and-core` with **31 unit tests passing** locally and on CI (Ubuntu + Windows). The project honestly documents **CONDITIONAL GO** feasibility: off-field training is allowed; **match audio is not approved**.

**Highest-priority blockers before further feature work:**

1. **Merge PR #1** — all Phase -1/0/1 work is stranded on a draft branch; `main` contains only governance files.
2. **Branch protection lacks required CI checks** — protection exists but does not require the `CI` workflow to pass before merge.
3. **`vidarAdapter` flag not enforced** — documented rollback strategy contradicts current behavior (guidance works with flag off).

**Safe to proceed after merge:** Phase 2 ViDAR adapter + TRACE replay contract (desktop-only, no hardware).

---

## Project purpose

ECHO converts **already-selected**, timestamped robot-state snapshots into at most **one guidance cue** plus rare warnings for a human driver. It is explicitly **not** vision, planning, control, or telemetry replacement.

| Intended users | Maturity fit |
| -------------- | ------------ |
| Beginning FTC students | Phase 0 tests + student learning path |
| Advanced students / integrators | Adapter contracts (ViDAR, MIMIC, etc.) |
| Mentors | Feasibility gates, hearing safety, competition readiness docs |
| Downstream library maintainers | Versioned DTO contracts without compile-time coupling |

---

## Current maturity

| Phase | Status | Evidence |
| ----- | ------ | -------- |
| -1 Research | **Complete** | `docs/research/*`, `docs/feasibility-decision.md`, cited V0 manual |
| 0 Deterministic core | **Complete (PR #1)** | `EchoEngine`, `CueSelector`, 24 core tests |
| 1 Desktop training | **Complete (PR #1)** | `DesktopTrainingApp`, `runDesktopTraining`, audio off by default |
| 2 ViDAR/TRACE replay | **Designed, not implemented** | Stub `VidarObservation`, doc-only TRACE contract |
| 3 MIMIC/AMPER/BEACON | **Selection logic only** | Flags gate warnings; no live adapters |
| 4+ Hardware / competition | **Blocked** | Feasibility decision, feature-flag throws |

---

## Implemented capabilities

- Immutable `EchoSnapshot` with distinct `Presence` for unknown/stale/zero
- Priority-based cue selection with hysteresis, cooldowns, rate limits
- Sonification mappers (pan, pulse, pitch, gain) — numbers only in core
- Renderers: `NoOpRenderer`, `FakeRenderer`, `DesktopToneRenderer`, `FailingRenderer`
- `EchoDecisionRecord` with TRACE-shaped observability fields
- `EchoConfig` v1 with validation and regex JSON parser
- `EchoFeatureFlags` with hard throws for gated Android/FTC/competition paths
- CI: compile + test on Ubuntu/Windows, docs structure check
- Comprehensive documentation tree (30+ docs)

---

## Documented but unimplemented capabilities

| Capability | Severity | Type |
| ---------- | -------- | ---- |
| `vidarAdapter` flag enforcement | HIGH | CORRECTNESS |
| `traceExport` JSON/file export | MEDIUM | ARCHITECTURE |
| `echo-replay.v0` fixture format | HIGH | TESTING |
| `eligibility` package (docs say separate layer) | LOW | DOCUMENTATION |
| Load `config/echo-default.json` from classpath | MEDIUM | USABILITY |
| `sourceId` / `frame` fields in ViDAR contract | MEDIUM | INTEGRATION |
| Android / FTC renderer | BLOCKED | COMPATIBILITY |
| Required CI status checks on `main` | HIGH | SECURITY |

---

## Architecture findings

| ID | Severity | Type | Finding |
| -- | -------- | ---- | ------- |
| A1 | LOW | ARCHITECTURE | `docs/architecture.md` lists `eligibility` package; logic lives in `CueSelector` |
| A2 | MEDIUM | ARCHITECTURE | `CueSelector` holds mutable hysteresis/cooldown state — correct for runtime; replay needs fresh engine per session (tests already do) |
| A3 | HIGH | ARCHITECTURE | `vidarAdapter` flag declared but never read in selection path |
| A4 | MEDIUM | ARCHITECTURE | `traceExport` flag unused — no export hook in `EchoEngine` |
| A5 | INFORMATIONAL | ARCHITECTURE | `queueDepth` / `droppedCues` always 0 — placeholders for future renderer queue |
| A6 | LOW | ARCHITECTURE | `DesktopTrainingApp` bypasses `desktopAudioPlayback` flag; passes renderer directly |

**Dependency direction:** Clean. Core depends only on JDK 11 + JUnit. No sibling Allsparks JARs.

---

## Correctness findings

| ID | Severity | Type | Finding |
| -- | -------- | ---- | ------- |
| C1 | HIGH | CORRECTNESS | `BOUNDED_ADAPTER` snapshots produce guidance with `EchoFeatureFlags.disabled()` — contradicts roadmap rollback and HELM gate pattern |
| C2 | MEDIUM | CORRECTNESS | `EchoConfig.parseJson` uses regex parsing — adequate for controlled fixtures; fragile for arbitrary JSON |
| C3 | LOW | CORRECTNESS | `VidarObservationTest` only covers happy path |
| C4 | INFORMATIONAL | CORRECTNESS | Age calculation uses integer ms truncation — acceptable for 250 ms threshold |

---

## Safety findings

| ID | Severity | Type | Finding |
| -- | -------- | ---- | ------- |
| S1 | INFORMATIONAL | SAFETY | No motor/servo/command paths — verified by package scan |
| S2 | INFORMATIONAL | SAFETY | `competitionProfile`, `androidRenderer`, `ftcOutputAdapter` throw on build — good fail-closed |
| S3 | INFORMATIONAL | SAFETY | Renderer failures caught; engine mutes renderer; decision records failure |
| S4 | INFORMATIONAL | SAFETY | Default gain 0.08, max 0.20 — conservative |
| S5 | BLOCKED | SAFETY | Match audio paths correctly gated pending Phase 4 approval |

**Passive mode verified:** `NoOpRenderer` / disabled driver → silence without exceptions.

---

## Performance findings

| ID | Severity | Type | Finding |
| -- | -------- | ---- | ------- |
| P1 | INFORMATIONAL | PERFORMANCE | Selection is O(1) per step; no allocations in hot path beyond rejection list |
| P2 | INFORMATIONAL | PERFORMANCE | No benchmarks exist — acceptable at current maturity |
| P3 | LOW | PERFORMANCE | `DesktopToneRenderer` may allocate PCM buffers — desktop-only, not on Control Hub |

---

## API and usability findings

| ID | Severity | Type | Finding |
| -- | -------- | ---- | ------- |
| U1 | MEDIUM | USABILITY | `config/echo-default.json` exists but no public loader API |
| U2 | LOW | USABILITY | `EchoEngine.phase0()` factory is clear entry point for tests |
| U3 | INFORMATIONAL | USABILITY | Student learning path and mentor guide align with implemented phases |

---

## Testing findings

| ID | Severity | Type | Finding |
| -- | -------- | ---- | ------- |
| T1 | INFORMATIONAL | TESTING | 31 tests across 5 classes — matches README claim |
| T2 | MEDIUM | TESTING | No file-based replay fixtures despite documented TRACE contract |
| T3 | INFORMATIONAL | TESTING | `DocLinkCheckerTest` validates doc link integrity |
| T4 | INFORMATIONAL | TESTING | Deterministic replay tested in-memory (`deterministicReplayIdentical`) |
| T5 | INFORMATIONAL | TESTING | CI runs on Java 17; library targets Java 11 — compatible but undocumented |

---

## Documentation findings

| ID | Severity | Type | Finding |
| -- | -------- | ---- | ------- |
| D1 | LOW | DOCUMENTATION | Architecture table references nonexistent `eligibility` package |
| D2 | INFORMATIONAL | DOCUMENTATION | Maturity matrix in README is honest and accurate |
| D3 | MEDIUM | DOCUMENTATION | ViDAR doc lists `sourceId`/`frame` not present in Java DTO |
| D4 | INFORMATIONAL | DOCUMENTATION | Feasibility decision cites primary sources with dates |

---

## Dependency and supply-chain findings

| ID | Severity | Type | Finding |
| -- | -------- | ---- | ------- |
| DEP1 | MEDIUM | SECURITY | No Dependabot configuration |
| DEP2 | LOW | SECURITY | GitHub Actions use `@v4` tags (not SHA-pinned) |
| DEP3 | INFORMATIONAL | COMPATIBILITY | Zero runtime dependencies beyond JDK — excellent for FTC embedding |
| DEP4 | LOW | COMPATIBILITY | Gradle 8.7 wrapper present; no vulnerability scan in CI |

---

## Repository-health findings

| ID | Severity | Type | Finding |
| -- | -------- | ---- | ------- |
| R1 | **HIGH** | SECURITY | Branch protection on `main` lacks **required status checks** for CI workflow |
| R2 | HIGH | ARCHITECTURE | Feature work on draft PR #1; `main` stale (governance only) |
| R3 | MEDIUM | DOCUMENTATION | Milestones and labels exist; **zero GitHub issues** created (roadmap script not run) |
| R4 | INFORMATIONAL | DOCUMENTATION | Issue template at `.github/ISSUE_TEMPLATE/phase_work.md` |
| R5 | INFORMATIONAL | DOCUMENTATION | No releases published yet (appropriate for 0.1.0-SNAPSHOT) |
| R6 | LOW | DOCUMENTATION | Untracked `branch-protection-audit/` folder in worktree (preserve, do not commit secrets) |

**Branch protection (2026-08-17):** enforce admins, block force push, require conversation resolution; **0 required reviews**, **no required checks**.

---

## Cross-project integration findings

| Project | ECHO relationship | Status |
| ------- | ----------------- | ------ |
| ViDAR | Consumes selected target DTO | Stub adapter; contract v0 documented |
| TRACE | Emits decision records | In-memory only; no export |
| MIMIC/AMPER/BEACON | Warning/confirm flags on snapshot | Selection logic present; flags default off |
| HELM | Target source gated by flag | **Correctly enforced** |
| Pedro Pathing | No consumption | Clean boundary |
| Robot application | Team-owned adapter wiring | Out of scope for core |

**Circular dependencies:** None identified.

---

## Readiness assessment

| Gate | Met? |
| ---- | ---- |
| Phase -1 research | Yes |
| Phase 0 deterministic core | Yes (pending merge) |
| Phase 1 desktop training | Yes (pending merge) |
| Phase 2 integration replay | No — blocked on PR #1 merge |
| Phase 4 hardware | No — correctly blocked |
| Competition approval | No — correctly blocked |

---

## Recommended work order

1. **Merge PR #1** — land Phase -1/0/1 on `main`
2. **Add required CI checks to branch protection** — prevent unvalidated merges
3. **Run roadmap script / create GitHub backlog** — track epics and slices
4. **Phase 2: ViDAR adapter + TRACE replay** — flag gate, fixtures, golden replay tests
5. **Draft FTC Q&A maintenance** — ready now; submission blocked until Lead Coach + 28 Sep 2026
6. **Kickoff manual re-verification** — blocked until 12 Sep 2026
7. **Dependabot + supply-chain hygiene**
8. **Phase 4 hardware spike** — blocked on rules + mentor approval + hardware

---

## Deferred or rejected ideas

| Idea | Disposition | Reason |
| ---- | ----------- | ------ |
| Enable match audio by default | **Rejected** | No approved transducer path |
| Compile-time ViDAR dependency | **Rejected** | ADR 0003 / architecture boundary |
| Separate eligibility package (now) | **Deferred** | Logic works in `CueSelector`; extract only if package grows |
| Desktop UI replay panel | **Deferred** | Phase 2 file replay is higher leverage |

---

## Evidence and references

- Source: `src/main/java/org/allsparks/echo/` (41 Java files)
- Tests: `src/test/java/org/allsparks/echo/` (31 test methods)
- CI: `.github/workflows/ci.yml` — runs 32083416988 SUCCESS
- PR: https://github.com/The-Allsparks/ECHO/pull/1
- Feasibility: `docs/feasibility-decision.md`
- ADRs: `docs/adr/0001` through `0006`

---

*Next audit trigger: after Phase 2 merge or substantial architecture change.*
