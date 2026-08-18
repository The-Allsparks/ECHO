# ECHO Priority Ledger

**Updated:** 2026-08-17  
**Audited commit:** `b09385d` (`main` after PR #1)

| Issue | Priority | Readiness | Dependencies | Status | Subagent | Branch | PR | CI | Merge | Blocker | Next action |
| ----- | -------- | --------- | ------------ | ------ | -------- | ------ | -- | -- | ----- | ------- | ----------- |
| Merge PR #1 (Phase -1/0/1) | P0 | Done | — | **merged** | — | `0.1-feasibility-and-core` | [#1](https://github.com/The-Allsparks/ECHO/pull/1) | SUCCESS | **merged** `b09385d` | — | — |
| #3 Required CI checks | P0 | Ready | PR #1 merged | **in-progress** | orchestrator | docs branch | pending | — | protection applied | — | Document in CONTRIBUTING; close after docs PR |
| #4 Phase 2 ViDAR + TRACE replay | P1 | Ready | PR #1 merged | backlog | [reviewed](db198001-0f3e-402b-9e6c-5b3d6655d458) | — | — | — | — | wait for #3 PR | Implement after #3 docs PR resolves |
| #5 Draft FTC Q&A | P1 | Ready | None | backlog | — | — | — | — | — | Lead Coach for submit | Docs-only |
| Kickoff manual re-verification | P0 | Blocked | 12 Sep 2026 | backlog | — | — | — | — | — | Kickoff date | Wait |
| Phase 4 Driver Hub audio spike | P2 | Blocked | Kickoff + Q&A + hardware | blocked | — | — | — | — | — | Approval gate | Do not start |
| Dependabot configuration | P3 | Done | — | **resolved** | — | — | #1 | — | present on `main` | — | `.github/dependabot.yml` already merged |

## Priority model

1. Safety blockers → 2. Correctness blockers → 3. CI/build → 4. Dependency-unblockers → 5. Architecture seams → 6. Tests for upcoming work → 7. User-facing slices → 8. Performance (measured) → 9. Docs/usability → 10. Cleanup

## Active subagent rule

**MAX_ACTIVE_SUBAGENTS=1.** Issue #3 GitHub protection applied by orchestrator; docs PR in progress. Phase 2 waits until that PR resolves.
