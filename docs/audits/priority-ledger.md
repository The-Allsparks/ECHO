# ECHO Priority Ledger

**Updated:** 2026-08-17  
**Audited commit:** `084d8b0`

| Issue | Priority | Readiness | Dependencies | Status | Subagent | Branch | PR | CI | Merge | Blocker | Next action |
| ----- | -------- | --------- | ------------ | ------ | -------- | ------ | -- | -- | ----- | ------- | ----------- |
| Merge PR #1 (Phase -1/0/1) | P0 | Ready | CI green | **ready-to-merge** | — | `0.1-feasibility-and-core` | [#1](https://github.com/The-Allsparks/ECHO/pull/1) | SUCCESS (ubuntu, windows, docs-structure) | **Pending human approval** | `AUTOMATIC_MERGE=false` | Approve and merge |
| Add required CI checks to branch protection | P0 | Ready | PR #1 merge optional | backlog | — | — | — | — | — | — | Create issue; implement via GitHub API |
| Phase 2 ViDAR + TRACE replay | P1 | Blocked | PR #1 merge | backlog | [reviewed](db198001-0f3e-402b-9e6c-5b3d6655d458) | — | — | — | — | PR #1 | Single-issue vertical slice approved |
| Draft FTC Q&A maintenance | P1 | Ready | None | backlog | — | — | — | — | — | Lead Coach for submit | Docs-only |
| Kickoff manual re-verification | P0 | Blocked | 12 Sep 2026 | backlog | — | — | — | — | — | Kickoff date | Wait |
| Phase 4 Driver Hub audio spike | P2 | Blocked | Kickoff + Q&A + hardware | blocked | — | — | — | — | — | Approval gate | Do not start |
| Dependabot configuration | P2 | Ready | PR #1 merge | backlog | — | — | — | — | — | — | Add `.github/dependabot.yml` |

## Priority model

1. Safety blockers → 2. Correctness blockers → 3. CI/build → 4. Dependency-unblockers → 5. Architecture seams → 6. Tests for upcoming work → 7. User-facing slices → 8. Performance (measured) → 9. Docs/usability → 10. Cleanup

## Active subagent rule

**MAX_ACTIVE_SUBAGENTS=1.** Phase 2 scope review complete; no implementation subagent until PR #1 resolves.
