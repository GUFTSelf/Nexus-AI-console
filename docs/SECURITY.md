# Nexus AI Security and Backend Boundary

## Release rule

Production mobile binaries must not contain Gemini provider credentials, release-keystore passwords, Apple signing credentials, Firebase service-account credentials, or store API keys.

The current direct Gemini REST client is restricted to Android debug builds. Production AI features require a server-side gateway that:

1. authenticates the Nexus user or verified app instance;
2. applies rate limits and abuse controls;
3. validates request size and content type;
4. selects an allowed model and fixed policy version;
5. keeps the provider credential in a managed secret store;
6. returns a versioned response envelope;
7. records only the minimum audit data required by the active retention policy.

## Canonical record boundary

The canonical execution hash covers only stable, decision-relevant fields:

- schema version;
- initial value;
- ordered operation list;
- operand, before-state, and after-state values;
- final value;
- disposition.

The following fields must remain outside the canonical record:

- timestamps;
- case IDs and trace IDs;
- device identifiers;
- user identity;
- network latency;
- probabilistic model commentary.

This separation allows audit records to remain unique while identical normalized executions retain identical hashes.

## Required production controls

- HTTPS only with platform network-security defaults.
- Firebase App Check or an equivalent app-attestation control for the backend.
- Server-side authentication and authorization for enterprise workspaces.
- Per-user and per-tenant data isolation.
- Key rotation and incident-revocation procedure.
- Dependency, static-analysis, secret-scanning, unit-test, and release-signing checks in CI.
- A documented retention period and account/data-deletion workflow.
- No representation that cached, hardcoded, or model-generated sources are authenticated evidence.

## Reporting

Before public launch, replace this section with a monitored security-contact email and a vulnerability-reporting policy hosted on the official GUTS Deterministic Technology domain.
