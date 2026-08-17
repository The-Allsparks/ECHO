# FTC rules and platform research

**Access date:** 2026-08-17  
**Season under review:** 2026–2027 FIRST Tech Challenge, BIOBUZZ™ presented by RTX  
**Evidence class legend:** **Verified** = quoted or paraphrased from a primary source accessed on the access date. **Inference** = engineering conclusion from verified facts. **Unknown** = not established by a primary source. **Proposal** = ECHO design, not a rule.

This document does **not** approve ECHO for match use.

## 1. Authoritative documents located

| Document | Version / date | URL | Accessed |
| -------- | -------------- | --- | -------- |
| BIOBUZZ Competition Manual (HTML) | Pre-Season **V0**, updated **31 Jul 2026** | https://ftc-resources.firstinspires.org/ftc/game/manual | 2026-08-17 |
| Current Game and Season Materials | lists Manual V0 (31 Jul 2026) and Team Update 00 | https://ftc-resources.firstinspires.org/ftc/game | 2026-08-17 |
| BIOBUZZ Competition Manual preview announcement | 31 Jul 2026 | https://community.firstinspires.org/biobuzz-cm-preview-release | 2026-08-17 |
| FTC SDK / FtcRobotController | public repo; README describes **v11.2.1** (20260724) as current at access | https://github.com/FIRST-Tech-Challenge/FtcRobotController | 2026-08-17 |
| FTC Docs — Driver Station app update | current FTC Docs site | https://ftc-docs.firstinspires.org/en/latest/ftc_sdk/updating/ds_app/Updating-the-DS-App.html | 2026-08-17 |
| FTC Docs — Driver Station components | current FTC Docs site | https://ftc-docs.firstinspires.org/en/latest/control_hard_compon/ds_components/components/components.html | 2026-08-17 |
| FTC Control System Guide (PDF booklet) | FTC Docs CDN | https://ftc-docs-cdn.ftclive.org/booklets/en/control_system.pdf | 2026-08-17 |
| FTC Robot Controller wiki — Playing Audio Files | last wiki edit 26 Dec 2022 | https://github.com/FIRST-Tech-Challenge/FtcRobotController/wiki/Playing-Audio-Files | 2026-08-17 |
| FTC Robot Controller wiki — Driver Station Speech Telemetry | last wiki edit 26 Dec 2022 | https://github.com/FIRST-Tech-Challenge/FtcRobotController/wiki/Driver-Station-Speech-Telemetry | 2026-08-17 |
| Team Q&A home | official | https://ftc-qa.firstinspires.org/ | 2026-08-17 |
| Team Q&A registration instructions PDF | marked **V25-26.1** (prior-season revision on the PDF) | https://info.firstinspires.org/hubfs/web/program/ftc/team-qa-registration-instructions.pdf | 2026-08-17 |

### 1.1 Documents that could not be retrieved as primary text on the access date

| Attempt | Result | Handling |
| ------- | ------ | -------- |
| https://ftc-resources.firstinspires.org/ftc/game/tu-00 | HTTP 404 | **Unknown.** The Game and Season Materials page *lists* Team Update 00. The HTML fetch failed. ECHO does **not** treat search-engine snippets of TU-00 as verified rule text. |
| Complete Kickoff Competition Manual | Not released | **Verified from V0:** “The complete BIOBUZZ Competition Manual will be released on Saturday, September 12th” (community blog, 31 Jul 2026). Game-specific and some tournament sections in V0 are placeholders. |

**Do not silently substitute 2025–2026 DECODE rules for BIOBUZZ.** Where this file cites a previous-season archive, it is labeled as **historical**, not current.

## 2. Platform snapshot (current public SDK)

**Verified** from the FtcRobotController README as of 2026-08-17:

- The public repository still describes itself as the SDK for the **DECODE (2025–2026)** season in the GitHub page title/description at access time.
- Release notes include **Version 11.2.1 (20260724-093406)** and **Version 11.2 (20260707-102819)**, called an offseason release for 2025/2026 in the v11.2 GitHub release notes.
- Driver Station APK is distributed as `FtcDriverStation-release.apk` from GitHub Releases, not as team-modifiable match software.

**Inference:** On 2026-08-17, before BIOBUZZ Kickoff, the currently published SDK is the late DECODE / offseason line. A BIOBUZZ-specific SDK may replace it at or after Kickoff. ECHO must re-verify SDK and DS app versions after 12 Sep 2026.

**Verified** from FTC Docs “Updating the Driver Station App”:

- The Driver Station App is provided with the FTC SDK.
- It is the major interface for robot configuration, gamepad support, self-inspect, Team code selection and execution.
- It runs on the REV Driver Hub or an approved Android smartphone.

**Verified** from FTC Docs Driver Station components:

- The heart of the Driver Station is the Android device running the Driver Station app.
- REV Driver Hub (REV-31-1596) or an Android smartphone listed/allowed by the Competition Manual.
- Gamepads must be **wired**. “Special features of some gamepads (Rumble, Lighting) may be programmed and used by teams for notifications and signaling to the drivers of the robot.”

## 3. Rules that constrain ECHO (BIOBUZZ Manual V0)

Quotes below are from the V0 HTML Competition Manual accessed 2026-08-17.

### 3.1 Safety / fair play — audio on robot or operator console

**R202** *Design ROBOTS and OPERATOR CONSOLES for safety and fair play.* Parts shall not be unsafe or interfere with operations of other ROBOTS or FIELD STAFF. Examples include:

- **R202.B:** “audio devices that generate sound at a level sufficient to be a distraction or mimic match sounds”

**Verified:** This rule applies to both ROBOT and OPERATOR CONSOLE.  
**Unknown / Q&A needed:** Whether a **private wired headset** worn by a DRIVER, at a volume that is not a distraction to others and does not mimic match sounds, is permitted. V0 does not define “distraction” with a dB limit and does not mention headsets. **Do not stretch R202.B into approval.**

**R903** *OPERATOR CONSOLE physical requirements.* Volume limit **3 ft wide × 1 ft 6 in deep × 2 ft tall**, “excluding any items that are held or worn by the DRIVERS during the MATCH.”

**Verified:** Worn/held items are excluded from the size box.  
**Inference:** A headset *worn by a driver* is not automatically illegal for size. Size legality ≠ audio-device legality.

### 3.2 Wireless and extra software

**E301** *No wireless communication.* Teams may not set up their own Wi-Fi, Bluetooth, or other 2.4/5 GHz communications systems in the venue. Note that Bluetooth uses 2.4 GHz.

**R704** *Use networks and bandwidth as directed.*

- **R704.A:** No other form of wireless communication except official tools, to/from/within the ROBOT.
- **R704.B:** All communication signals must originate from only the ROBOT CONTROLLER or DRIVER STATION using the ROBOT CONTROLLER Wi-Fi network.
- **R704.C:** Programming laptops and other devices (other than the DRIVER STATION) must be disconnected from RC Wi-Fi during MATCH play.
- **R704.D:** Software with access to RC Wi-Fi must limit streamed data. Software may only stream robot control data, debugging data, and telemetry **using the FTC Driver Station Application**. “Additional logging/streaming services, such as those hosted by third party plugins and tools such as FTC Dashboard, FTControl Panels, and others are prohibited. No continuous video stream is allowed.”

**R711.C:** On RC and DS Android devices, Wi-Fi must be enabled and **Bluetooth must be disabled**.

**R904** *ROBOT application wireless communication only.* Other than the RC app ↔ DS app connection, “no other form of wireless communications shall be used to communicate to, from, or within the OPERATOR CONSOLE during a MATCH.” Examples: active wireless network cards and Bluetooth devices.

**Verified implications for ECHO:**

- Bluetooth headphones, Bluetooth gamepad audio, and any second radio are **not** a legal match path.
- A companion laptop/phone talking to the robot or DS over Wi-Fi during a MATCH is **not** a legal match path (R704.C, R704.D, R904).
- FTC Dashboard-style extra streaming is **explicitly prohibited** during MATCH play by R704.D.

### 3.3 One Driver Station; do not modify official software

**R901** *Use only a specified DRIVER STATION device.* The OPERATOR CONSOLE may have only one approved Android-based DRIVER STATION device connected and powered on. Must include:

- **A.** REV Driver Hub (REV-31-1596), **or**
- **B.** Any Android Device with USB cables/hubs for connecting gamepads.

V0 states the REV Driver Hub is the **only officially supported** DRIVER STATION device; other Android devices are the team’s responsibility.

**R706** *Only specified modifications to core control system devices are permitted.* The DRIVER STATION **device and software**, Android-based ROBOT CONTROLLER device, and listed power hardware “shall not be tampered with, modified, or adjusted in any way,” with a closed list of exceptions (cables, fasteners, labels, manufacturer firmware, equivalent repairs, etc.).

**Verified:** Replacing, overlaying, or patching the official Driver Station application is **not** in the exception list.  
**Inference:** A custom DS APK, Xposed overlay, accessibility service that drives the official DS, or automated UI control of the official DS is not a defensible match architecture.

**R902:** DS touch screen must remain accessible, visible, and usable without extra aides (e.g. a mouse).

### 3.4 USB on the robot vs operator console

**R707** *USB is for vision* applies to the **ROBOT control system** USB (webcams, hub/switch, Expansion Hub). It does **not** by its text regulate Driver Hub USB ports.

**Inference:** Robot-side USB audio hardware is not a listed R707 device and is not a match path. Operator-console USB is a separate question (gamepads vs other USB functions). V0’s operator-console section in the accessed HTML **does not include a numbered legal-gamepad list** (that list existed in prior-season manuals as a distinct R9xx rule). **Unknown:** BIOBUZZ Kickoff text may restore an explicit gamepad enumeration. Until then, FTC Docs still say “The Competition Manual defines the gamepads that are allowed.”

### 3.5 Q&A calendar

**Verified** from V0 §1.7.4: The Q&A opens **28 September 2026, 12:00 p.m. ET**. Access is through Lead Coach 1 or 2’s FIRST dashboard account. Q&A responses do not supersede manual text if inconsistent; the manual wins.

Draft questions for later authorization are in [feasibility-decision.md](../feasibility-decision.md). **Not submitted.**

## 4. Official audio-related software (SDK / wiki)

These are **platform** facts, not automatic match-architecture approval.

### 4.1 SoundPlayer / Blocks SoundPool

**Verified** (FTC wiki “Playing Audio Files”, accessed 2026-08-17; wiki last edited 26 Dec 2022):

- FTC Blocks can play short `.wav` / `.mp3` sounds on RC phones **and DS devices**.
- “The sound plays back on both RC phone and DS device.”
- “Note that the REV Control Hub has no speakers.”
- FTC Blocks uses Android `SoundPool` (short clips). Longer files are unreliable; `MediaPlayer` is “not available in FTC Blocks.”
- Tight-loop replay can produce a “staccato of continuous starts.”

`CommandList.CmdPlaySound` exists in historical FTC javadoc as a serialized play-sound command (SkyStone-era javadoc still published). **Inference:** sound playback on the DS is historically implemented as an official RC→DS command on the **existing** legal radio path—not a new wireless protocol.

**Unknown (must re-verify on current SDK source after Kickoff):** stereo left/right gain, latency, and whether Driver Hub actually emits the sound without an analog/USB transducer.

### 4.2 Telemetry.speak

**Verified** (FTC wiki “Driver Station Speech Telemetry”; Telemetry javadoc: `speak` “Directs the Driver Station device to speak the given text using TextToSpeech”):

- Spoken telemetry is intended **on the DS phone or Driver Hub**.
- Control Hub has no built-in speaker (wiki).
- Wiki instructs: “Turn up the audio volume on the Driver Hub or DS phone (not the ringtone volume).”

**Inference:** FIRST documentation *assumes* the Driver Hub can play TTS audio at some system volume. REV’s published Driver Hub specification **does not list a speaker or headphone jack** (see [driver-hub-audio.md](driver-hub-audio.md)). This is a **documentation conflict**, not a license to assume a speaker exists.

ECHO’s initial cue vocabulary **avoids spoken narration** unless a later study shows a compelling need. `Telemetry.speak` is therefore **not** the preferred long-term sonification API even if legal.

### 4.3 Gamepad rumble / lighting

**Verified** (FTC Docs Driver Station components): rumble and lighting on some legal gamepads may be programmed for driver notifications.

**Verified distinction:** rumble is **haptic**, not auditory spatialization. It is out of ECHO’s core scope except as a possible future multimodal adapter, and only if still legal.

## 5. What team code can and cannot host

| Location | Team-authored code during a MATCH? | Audio? |
| -------- | ---------------------------------- | ------ |
| Robot Controller TeamCode / OpMode | **Yes** (official model) | Can *request* official SoundPlayer / Telemetry.speak; Control Hub has **no** speaker |
| Official Driver Station app | **No** custom team plugin in the published model | Official app may play sounds/TTS if the SDK implements it |
| Second Android app on the Driver Hub | **Unknown / likely hostile to inspection** if it communicates in MATCH; R901 is about one DS *device*, not explicitly one *app*. Extra apps that use Bluetooth or other radios violate R904/R711. Extra apps that attach to RC Wi-Fi violate R704. | Not a defensible match path without Q&A |
| Laptop companion | **No** during MATCH (R704.C) | Training only |
| Modified DS APK | **No** (R706) | Not a match path |

## 6. Stop conditions triggered by this research

None of the stop conditions require abandoning **off-field** work.

Match-path stop conditions that **are** currently active as **gates**, not as “never research”:

- Full BIOBUZZ manual is V0; Kickoff manual is not out.
- Legal gamepad list is not in the accessed V0 operator-console section.
- Driver Hub transducer is not in REV specs; wiki implies audio volume on Driver Hub.
- Headset legality under R202.B is unspecified.
- USB audio accessory on Driver Hub USB-A is unspecified (REV text: USB-A for “USB controllers and other HID devices”).

## 7. Re-verification checklist (after 12 Sep 2026)

1. Download Kickoff Competition Manual; recapture R202, R704, R706, R711, R9xx.
2. Read Team Updates from TU-00 onward from the HTML/PDF, not snippets.
3. Confirm current FtcRobotController season tag and SoundPlayer / Telemetry.speak source.
4. Confirm inspection checklist language for operator-console electronics.
5. Search Q&A for headset, USB DAC, SoundPlayer, and DS audio after 28 Sep 2026.
