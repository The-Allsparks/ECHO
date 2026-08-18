# Contributing to ECHO

ECHO is maintained by [The Allsparks](https://github.com/The-Allsparks) (FTC Team 36117) for our team and the wider FTC community.

## Setup

```powershell
git clone https://github.com/The-Allsparks/ECHO.git
cd ECHO
.\gradlew.bat test
```

On Linux/macOS:

```bash
./gradlew test
```

Optional desktop training UI (no competition audio; speakers remain off unless you pass `--audio`):

```powershell
.\gradlew.bat runDesktopTraining
```

## Rules of engagement

1. **ECHO never commands robot hardware.** Pull requests must not add motor, servo, path, or autonomous command paths.
2. **Do not enable physical or competition audio** without the matching approval gate in `docs/feasibility-decision.md` and `docs/competition-readiness.md`.
3. Distinguish **verified fact**, **engineering inference**, and **untested hypothesis** in documentation. Cite primary sources for rules, platform, safety, and compatibility claims.
4. Do not treat desktop audio success as Driver Hub compatibility, and do not treat Driver Hub compatibility as competition legality.
5. Do not modify the official Driver Station or Robot Controller applications in this repository.
6. Do not add an unauthorized wireless protocol.
7. Missing, stale, unknown, or low-confidence data must suppress cues. Silence is a valid output.
8. Do not commit secrets, Wi-Fi passwords, tokens, or student PII.

## Pull requests

- Prefer small, reviewable PRs.
- Include motivation, phase impact, test evidence, rules notes, and hearing-safety notes.
- Update maturity language in `README.md` when a claim changes.
- Run `.\gradlew.bat test` (or `./gradlew test`) before requesting review.

### Required checks on `main`

Pull requests into `main` must pass these GitHub Actions jobs before merge:

- `test (ubuntu-latest)`
- `test (windows-latest)`
- `docs-structure`

The branch must also be up to date with `main`. Do not bypass required checks.

## Line endings

The repository stores LF line endings (see [.gitattributes](.gitattributes)).

## License

Contributions are accepted under the MIT License ([LICENSE](LICENSE)). No CLA is required.
