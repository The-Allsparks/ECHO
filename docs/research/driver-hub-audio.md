# REV Driver Hub audio research

**Access date:** 2026-08-17  
**Hardware:** REV Driver Hub **REV-31-1596** (the only officially supported DS device in BIOBUZZ Manual V0 R901)

## 1. Primary vendor sources

| Source | URL | Speaker? | Headphone jack? | USB-A described as |
| ------ | --- | -------- | --------------- | ------------------ |
| REV Driver Hub specifications (docs.revrobotics.com markdown) | https://docs.revrobotics.com/duo-control/control-system-overview/driver-hub-specifications.md | **Not listed** | **Not listed** | “Connect USB controllers and other **HID** devices” |
| REV product page | https://www.revrobotics.com/rev-31-1596/ | **Not listed** | **Not listed** | “3 USB-A ports for connecting USB Controllers” |
| REV Getting Started | https://docs.revrobotics.com/duo-control/menu/driver-hub-gs.md | Not listed | Not listed | Gamepads in USB 2.0 ports |

**Verified mechanical/electrical interfaces in the specification table:** Power button, USB-C (PC + charging), three USB-A, Ethernet RJ45. Display 5 in. Processor RKPX30 Quad-core ARM A35, 1 GB RAM, 8 GB eMMC, Wi-Fi + Bluetooth 4.1 radio (Bluetooth must be **disabled** in MATCH per R711).

**Verified absence:** No speaker, no 3.5 mm jack, no HDMI audio, no “audio” row in the published interface table.

## 2. Conflict with FTC wiki

FTC wiki “Driver Station Speech Telemetry” (accessed 2026-08-17; page edited 26 Dec 2022) says to turn up volume **on the Driver Hub or DS phone**, implying the Hub can play TTS.

**This is a documentation conflict.**

Possible resolutions (**Unknown** until hardware test):

1. The Hub has an undocumented internal transducer.
2. The wiki language was written for phones and applied to “Driver Hub” without a speaker check.
3. Volume settings exist in Android even if the analog path is unterminated (software volume, no sound).

ECHO will **not** claim a built-in speaker. Phase 4 must include: play a bounded test cue, measure SPL at 0.5 m, photograph any jack, and `dumpsys media.audio_flinger` / equivalent if accessible without modifying the DS app.

## 3. Candidate paths on this device

### 3.1 Built-in speaker (Path 3)

| Topic | Finding |
| ----- | ------- |
| Technical | **Unknown.** Not in vendor spec. |
| Legality | If it exists, R202.B still limits loud/distracting/mimic sounds. Open-air speaker at a MATCH is likely a distraction to alliance partners and field staff. |
| Team code | Only via official DS playback (SoundPlayer / TTS), not a custom DS renderer. |
| Hardware | None extra **if** speaker exists |
| Permissions | None |
| Modify official software | No |
| Comms | Existing RC–DS |
| Wireless | None extra |
| Latency / reliability / discovery | Unknown |
| Interferes with gamepad / DS | Unlikely if internal |
| Hearing | Open-air: startle, venue noise, field-staff awareness |
| Competition | **Not viable** without (a) proving a speaker and (b) proving R202.B compliance. Open-air is a poor human-factors choice even if legal. |
| Practice | Same |
| Q&A | “Does REV-31-1596 include a user-accessible audio output?” |
| Hardware | **Required** |

### 3.2 Wired headset on a Driver Hub analog port (Path 4)

**Verified:** no analog headset port in REV specs.  
**Status:** **No documented port.** Do not invent one.

### 3.3 USB audio accessory on USB-A (Path 2 / Path 5)

| Topic | Finding |
| ----- | ------- |
| Technical | Possible **only if** Hub USB host + kernel USB audio + UAC1 device. REV documents USB-A as **HID**. |
| Legality | **Unspecified.** Extra non-gamepad USB function is not approved by silence. R901 mentions USB cables/hubs **for connecting gamepads**. A USB DAC is not a gamepad. |
| Team code | Indirect (system routing + official DS audio APIs) |
| Hardware | UAC1 adapter + wired headset; consumes a USB-A port (Hub has three; two gamepads + DAC is a port-count issue) |
| USB-C | Documented for charging/PC, not as an accessory audio sink |
| Interferes with gamepad | Real risk (USB host controller, power, enumeration) |
| R202.B | Headset quieter than a speaker, still unspecified |
| Q&A | Required before treating as legal |
| Hardware | Required |

### 3.4 Official SoundPlayer / Telemetry.speak on Hub (software path, hardware still required)

This is the **least illegal-looking software path** because it uses official RC→DS commands and does not modify the DS APK.

It is **not** a complete architecture: without a transducer, the command is a no-op.

SoundPool is for **short clips**, not a continuous spatial stream. Mapping bearing to pan would require either:

- pre-baked left/right/center clips (**proposal**, limited resolution), or
- an SDK capability that has **not** been verified in current TeamCode APIs for independent L/R gain.

### 3.5 Ethernet port

**Verified** present (10/100, passive PoE). Using it for audio streaming would be a **new communications path** and is not an ECHO match design. Training use on a bench is still not MATCH legal as a second control path.

## 4. Control Hub (robot-side) audio (Path 9)

**Verified:** FTC wiki — REV Control Hub has **no speakers**.  
Robot-side sound would be in-field, likely R202.B (distraction / mimic match sounds), and would not give the driver private directional stereo. **Rejected** as a driver-guidance path.

## 5. Conclusion

Driver Hub audio is **not documented by REV**. FTC software docs assume volume exists. ECHO’s hardware stance: **treat the Hub as having no proven analog output** until Phase 4 measurements.
