## Summary
- Completes Phase -1 feasibility research for BIOBUZZ Competition Manual V0 (31 Jul 2026) with cited primary sources.
- Records feasibility decision: **CONDITIONAL GO** — desktop/training only; **no approved match audio path**.
- Implements Phase 0 deterministic core (`org.allsparks.echo`): selection, sonification numbers, silence policy, fake/no-op renderers.
- Implements Phase 1 desktop training UI with **speakers off by default** (`runDesktopTraining` / `--audio` opt-in only).
- Adds CI (`./gradlew check`) and 31 unit tests (all passing locally on Windows).

## What is NOT validated
- **Not** competition-legal
- **Not** Driver Hub audio
- **Not** Android renderer
- **Not** robot-tested
- **Not** claiming driving benefit

## Feasibility highlights
- REV-31-1596 specs list **no speaker and no headphone jack** (vendor docs).
- FTC wiki documents `SoundPlayer` and `Telemetry.speak` on DS/Hub, but transducer path is **unproven**.
- R704.D prohibits extra streaming (e.g. FTC Dashboard) during MATCH play.
- R706 prohibits modifying the official Driver Station app.
- Headset/USB audio accessory legality is **unspecified** in V0 — draft Q&A in `docs/research/draft-ftc-qa.md` (**not submitted**).

## Test plan
- [x] `./gradlew test` (Windows)
- [ ] CI on GitHub Actions after merge
- [ ] Manual desktop trainer smoke test (`./gradlew runDesktopTraining`)

## Maturity matrix (honest)
| Claim | Status |
| --- | --- |
| Researched | Yes |
| Desktop-tested | Partial (unit tests + trainer) |
| Simulated | Partial |
| Android / Driver Hub / Robot / Practice / Competition | **No** |
