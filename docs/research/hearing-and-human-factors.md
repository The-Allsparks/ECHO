# Hearing and human-factors research

**Access date:** 2026-08-17

ECHO treats driver hearing and attention as safety-critical resources. This file cites **health and human-factors** sources. It does not claim ECHO is beneficial.

## 1. Authoritative exposure guidance

| Source | Statement used | URL | Accessed |
| ------ | -------------- | --- | -------- |
| NIOSH / CDC, *Understand Noise Exposure* | REL **85 dBA** as 8-hour TWA; 3 dB exchange rate; noise ≥85 dBA considered hazardous; hearing loss is preventable; noise can reduce situational awareness and contribute to injuries | https://www.cdc.gov/niosh/noise/prevent/understand.html | 2026-08-17 |
| NIOSH Publication 98-126 | Confirms 85 dBA 8-hr TWA REL | https://www.cdc.gov/niosh/docs/98-126/ | 2026-08-17 |
| WHO–ITU *Safe listening devices and systems* | Adult mode **80 dB for 40 h/week**; child mode **75 dB for 40 h/week** on personal audio devices | https://www.who.int/publications-detail-redirect/9789241515276 | 2026-08-17 |
| BIOBUZZ Manual V0 **R202.B** | Operator-console audio must not be a distraction or mimic match sounds | Competition Manual HTML | 2026-08-17 |

**Inference for ECHO (not a medical device claim):** default gain must be conservative. Raising volume to beat a gym is the wrong solution. NIOSH’s situational-awareness warning applies directly to covering referee calls, coaches, and robot mechanical sounds.

OSHA PELs are workplace regulation, not an FTC rule. ECHO cites NIOSH/WHO as **health guidance**, not as FIRST law.

## 2. Competition environment (facts vs assumptions)

| Item | Class | Note |
| ---- | ----- | ---- |
| FTC venues are often loud | **Inference** from common event practice | Not quantified in V0. Phase 6 must measure venue dBA if ECHO is ever practiced at an event. |
| Match sounds exist and must not be mimicked | **Verified** R202.B | Confirmation chimes must not copy FIRST start/end tones. |
| Refs and alliance partners use voice | **Inference** | Two-ear isolation is a communication risk even if a headset were legal. |
| One-ear vs two-ear legality | **Unknown** | Do not assume either is legal. Human-factors: one ear preserves more ambient; stereo pan **requires** two channels to mean “left/right.” A one-ear profile would need a different mapping (pulse only) — **proposal**, not implemented as competition. |

## 3. Sonification and alerting practice (adopt ideas, not products)

| Domain | Practice ECHO should learn | Practice ECHO should not copy blindly |
| ------ | -------------------------- | ------------------------------------- |
| Aviation crew alerting | Distinct warning vs advisory; inhibit nuisance alerts; avoid continuous tones | Multi-crew aircraft procedures |
| Automotive | Rate-limited chimes; do not mask sirens | Cabin infotainment spatial mix |
| Game audio | Priority buses; one focus cue | Dense 3D mixes; music beds |
| Accessibility navigation | Short, learnable motifs; user-controlled mute | Spoken turn-by-turn in a 2-minute teleop unless tested |

**Proposal:** one guidance cue + rare warnings; no continuous drone; no speech in the initial vocabulary.

## 4. Risks ECHO must design against

| Risk | Mitigation in ECHO design |
| ---- | ------------------------- |
| Over-loud listening | Conservative default gain, configurable cap, no “compete with the gym” preset |
| Startle | Smooth envelopes; no max-gain onsets; warnings rate-limited |
| Habituation | Pulse, not a steady tone; silence when idle |
| Cue confusion | Timbre family owns category; pan owns bearing; pulse owns proximity/alignment — documented in cue vocabulary |
| Fatigue | Silence policy; cooldowns |
| Loss of referee/coach/robot sound | Prefer not covering both ears until legality **and** communication tests pass; immediate mute |
| Individual hearing differences | Training profiles; no single “correct” pan curve claimed as universal |
| Claiming benefit | Forbidden until Phase 6 |

## 5. Accessibility

Audio guidance **can** help some drivers who cannot continuously watch telemetry. It can also **exclude** drivers with hearing loss or auditory processing differences. ECHO must remain fully optional. Visual telemetry stays the default workflow when ECHO is disabled.

Do not describe ECHO as an ADA accommodation product. It is an experimental driver display.

## 6. Experimental honesty

Human-factors literature does not automatically transfer to FTC teleop. Venue noise, 2-minute matches, and alliance shouting are specific. **No performance improvement is claimed.**
