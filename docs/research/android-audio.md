# Android audio research

**Access date:** 2026-08-17  
**Evidence class:** **Verified** / **Inference** / **Unknown** / **Proposal**

ECHO’s core library does **not** depend on Android. This file records what would be true *if* a later gated Android renderer were written.

## 1. Primary sources

| Source | What it establishes | Accessed |
| ------ | ------------------- | -------- |
| Android Open Source Project, *USB digital audio* | Host-mode UAC1 subset from Android 5.0 (API 21) | 2026-08-17 |
| USB-IF Audio Device Class (UAC1 / UAC2) | Class-compliant USB audio peripherals | cited via AOSP page |
| Android `AudioTrack` reference | App-level PCM playback API | fetch timed out 2026-08-17; URL recorded |
| FTC wiki Playing Audio Files | Official FTC path uses Android `SoundPool` for Blocks sounds | 2026-08-17 |
| FTC Telemetry javadoc `speak(...)` | Official DS TTS | 2026-08-17 |

URLs:

- https://source.android.com/docs/core/audio/usb
- https://developer.android.com/reference/android/media/AudioTrack
- https://developer.android.com/ndk/guides/audio/opensl/opensl-for-android (historical OpenSL ES; AAudio is the modern NDK path)

## 2. Verified Android USB audio (AOSP)

Accessed 2026-08-17 from https://source.android.com/docs/core/audio/usb :

Android **5.0+ host mode** supports a **subset of USB Audio Class 1 (UAC1)**:

- Android device must act as **USB host**
- PCM (interface type I)
- Bit depth 16, 24, or 32 (24-bit left-justified in 32-bit)
- Sample rates 48, 44.1, 32, 24, 22.05, 16, 12, 11.025, or 8 kHz
- 1 (mono) or 2 (stereo) channels

AOSP also states:

- Compatible USB digital audio peripherals are **automatically routed** by audio policy.
- There are **no APIs specific to USB digital audio**.
- Automatic routing can interfere with USB-aware apps; developer option exists to disable USB audio routing.
- USB audio uses **isochronous** transfers (bandwidth guaranteed, no retransmission).
- Isochronous USB adds **latency** relative to an on-board DAC.

**Inference:** If a Driver Hub kernel includes `CONFIG_SND_USB_AUDIO` and USB audio policy, a UAC1 headset/DAC on USB-A *might* become the system output. That is a **hardware + OEM image** question, not something team Java can assume.

**Unknown:** Whether the REV Driver Hub Android image enables USB audio host, `android.hardware.usb.host`, and `audio.usb.default`. REV published specs do not claim it.

## 3. App-level playback APIs (not FTC-specific)

| API | Typical use | FTC relevance |
| --- | ----------- | ------------- |
| `SoundPool` | Short clips | **Verified** as the Blocks SoundPlayer backend (wiki) |
| `MediaPlayer` | Longer files | Wiki: not in Blocks; Java TeamCode on RC could use it **on the RC process**, which does not help a speakerless Control Hub |
| `AudioTrack` | Streaming PCM, stereo buffers | Suitable for pan/pulse **if** team code runs on the device that owns the output |
| `AudioManager` | Device routing, volume | Needed to discover wired vs USB vs speaker |
| OpenSL ES / AAudio / Oboe | Low-latency native | Overkill for ECHO; not student-teachable as a first path |
| OpenAL / HRTF | Spatialization | See [build-versus-adopt.md](build-versus-adopt.md) — **not adopted** |

**Verified constraint from FTC architecture:** TeamCode runs on the **Robot Controller**. The official DS app runs on the Driver Hub. TeamCode cannot assume it is an Android audio app on the Driver Hub.

**Inference:** Continuous stereo PCM from TeamCode would require either:

1. Official DS playback APIs (`SoundPlayer`, `Telemetry.speak`, or a future SDK hook), or
2. Team code **inside** the DS process (conflicts with R706), or
3. A second app on the Driver Hub (rules-ambiguous; extra comms likely illegal in MATCH).

## 4. Permissions

| Permission / feature | When needed | Match implication |
| -------------------- | ----------- | ----------------- |
| No special permission for `SoundPool` / `AudioTrack` playback to the default output | Typical | N/A |
| `RECORD_AUDIO` | Microphone | **Out of scope.** ECHO is not a sensing system. |
| `BLUETOOTH` / `BLUETOOTH_CONNECT` | BT audio | **Illegal in MATCH** (R711, R904, E301) |
| USB host (`android.hardware.usb.host`) | USB DAC | Device image dependent |
| Notification / foreground service | Companion app | Not a match path |

## 5. Device discovery and reconnection

**Verified (AOSP):** USB audio peripherals are enumerated as USB devices; Android routes playback when policy matches.

**Unknown on Driver Hub:**

- Whether inserting a UAC headset changes the default output away from a (possibly nonexistent) speaker
- Whether unplugging reverts cleanly
- Whether USB audio enumeration **disrupts HID gamepad** endpoints on a composite device
- Whether a composite gamepad+audio device causes Android to treat the gamepad as an audio sink (community reports exist for DualShock 4 on generic Android; **not** Driver Hub measurements)

ECHO policy (**proposal**): if the selected output device is lost, **silence** and record `AUDIO_DEVICE_LOST`. Never guess a new device. Never affect robot control.

## 6. Latency expectations (order of magnitude, not measurements)

| Path | Expected character | Status |
| ---- | ------------------ | ------ |
| Local `AudioTrack` on same device | Tens of ms if buffer is small | **Unknown** on Driver Hub |
| `SoundPool` via RC→DS command | Loop time + radio + DS mixer | **Unknown**; wiki warns first plays can be delayed |
| USB isochronous DAC | Additional controller latency | **Unknown** |
| Bluetooth A2DP | High; also illegal in MATCH | Rejected |

No ECHO latency number may be published as “Driver Hub fact” until Phase 4 hardware logs exist.

## 7. What ECHO will not do on Android until gated

- Request microphone permission
- Enable Bluetooth
- Install a second MATCH communication channel
- Modify the official DS APK
- Assume USB audio routing exists on REV-31-1596
