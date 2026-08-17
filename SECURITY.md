# Security Policy

## Supported versions

| Version | Supported |
| ------- | --------- |
| 0.1.x   | Yes       |

## Reporting a vulnerability

Please do **not** open a public issue for security problems that could put robots, students, drivers, or machines at risk.

Prefer:

1. GitHub Security Advisories for this repository (when available), or
2. A private email contact published by the maintainers

Include:

- A description of the issue
- Steps to reproduce
- Impact assessment (for example: unexpected audio during a match, inability to mute, credential exposure, or a path that commands robot hardware)

## Safety expectations for this project

ECHO intentionally:

- **Never commands motors, servos, autonomous actions, or path following**
- Keeps **physical and competition audio disabled by default**
- Treats missing, stale, unknown, or low-confidence information as a reason to **remain silent**
- Requires an immediate driver disable that does not affect robot operation
- Does not claim driving benefit until a controlled experiment produces evidence

If you discover a path that plays match audio without an explicit feature flag, that bypasses mute/disable, that uses an unauthorized wireless channel, or that can command robot hardware, treat it as a safety defect.

## Secrets

Never store passwords, Wi-Fi credentials, API keys, or tokens in the repository, issues, or exported logs.
