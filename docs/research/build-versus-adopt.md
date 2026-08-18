# Build versus adopt

**Access date:** 2026-08-17  
**Question:** Should ECHO invent sonification, or adopt an existing spatial-audio / accessibility stack?

## 1. Options considered

| Technology | License / platform | Fit for FTC students + Driver Hub |
| ---------- | ------------------ | --------------------------------- |
| Android `SoundPool` / FTC `SoundPlayer` | FTC SDK | Official short-clip path; not a spatial engine; RC/DS split |
| Android `AudioTrack` | AOSP / Android SDK | Good PCM pan/pulse **on the device that plays audio**; TeamCode is not that device |
| `javax.sound.sampled` | JDK | **Adopt for desktop training** |
| OpenAL / OpenAL Soft | LGPL/BSD variants | Extra native binary; HRTF optional; poor FTC onramp |
| Google Resonance / other HRTF | Various | Over-capability; head-tracked 3D is the wrong problem |
| Equal-power stereo pan | Public domain math | **Adopt the algorithm, implement ourselves** |
| Interaural time difference (ITD) | Psychoacoustics | Small benefit on cheap headsets; harder to test; skip initially |
| Pulse-rate sonification | Auditory display literature | **Adopt the idea**; implement deterministically |
| Android accessibility / TalkBack | AOSP | Speech-first; fights DS UI; not a match architecture |
| Game audio middleware (FMOD/Wwise) | Commercial | Unnecessary |
| FRC driver-aid audio | Team-specific | Different control system (roboRIO/Driver Station laptop). Do not copy legality. |
| FTC Dashboard | BSD-ish community | **Illegal extra stream in MATCH** (R704.D) |

## 2. What to reuse

- **Equal-power panning** (`L = cos((p+1)·π/4)`, `R = sin((p+1)·π/4)` for pan `p ∈ [-1,1]`). Simple, testable, teachable.
- **Pulse timing** as the proximity/alignment dimension (auditory-display practice).
- **Priority / inhibit / rate-limit** patterns from aviation alerting — as *design rules*, not as a library.
- **`javax.sound.sampled`** for off-field playback.
- **JUnit 5** and Gradle 8.7 / Java 11, matching Allsparks AMPER.

## 3. What to adapt later (gated)

- FTC `SoundPlayer` clip bank (left / center / right / warn families) **if** Path 10 hardware exists.
- TRACE event schema — adapter, not a logging competitor.
- Optional ViDAR/MIMIC/AMPER/BEACON/HELM **versioned DTOs**.

## 4. What not to adopt

- HRTF / binaural rendering as the core (head pose unknown; gym noise; unteachable).
- OpenAL as a required native dependency.
- Spoken `Telemetry.speak` as the default vocabulary (slow, language-heavy, masks other speech).
- Continuous musical drones.
- Any library that commands motors.
- Patching the official DS app.

## 5. Selected core approach

**Deterministic cue selection + two-channel intensity pan + pulse interval + optional bounded pitch (default off) + distinct timbre families**, rendered by a replaceable `CueRenderer`.

Reasons:

1. Students can plot pan and pulse and write tests without listening.
2. No native spatializer on the Driver Hub is documented.
3. Stereo pan is **not** 3D localization; documentation must say so.
4. Licensing remains MIT-only in the core.

## 6. Licensing implications

Core is MIT. Do not add LGPL native libs to the default artifact. If a future Android renderer uses AndroidX, record that in an ADR before adding the dependency.
