# ECHO

**Environmental Cue and Heading Output**

ECHO is a Driver Station auditory-rendering and directional-guidance framework for FIRST Tech Challenge.

> ViDAR determines where things are. ECHO turns selected robot information into sound for the driver.

ECHO presents selected, timestamped robot-state information to a human driver. It is not a vision system, not a microphone or sonar system, not an autonomous planner, not a robot controller, and not a replacement for Driver Station telemetry.

**ECHO never commands robot hardware.** It does not replace [ViDAR](https://github.com/The-Allsparks/ViDAR) or [HELM](https://github.com/The-Allsparks/HELM). It is **disabled by default**.

Built by **[The Allsparks](https://github.com/The-Allsparks)** (FTC Team **36117**).

Repository: **[The-Allsparks/ECHO](https://github.com/The-Allsparks/ECHO)**

> **Disclaimer:** ECHO is community-developed and unofficial. It is **not** affiliated with or endorsed by FIRST, REV Robotics, Sony, Logitech, or other referenced vendors. Teams must verify legality and performance against the current-season FTC Competition Manual.

---

## Current maturity

| Claim | Status |
| ----- | ------ |
| Researched | **Yes** — Phase -1 complete as of 2026-08-17 using the BIOBUZZ Competition Manual V0 and cited platform docs |
| Desktop-tested | **Partial** — Phase 0 unit tests and Phase 1 desktop simulator compile and run on a development machine |
| Simulated | **Partial** — synthetic bearing/distance scenarios only |
| Android-tested | **No** |
| Driver Hub-tested | **No** |
| Robot-tested | **No** |
| Practice-field-tested | **No** |
| Competition-approved | **No** |

**Feasibility decision:** `CONDITIONAL GO`. See [docs/feasibility-decision.md](docs/feasibility-decision.md).

Honest limits:

- Desktop audio success does **not** prove Driver Hub compatibility.
- Driver Hub compatibility does **not** prove competition legality or readiness.
- Competition use requires current-season verification, hardware validation, hearing-safety review, and team approval.
- ECHO does **not** claim that auditory guidance improves driving. That claim requires a Phase 6 experiment.

| Item | Status |
| ---- | ------ |
| **Version** | `0.1.0-SNAPSHOT` |
| **Implemented phases** | Phase -1 research; Phase 0 deterministic core; Phase 1 desktop training (audio off by default) |
| **Phases 2–7** | Designed / gated / not implemented as active capabilities |
| **Physical match audio** | **Disabled.** Do not enable. |
| **FTC SDK / Android renderer** | **Not in this library.** |

---

## What ECHO is not

- A vision system.
- A microphone or acoustic sensing system.
- An ultrasonic or sonar system.
- An autonomous planner.
- A robot controller.
- A path-following library.
- A replacement for Driver Station telemetry.
- A general-purpose soundboard.
- A system that commands robot hardware.

---

## Quick start (desktop)

```powershell
git clone https://github.com/The-Allsparks/ECHO.git
cd ECHO
.\gradlew.bat test
```

On Linux/macOS:

```bash
./gradlew test
```

Launch the off-field training UI with speakers muted:

```powershell
.\gradlew.bat runDesktopTraining
```

The UI can optionally play desktop tones if you pass `--audio`. That is a **training** path only. It does not prove Driver Hub or competition use.

---

## Design principles

1. **Research before architecture and implementation.** Rules and platform facts are cited.
2. **Silence is valid.** Missing, stale, unknown, or untrusted data suppresses cues.
3. **One selected guidance cue.** Warnings may preempt. ECHO does not sonify everything the robot knows.
4. **The driver selects the objective.** ECHO does not silently pick a strategic target.
5. **Replaceable layers.** Core selection has no FTC SDK or Android dependency.
6. **Feature flags and approval gates.** Active capabilities default off.
7. **Immediate disable.** Muting ECHO must not affect robot operation.
8. **Integrate; do not replace.** ViDAR, Pedro Pathing, MIMIC, AMPER, BEACON, TRACE, and HELM keep their ownership.

---

## Documentation

| Doc | Purpose |
| --- | ------- |
| [Feasibility decision](docs/feasibility-decision.md) | Go / Conditional Go / Training Only / No-Go |
| [Architecture](docs/architecture.md) | Layers, contracts, and non-goals |
| [Cue vocabulary](docs/cue-vocabulary.md) | What each sound is allowed to mean |
| [Human factors](docs/human-factors.md) | Attention, overload, and accessibility |
| [Hearing safety](docs/hearing-safety.md) | Volume, exposure, and mute |
| [Driver training](docs/driver-training.md) | Off-field practice |
| [Configuration](docs/configuration.md) | Versioned config and safe defaults |
| [Testing](docs/testing.md) | Deterministic tests vs listening |
| [Hardware validation](docs/hardware-validation.md) | Gated physical checks |
| [Competition readiness](docs/competition-readiness.md) | Why ECHO is not match-ready |
| [Student learning path](docs/student-learning-path.md) | Lessons by phase |
| [Mentor guide](docs/mentor-guide.md) | How to teach and gate the work |
| [References](docs/references.md) | Citation table |
| [Research](docs/research/) | Phase -1 source notes |

---

## Ecosystem

| Project | Owns | ECHO may consume |
| ------- | ---- | ---------------- |
| [ViDAR](https://github.com/The-Allsparks/ViDAR) | Visual detection and spatial observations | Selected target bearing, distance, confidence, age |
| Pedro Pathing | Localization and chassis motion | Nothing that would become a motion command |
| [MIMIC](https://github.com/The-Allsparks/MIMIC) | Mechanism lifecycle | Ready / acquire / complete / fault events |
| [AMPER](https://github.com/The-Allsparks/AMPER) | Electrical observation | Power warnings |
| [BEACON](https://github.com/The-Allsparks/BEACON) | Communication health | Link warnings |
| [TRACE](https://github.com/The-Allsparks/TRACE) | Recording and replay | Decision records |
| [HELM](https://github.com/The-Allsparks/HELM) | Intent and bounded task selection | Explicit selected objective, only if enabled |

These projects are **not** compile-time requirements for the ECHO core.

---

## License

MIT — same open-source license family as [ViDAR](https://github.com/The-Allsparks/ViDAR) and [AMPER](https://github.com/The-Allsparks/AMPER). See [LICENSE](LICENSE).

## Contributing

See [CONTRIBUTING.md](CONTRIBUTING.md), [CODE_OF_CONDUCT.md](CODE_OF_CONDUCT.md), and [SECURITY.md](SECURITY.md).
