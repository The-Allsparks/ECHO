# Hardware validation

**Status:** not started. Phase 4 is an approval gate.

Minimum spike (only after a defensible path exists in the current manual):

1. Enumerate audio devices on the Driver Hub without a custom DS APK.
2. Identify selected output.
3. One bounded test cue at default gain.
4. Measure start latency (TRACE timestamps).
5. Unplug/replug; gamepad still drives; DS still connected.
6. Confirm no extra SSID / Bluetooth.
7. Disable ECHO; workflow identical to stock.
8. Log results; do not merge as default competition config.

Until then, hardware validation required = **yes, all of the above, still open**.
