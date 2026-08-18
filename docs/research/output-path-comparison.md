# Output-path comparison

**Access date:** 2026-08-17  
**Decision context:** BIOBUZZ Competition Manual **V0**; FTC SDK public line **11.2.x**; REV-31-1596 specs without a listed speaker or jack.

Legend: **Legal** = supported by current V0 text. **Illegal** = contradicted by V0. **Unspecified** = not enough text to approve. **Unproven** = needs hardware.

| # | Path | Technical | Match legality | Team code can reach it? | Hardware | Perms | Modify official SW | Comms | Wireless | Latency | Reliability | Discovery | Reconnect | vs gamepad | vs DS radio | Hearing | Match viable? | Practice/training | Q&A | Hardware test |
| - | ---- | --------- | -------------- | ----------------------- | -------- | ----- | ------------------ | ----- | -------- | ------- | ----------- | --------- | --------- | ---------- | ----------- | ------- | ------------- | ----------------- | --- | ------------- |
| 1 | Gamepad headset jack as Android device | **Unproven** (jack ≠ UAC) | **Unspecified** + R202.B | Only if OS routes DS audio | Legal wired pad + headset | USB host already | No | USB | None if wired | Unk | Unk; composite USB risk | Unk | Unk | **High risk** | USB stall risk | Two-ear isolation | **No** | Maybe bench | Yes | Yes |
| 2 | USB audio on Driver Hub USB-A | Possible if OEM image supports UAC1 | **Unspecified**; USB-A documented as HID; not a gamepad | Indirect via system routing | UAC DAC + headset; uses a port | USB host | No | USB isochronous | None | Unk | Unk | Unk | Unk | Port/power risk | Unk | Headset HF issues | **No** until Q&A + test | Bench only | Yes | Yes |
| 3 | Driver Hub built-in speaker | **Unproven**; not in REV spec | R202.B hostile to open-air | Official SoundPlayer/TTS only | None extra if exists | None | No | RC–DS official | None extra | Unk | Unk | N/A | N/A | Low | Low | Distraction, startle | **No** as designed guidance | Debug only | Yes | Yes |
| 4 | Wired headset on Hub analog jack | **No documented jack** | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | **No** | N/A | Confirm none | Confirm none |
| 5 | Other legal wired accessory | Same as 2 if UAC; else unknown device class | Must be explicitly legal | Unk | Unk | Unk | No | Unk | Cannot be wireless | Unk | Unk | Unk | Unk | Unk | Unk | Unk | **No** | Unk | Yes | Yes |
| 6 | Custom code in/with official DS app | Easy technically | **Illegal** (R706) | Would require modified DS | — | — | **Yes — forbidden** | — | — | — | — | — | — | — | — | — | **No** | **No** | No (clear rule) | Do not test via patched APK |
| 7 | Separate companion app (phone/laptop) | Easy | **Illegal in MATCH** (R704.C/D, R904, R901 extra radios) | Yes off-field | Second device | App perms | No | Extra network | Usually yes | Low on LAN | High off-field | OS | OS | N/A | **Forbidden in MATCH** | Training HF | **No** | **Yes off-field** | No | Training only |
| 8 | Desktop / Android training with sim or TRACE replay | **Yes** (`javax.sound` / desktop) | N/A (not a MATCH) | Yes | PC headphones | None | No | File/sim | None required | Measurable on PC | High | OS | OS | N/A | N/A | Controlled lab | N/A | **Yes — selected** | No | Desktop only |
| 9 | Robot-side speaker | Control Hub **no speaker** (wiki); extra speaker on robot | R202.B; field audio | TeamCode could play locally if a speaker existed | Extra robot audio device not in R707 USB list | — | No | None to driver stereo | None | — | — | — | — | — | — | Distracts field | **No** | Debug beeps maybe | No | Not for ECHO |
| 10 | Official `SoundPlayer` / `Telemetry.speak` on DS | Software **exists** (wiki/javadoc); transducer **unproven** | Software uses official channel; **output device** still unspecified; speech/cues still R202.B | TeamCode can *call* APIs; cannot own mixer | Hub output unknown | None extra | No | Official RC–DS | Official only | Wiki: first plays delayed | Short clips only | Default Android output | Unk | Should be none | Uses allowed telemetry/commands; must not become extra streaming (R704.D) | Speech overload; open-air if speaker | **Conditional investigation only** | Yes as API study | Headset/speaker Q&A | Yes |
| 11 | FTC Dashboard as audio host | Dashboard is a web telemetry UI | **Illegal in MATCH** (R704.D names FTC Dashboard) | Practice pits maybe | Laptop | Browser | No | Extra Wi-Fi stream | Yes | — | — | — | — | — | **Forbidden MATCH** | N/A | **No** | Pits only, not ECHO audio | No | No |
| 12 | Gamepad rumble | SDK rumble APIs (FTC Docs) | Docs allow signaling | TeamCode | Legal pad with rumble | None | No | USB HID | None | Low | Pad-dependent | HID | Unplug | Is the gamepad | Low | Not hearing | Not ECHO audio | Haptic experiments separate | No | If ever in scope |

## Selected paths

| Use | Path | Status |
| --- | ---- | ------ |
| **Now** | Path 8 — desktop training / simulation / fake renderer | **GO** for off-field |
| **Later investigation (gated)** | Path 10 — official DS playback APIs, **only after** transducer + legality + human-factors gates | **Not approved** |
| **Not selected** | 1–7, 9, 11 as match audio architectures | See table |

Do not describe Path 8 as a competition capability.
