# Troubleshooting

| Symptom | Likely cause | Action |
| ------- | ------------ | ------ |
| No sound on desktop | Audio flag off (default) | Expected. Pass `--audio` only for training. |
| No sound but UI shows a cue | OS volume / device | Check OS output; ECHO gain is conservative |
| Always silence | Driver disabled, stale, low confidence, no target | Read `silenceReason` in the explanation panel |
| Rapid cue chatter | Hysteresis too small | Leave defaults; do not “tune loud” |
| Desktop pan feels wrong | Not wearing stereo headphones | Use headphones; pan is intensity L/R |
| Want this on a Driver Hub | Not validated | Stop. See feasibility decision |
| Robot moved when ECHO ran | **Must not happen** | File a security/safety report; ECHO has no motor APIs |

Logs: `EchoDecisionRecord.toExplanation()`.
