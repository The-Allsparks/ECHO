# AMPER integration

AMPER owns electrical observation and power warnings. ECHO must not override AMPER limits.

Optional field `amperWarning` (present + severity). Maps to `WARN_AMPER` only when AMPER already classified a warning. ECHO does not compute brownout itself.

Flag `amperAdapter` default **false**. Rate-limited. Distinct timbre from MIMIC/BEACON.
