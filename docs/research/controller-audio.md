# Controller (gamepad) audio research

**Access date:** 2026-08-17

## 1. Primary sources

| Source | Role |
| ------ | ---- |
| BIOBUZZ Competition Manual V0 | Operator console, wireless, R202 |
| FTC Docs Driver Station components | Legal gamepad *types as documented by FTC Docs*; wired-only; rumble/lighting note |
| FTC Control System Guide PDF | Same gamepad family list including DualSense, DualShock 4, Etpark, Logitech F310, Xbox 360, Quadstick |
| Sony DualShock 4 / DualSense product materials | 3.5 mm headset jack exists on those consumer products (product fact, not FTC legality) |
| AOSP USB digital audio | Android plays to **USB Audio Class** devices, not to arbitrary HID |
| DS4Windows issue “Audio through DS4 headphone jack” | Community: DS4 Bluetooth advertises audio; USB headset-jack-as-UAC on PC is **not** a well-established class-compliant path |

**Important:** BIOBUZZ Manual V0 operator-console HTML accessed on 2026-08-17 **does not enumerate** legal gamepads. FTC Docs still defer to the Competition Manual. Treat the FTC Docs list as **platform documentation**, and re-verify against the Kickoff manual.

## 2. Candidate: officially supported gamepad headset jack

### Technical feasibility

| Controller | 3.5 mm jack (consumer product) | USB HID when wired | USB Audio Class when wired | Android host behavior |
| ---------- | ----------------------------- | ------------------- | -------------------------- | --------------------- |
| Logitech F310 | Typically **no** | Yes | **No** (not an audio device) | N/A |
| Sony DualShock 4 | **Yes** | Yes | **Unknown** as class-compliant UAC on USB; community tools historically struggled to use the jack over USB on PC | Community Android reports that a plugged DS4 can confuse USB audio routing; **not** a Driver Hub measurement |
| Sony DualSense | **Yes** | Yes | DualSense is known on some USB hosts to expose a headset function; **not verified** on Driver Hub | **Unknown** |
| Etpark wired PS4-style | Jack **varies by clone** | Yes | **Unknown** | **Unknown** |
| Xbox 360 wired | Typically **no** independent USB audio jack used this way | Yes | No | N/A |

**Verified:** Android USB audio applies to **UAC** peripherals (AOSP). HID gamepad and UAC speaker are different USB functions. Presence of a 3.5 mm jack does **not** mean the Driver Hub OS can play ECHO tones through that jack.

**Inference:** Path 1 is **not technically established**. It requires a real-hardware USB descriptor dump on a Driver Hub with the exact legal controller and a headset inserted.

### Competition legality

- Wired gamepad: required by FTC Docs; Bluetooth gamepad illegal.
- Using the jack: **Unknown.** Not mentioned in V0.
- R202.B still applies if the sound is loud enough to distract others or mimics match sounds. A leaky earcup in a quiet venue could still be a problem; a loud jack-powered speaker would be worse.

### Can custom team code reach it?

Only if:

1. Android routes system/DS audio to that USB function, **and**
2. Official DS / SoundPlayer / TTS actually plays to the default output, **and**
3. Gamepad HID continues to work.

TeamCode cannot open the jack as a raw ALSA device on the Driver Hub.

### Hardware / permissions / DS modification / comms

- Hardware: legal wired gamepad + wired headset (if jack works).
- Permissions: none beyond USB host already used for HID.
- Official software modification: none if routing is automatic.
- Communication: existing USB HID; audio would be additional USB isochronous if UAC appears.
- Wireless: none if fully wired.
- Latency: unknown; USB audio + DS mixer.
- Reliability: unknown; composite USB devices are a classic enumeration risk.
- Discovery: unknown.
- Reconnection: unknown; unplugging headset vs unplugging gamepad may differ.
- Gamepad interference: **primary hardware risk**.
- DS communication interference: possible if USB bus/controller stalls.
- Hearing: in-ear monitoring reduces field/ref awareness (see hearing doc).
- Competition viability: **not currently defensible**.
- Practice viability: useful **if** hardware works, still not a match approval.
- Q&A: yes (headset + USB audio function).
- Hardware investigation: **required**.

## 3. Rumble is not ECHO audio

FTC Docs allow rumble/lighting as driver signaling. That is **not** stereo bearing. ECHO must not relabel rumble as auditory guidance. A future multimodal adapter would be a separate, gated design.

## 4. Conclusion for Path 1

**Status:** technically **unproven**, legally **unspecified**, **not selected** as a competition architecture.

Mark any claim that “the DualShock headset jack is an FTC audio device” as **false until measured on a Driver Hub**.
