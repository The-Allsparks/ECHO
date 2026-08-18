# HELM integration

HELM owns intent and bounded task selection. ECHO may render a HELM-selected target **only** when `EchoFeatureFlags.helmTargetSource()` is true **and** the snapshot marks `targetSource=HELM`.

ECHO still does not execute HELM tasks. If the flag is false, a HELM-sourced target is rejected (`MISSING_CAPABILITY`).
