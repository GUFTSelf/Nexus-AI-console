# Nexus AI Store Listing Draft

## Shared positioning

**Name:** Nexus AI
**Category:** Utilities / Productivity
**Release:** 1.0

**One-line positioning:** A transparent console for deterministic execution, replay comparison, and inspectable integrity records.

## Google Play

**Short description:**
Run deterministic tasks, compare replays, and inspect canonical VEK records.

**Full description:**

Nexus AI introduces a transparent way to inspect deterministic computation. Enter an ordered arithmetic task, execute it through the VEK canonical engine, and review every state transition from the initial value to the final result.

Use replay comparison to repeat the same normalized task and confirm whether its canonical records and SHA-256 integrity commitments match. Timestamps, random audit identifiers, and device metadata remain outside the canonical record so they do not change the reproducible commitment.

Version 1.0 focuses on offline deterministic execution and auditability. A hash is presented as an integrity commitment—not as automatic proof that an external factual claim is true. Nexus AI does not replace qualified medical, legal, financial, security, or other professional review.

Core features:

- Ordered deterministic arithmetic execution
- Inspectable before-state and after-state transitions
- Canonical JSON execution records
- Replay-equivalence comparison
- SHA-256 integrity commitments
- Local verification history and audit records
- Clear separation between deterministic execution and evidence verification

## Apple App Store

**Subtitle:**
Canonical execution and replay

**Promotional text:**
Run a deterministic task, inspect each transition, and confirm replay equivalence with a canonical SHA-256 integrity record.

**Keywords:**
deterministic,replay,verification,audit,integrity,execution,VEK,canonical

**Description:**
Use the Google Play full description above, adjusted only for platform-specific feature differences confirmed in the final iOS build.

## App Review notes

Nexus AI 1.0 can be evaluated without an account. Use the preloaded deterministic test request:

```text
MODE: REPLAY_COMPARISON
INPUT: 7
OPERATIONS: MULTIPLY 8; SUBTRACT 11; DIVIDE 5; ADD 9
REPLAY_COUNT: 2
```

Expected final value: `18`
Expected SHA-256: `3e408a514a78d1a28568de1e838a23e747231445276418c131603d52e013d996`

The release build does not require login and does not expose simulated subscription purchasing. Direct client-side Gemini access is disabled. Any future cloud AI feature will use a protected backend and will receive a separate review description.

## Required replacements before submission

- Support URL
- Privacy-policy URL
- Marketing URL
- Monitored support email
- Final screenshots and app icon
- Final age-rating and data-safety answers based on the signed production binaries
