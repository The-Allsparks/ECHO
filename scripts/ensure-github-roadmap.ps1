function Ensure-Issue {
    param(
        [string]$Title,
        [string]$Body,
        [string[]]$Labels,
        [string]$Milestone
    )
    $repo = "The-Allsparks/ECHO"
    $existing = gh issue list --repo $repo --state all --search "in:title `"$Title`"" --json number,title --jq ".[] | select(.title==`"$Title`") | .number"
    if ($existing) {
        Write-Host "reuse issue #$existing $Title"
        return [int]$existing
    }
    $labelArg = ($Labels | ForEach-Object { "--label"; $_ }) -join ' '
    $cmd = "gh issue create --repo $repo --title `"$Title`" --body-file - $labelArg --milestone `"$Milestone`""
    $num = $Body | Invoke-Expression $cmd
    Write-Host "created $Title -> $num"
    return $num
}

$epic1 = @'
## Problem
ECHO needs a source-backed Phase -1 record before any match-audio architecture is claimed.

## Student learning objective
Students can distinguish verified FTC facts, engineering inference, and unknowns requiring Q&A or hardware.

## Scope
Maintain research docs, references, feasibility decision, and Kickoff re-verification checklist.

## Out of scope
Submitting FTC Q&A without Lead Coach authorization. Claiming competition legality.

## Acceptance criteria
- [ ] Research docs cite primary sources with access dates
- [ ] Feasibility decision remains honest after Kickoff manual
- [ ] Draft Q&A questions exist but are not submitted

## Dependencies
None

## Readiness requirements
Access to current FTC resources and REV docs.

## Architecture impact
Gates all physical output phases.

## Implementation plan
Re-read manual after 12 Sep 2026; update docs; search Q&A after 28 Sep 2026.

## Validation plan
Maintainer review against primary sources only.

## Documentation required
docs/research/*, docs/feasibility-decision.md, docs/references.md

## Hardware validation required
- [ ] None for this slice

## Rules implications
High — defines what may be planned vs approved.

## Human-safety implications
Hearing and communication risks documented.

## Rollback or disable strategy
If rules forbid all paths, escalate to TRAINING ONLY or NO-GO in feasibility doc.

## Parent epic or roadmap link
Roadmap epic: ECHO Phase -1 Feasibility and Research
'@

Ensure-Issue -Title "Epic: ECHO Phase -1 Feasibility and Research" -Body $epic1 -Labels @('epic','status:in-progress','maturity:researched','type:research','priority:p0','risk:rules') -Milestone "0.1 Feasibility and Research"

$issues = @(
  @{
    Title = "Re-verify BIOBUZZ Kickoff manual and Team Updates"
    Milestone = "0.1 Feasibility and Research"
    Labels = @('status:backlog','maturity:researched','type:research','priority:p0','risk:rules')
    Body = @'
## Problem
V0 manual is pre-season; Kickoff manual may change operator-console and streaming rules.

## Student learning objective
Students learn that season rules are versioned and must be re-read.

## Scope
Update docs/research/ftc-rules-and-platform.md and docs/feasibility-decision.md after Kickoff.

## Out of scope
Implementing match audio.

## Acceptance criteria
- [ ] Kickoff manual version/date recorded
- [ ] R202, R704, R706, R711, R901-R904 re-checked
- [ ] Feasibility decision updated if needed

## Dependencies
Kickoff publication (12 Sep 2026)

## Readiness requirements
None

## Architecture impact
May block Phase 4+

## Implementation plan
Diff V0 vs Kickoff HTML/PDF; update references table.

## Validation plan
Two maintainers sign off on citations.

## Documentation required
docs/feasibility-decision.md, docs/references.md

## Hardware validation required
- [ ] None

## Rules implications
Authoritative

## Human-safety implications
R202.B unchanged or clarified

## Rollback or disable strategy
If no legal path, keep match audio disabled in flags.

## Parent epic or roadmap link
Epic: ECHO Phase -1 Feasibility and Research
'@
  },
  @{
    Title = "Draft FTC Q&A for headset and Driver Hub audio (do not submit without authorization)"
    Milestone = "0.1 Feasibility and Research"
    Labels = @('status:ready','maturity:researched','type:research','priority:p1','risk:rules')
    Body = @'
## Problem
Headset/USB audio legality is unspecified in V0.

## Student learning objective
Students learn to ask narrow, evidence-seeking Q&A questions.

## Scope
Maintain docs/research/draft-ftc-qa.md; submit only after Lead Coach approval post 28 Sep 2026.

## Out of scope
Autonomous Q&A submission.

## Acceptance criteria
- [ ] Four draft questions under 500 chars each
- [ ] Each maps to a blocked output path
- [ ] Submission requires explicit authorization note in issue

## Dependencies
Q&A system open date

## Readiness requirements
Lead Coach account

## Architecture impact
Unblocks Phase 4 path selection only if answered favorably

## Implementation plan
Search Q&A for duplicates before any submission.

## Validation plan
Mentor review of wording

## Documentation required
docs/research/draft-ftc-qa.md

## Hardware validation required
- [ ] None

## Rules implications
Critical

## Human-safety implications
Headset communication impact noted

## Rollback or disable strategy
If answers are negative, remain TRAINING ONLY for audio

## Parent epic or roadmap link
Epic: ECHO Phase -1 Feasibility and Research
'@
  },
  @{
    Title = "Epic: ECHO Phase 0 Deterministic Core"
    Milestone = "0.2 Deterministic Core"
    Labels = @('epic','status:in-progress','maturity:desktop','type:architecture','priority:p0','runtime:desktop')
    Body = @'
## Problem
Students need explainable cue selection without hardware or FTC SDK.

## Student learning objective
Explain why a cue was eligible, selected, rejected, suppressed, or replaced by silence.

## Scope
Core contracts, selector, sonification numbers, fake/no-op renderers, unit tests.

## Out of scope
Android audio, robot integration, ViDAR compile dependency.

## Acceptance criteria
- [ ] 31+ deterministic tests pass on Windows and Linux CI
- [ ] Silence reasons explicit
- [ ] No FTC SDK dependency

## Dependencies
Phase -1 complete

## Readiness requirements
JDK 11+

## Architecture impact
Foundation for all adapters

## Implementation plan
Maintain org.allsparks.echo pure Java module.

## Validation plan
./gradlew test

## Documentation required
docs/architecture.md, docs/cue-vocabulary.md, docs/student-learning-path.md

## Hardware validation required
- [ ] None

## Rules implications
None (off-field)

## Human-safety implications
Conservative default gain in config

## Rollback or disable strategy
EchoFeatureFlags defaults off for I/O

## Parent epic or roadmap link
Roadmap
'@
  },
  @{
    Title = "Epic: ECHO Phase 1 Desktop Training"
    Milestone = "0.3 Desktop Training"
    Labels = @('epic','status:in-progress','maturity:desktop','type:usability','priority:p1','runtime:desktop')
    Body = @'
## Problem
Drivers need off-field pan/pulse recognition practice without implying Driver Hub support.

## Student learning objective
Identify direction and approximate approach state from ECHO cues with visual confirmation.

## Scope
DesktopTrainingApp, metrics, docs/driver-training.md. Audio off unless --audio.

## Out of scope
Match audio, Android renderer.

## Acceptance criteria
- [ ] UI shows bearing, distance, selected cue, explanation
- [ ] runDesktopTraining works with speakers muted by default
- [ ] README states desktop != Driver Hub

## Dependencies
Phase 0 core

## Readiness requirements
Desktop JVM

## Architecture impact
Uses DesktopToneRenderer behind explicit flag

## Implementation plan
Extend scenario playback in a later slice.

## Validation plan
Manual quiet-room check + unit tests for PCM generation

## Documentation required
docs/driver-training.md, examples/phase1-desktop.md

## Hardware validation required
- [ ] None

## Rules implications
None

## Human-safety implications
Low default gain; mute button

## Rollback or disable strategy
Default --no-audio

## Parent epic or roadmap link
Roadmap
'@
  },
  @{
    Title = "Phase 4 Driver Hub audio spike (approval gate — do not start without feasibility update)"
    Milestone = "0.5 Hardware Investigation"
    Labels = @('status:blocked','maturity:researched','type:research','priority:p2','phase-4','runtime:ftc','risk:hardware','risk:rules','risk:hearing','experimental')
    Body = @'
## Problem
No documented Driver Hub transducer; official DS audio APIs exist but output path is unproven.

## Student learning objective
Students learn hardware validation beats assumptions.

## Scope
Smallest spike: enumerate output, one bounded cue, latency log, gamepad/DS sanity, TRACE log.

## Out of scope
Default-enabling match audio; custom DS APK; Bluetooth; companion apps.

## Acceptance criteria
- [ ] Feasibility doc updated with measured results
- [ ] No unauthorized wireless path
- [ ] Disable restores stock workflow

## Dependencies
Kickoff rules re-verification; mentor approval; physical REV-31-1596

## Readiness requirements
Phase -1 Conditional GO still valid

## Architecture impact
May add gated ftcOutputAdapter flag only after success

## Implementation plan
Follow docs/hardware-validation.md

## Validation plan
Repeatable test script + mentor hearing review

## Documentation required
docs/hardware-validation.md, docs/competition-readiness.md

## Hardware validation required
- [x] Required — entire slice

## Rules implications
Must not proceed if Q&A/rules block path

## Human-safety implications
Conservative SPL; one-ear communication review

## Rollback or disable strategy
Feature flag off; remove adapter from team config

## Parent epic or roadmap link
Epic: ECHO Phase -1 Feasibility and Research
'@
  },
  @{
    Title = "Phase 2 ViDAR adapter and TRACE replay contract"
    Milestone = "0.4 Integration and Replay"
    Labels = @('status:backlog','maturity:simulated','type:integration','priority:p1','phase-2','runtime:desktop')
    Body = @'
## Problem
Need versioned ViDAR observation → explicit target contract and replay without ViDAR JAR dependency.

## Student learning objective
Trace a selected ViDAR target through eligibility, selection, sonification, and decision record.

## Scope
Expand VidarObservation, synthetic replay tests, TRACE-shaped export.

## Out of scope
Live robot ViDAR on Control Hub in this slice.

## Acceptance criteria
- [ ] Contract version documented
- [ ] Replay tests deterministic
- [ ] No silent multi-target selection

## Dependencies
Phase 0 core

## Readiness requirements
None

## Architecture impact
Optional adapter package

## Implementation plan
DTO + tests + docs/vidar-integration.md updates

## Validation plan
Unit replay tests

## Documentation required
docs/vidar-integration.md, docs/trace-integration.md

## Hardware validation required
- [ ] None

## Rules implications
None for desktop replay

## Human-safety implications
Stale/confidence gating enforced

## Rollback or disable strategy
vidarAdapter flag default false

## Parent epic or roadmap link
Epic: ECHO Phase 0 Deterministic Core
'@
  }
)

foreach ($i in $issues) {
  Ensure-Issue -Title $i.Title -Body $i.Body -Labels $i.Labels -Milestone $i.Milestone
}

Write-Host "ISSUES DONE"
gh issue list --repo The-Allsparks/ECHO --limit 20
