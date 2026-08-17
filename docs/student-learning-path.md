# Student learning path

Each phase: objective, demo, task, observable result, review questions, failure modes, safety.

## 1. Sound as data (Phase 0)

- **Objective:** Explain that a cue is a chosen message, not “the robot’s ears.”
- **Demo:** Print a decision record for silence vs guidance.
- **Task:** Given a snapshot, predict silence reason.
- **Result:** Written reason matches `CueSelector`.
- **Review:** Why isn’t ECHO a vision system?
- **Failures:** Treating 0 m as “unknown.”
- **Safety:** No hardware.

## 2. Frequency, amplitude, pan, pulse (Phase 1)

- **Objective:** Name which mapping owns bearing vs distance.
- **Demo:** Sliders in the training UI.
- **Task:** Identify left vs right without looking, then check the visual.
- **Result:** ≥8/10 correct in a quiet room (training metric only).
- **Review:** Why equal-power pan?
- **Failures:** Cranking gain.
- **Safety:** Start quiet; headphones optional.

## 3. Frames and bearing (Phase 0–2)

- **Objective:** 0 forward, +right, wrap to (−π, π].
- **Demo:** Bearing +350° vs −10°.
- **Task:** Normalize three angles on paper, then run tests.
- **Result:** Unit test pass.
- **Review:** Who converts ViDAR frames? (Adapter, not core.)
- **Failures:** Using field-centric bearing by accident.
- **Safety:** None.

## 4. Distance and alignment maps

- **Objective:** Pulse interval grows as distance grows (default profile).
- **Demo:** 0.25 m vs 2.5 m.
- **Task:** Compute expected interval from config.
- **Result:** Matches `PulseMapper`.
- **Review:** Why not encode distance *and* class *and* confidence in one sound?
- **Failures:** Inverting near/far.
- **Safety:** None.

## 5. Timestamps and stale data

- **Objective:** Age = now − observation; too old → silence.
- **Demo:** FakeClock jump.
- **Task:** Break a test by using wall clock in a unit test (then fix).
- **Result:** Deterministic fake clock tests.
- **Review:** Receipt vs observation timestamp.
- **Failures:** Trusting last known pose.
- **Safety:** Stale cues could aim the driver at empty space.

## 6. Confidence and unknown

- **Objective:** Unknown ≠ 0 ≠ false.
- **Demo:** `Scalar.unknown()` vs `Scalar.of(0)`.
- **Task:** List five silence reasons.
- **Result:** Eligibility tests green.
- **Review:** What should the driver do on silence? Look up.
- **Failures:** Filling unknown with 0 pan (center).
- **Safety:** Center pan on unknown is a lie.

## 7. Priority and interruption (Phase 3 design)

- **Objective:** Warnings preempt guidance.
- **Demo:** Inject AMPER warning in the selector test.
- **Task:** Explain commitment windows.
- **Result:** Preemption test pass.
- **Review:** Who owns power limits? (AMPER)
- **Failures:** Warning spam.
- **Safety:** Nuisance warnings cause ignore-all.

## 8. Attention and accessibility

- **Objective:** Two-ear audio can hide referees.
- **Demo:** Read hearing-safety doc aloud.
- **Task:** Design a mute gesture that doesn’t stop the robot.
- **Result:** `driverEnabled=false` path.
- **Review:** Is ECHO an accommodation product? (No.)
- **Failures:** “Just turn it up.”
- **Safety:** Hearing + communication.

## 9. Experimental design (Phase 6, gated)

- **Objective:** Benefit needs a protocol, not a vibe.
- **Demo:** Read the metric list in the charter.
- **Task:** Write a 1-page experiment with sample size and confounders — do not run as match advice.
- **Result:** Document only.
- **Review:** Why anecdotes are forbidden.
- **Failures:** n=1 “felt faster.”
- **Safety:** Don’t add match risk to “try it.”

## 10. TRACE analysis

- **Objective:** Replay decisions.
- **Demo:** Same snapshot twice.
- **Task:** Find why a cue was rejected in the record.
- **Result:** Identical hashes/explanations.
- **Review:** TRACE owns storage; ECHO owns the record shape.
- **Failures:** Logging becoming a second planner.
- **Safety:** None.

## 11. Safe deployment and rollback

- **Objective:** Flags default off; disable restores stock DS use.
- **Demo:** Training UI mute.
- **Task:** List rollback steps for a future Hub experiment.
- **Result:** Checklist in hardware-validation.md.
- **Review:** Library ≠ approval.
- **Failures:** Shipping `--audio` defaults.
- **Safety:** Match audio still forbidden.
