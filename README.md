# Nexus AI Console

Nexus AI is a mobile verification and deterministic-execution console built around the Verifiable Execution Kernel (VEK). The repository contains an Android application and an iOS release foundation.

## Current status

This branch is a release foundation, not a store-approved production release. It provides:

- explicit routing between claim verification and deterministic execution;
- an offline canonical arithmetic engine that does not call a language model;
- byte-identical replay comparison and SHA-256 integrity commitments;
- a cross-platform test vector shared by Android and iOS;
- Android Room persistence, audit records, and account deletion support;
- SwiftUI iPhone source generated with XcodeGen;
- Android and iOS continuous-integration workflows.

The canonical test vector starts with `7`, applies `MULTIPLY 8`, `SUBTRACT 11`, `DIVIDE 5`, and `ADD 9`, and must produce:

```text
final value: 18
SHA-256: 3e408a514a78d1a28568de1e838a23e747231445276418c131603d52e013d996
```

Release builds currently expose the offline VEK execution foundation. Development-only demonstration accounts, simulated tiers, direct Gemini calls, synthetic sample cases, and simulated subscription controls are excluded from release behavior. They must not be enabled publicly until the protected backend, real authentication, and platform billing are implemented and reviewed.

## Execution architecture

| Route | Purpose | Model involvement | Reproducible hash |
|---|---|---:|---:|
| `CLAIM_VERIFICATION` | Evaluate claims against evidence and policy | Optional development-only Gemini call | Integrity commitment |
| `DETERMINISTIC_EXECUTION` | Execute ordered arithmetic transitions | None | Yes |
| `REPLAY_COMPARISON` | Repeat and compare normalized records | None | Yes |
| `GENERAL_CHAT` | Conversational AI | Requires a protected server gateway for release | No VEK-verification claim |

Timestamps, random audit IDs, and device metadata are excluded from the canonical execution record. They may appear in an audit envelope without changing the canonical SHA-256 commitment.

## Android development

Requirements:

- JDK 17
- Android Studio compatible with Android Gradle Plugin 9.1.1
- Gradle 9.3.1
- Android SDK 36/36.1 and Build Tools 36.0.0

Open the repository in Android Studio and run the `app` configuration. The CI workflow installs Gradle directly because the original AI Studio export did not include a Gradle wrapper.

Development-only configuration belongs in an untracked `.env` file:

```properties
GEMINI_API_KEY=development-key-only
GOOGLE_WEB_CLIENT_ID=your-oauth-web-client-id
```

Never place a production Gemini credential in an APK or App Bundle. Release builds must use a protected server-side gateway and must not receive the provider key.

To build a signed Android App Bundle, configure these environment variables in a protected build environment:

```text
KEYSTORE_PATH
STORE_PASSWORD
KEY_ALIAS
KEY_PASSWORD
```

Then run:

```bash
gradle clean testDebugUnitTest lintDebug bundleRelease
```

## iPhone development

Requirements:

- macOS with Xcode
- XcodeGen (`brew install xcodegen`)
- an Apple Developer team for device signing and distribution

Generate and open the project:

```bash
cd ios
xcodegen generate
open NexusAI.xcodeproj
```

Set the Apple development team and confirm the bundle identifier before archiving. The current iOS foundation implements offline deterministic execution and replay. AI conversation and cloud account features should be added only through the protected cross-platform backend.

## Product-safety boundary

- A SHA-256 value is an integrity commitment, not automatically a mathematical proof.
- Deterministic verification applies to the normalized evidence or execution record, not to probabilistic model output.
- Claim verification must not present model-generated source names as authenticated evidence.
- Medical, legal, financial, defense, and other high-risk outputs require domain policies and appropriate human review.

## Release documentation

- [Store release checklist](docs/STORE_RELEASE_CHECKLIST.md)
- [Store listing draft](docs/STORE_LISTING_DRAFT.md)
- [Security and backend boundary](docs/SECURITY.md)
- [Privacy policy draft](docs/PRIVACY_POLICY_DRAFT.md)

Copyright © 2026 GUTS Deterministic Technology, LLC. No patent, source-code, trademark, or commercial license is granted by publication of this repository.
