# Nexus AI Store Release Checklist

Status date: August 7, 2026

## Owner actions that require Thoeun or an authorized company representative

### Company identity

- Confirm the exact legal name, address, telephone number, and public support email for GUTS Deterministic Technology, LLC.
- Obtain or confirm the company's D-U-N-S number.
- Publish a functional company website on a company-controlled domain.
- Create monitored `support@...`, `privacy@...`, and `security@...` addresses on that domain.

### Google Play

- Enroll in a Google Play Console organization account and complete identity/organization verification.
- Pay Google's one-time registration fee.
- Create the Nexus AI app record using package name `com.gutsdeterministic.nexusai`. Treat this identifier as permanent after the first upload.
- Accept Play agreements and complete developer-profile, payments, tax, content-rating, data-safety, advertising, app-access, and target-audience declarations.
- Recruit the required closed testers if Google applies the personal-account production-access rule to the selected account.

Google currently states that new apps and updates must target Android 16/API 36 beginning August 31, 2026. This project already targets API 36, but the requirement must be rechecked on submission day.

### Apple

- Enroll GUTS Deterministic Technology, LLC in the Apple Developer Program as an organization.
- Use an Apple Account with two-factor authentication, a company-domain work email, a functional company website, the D-U-N-S number, and legal authority to bind the company.
- Accept Apple agreements and complete annual membership payment.
- Create the App Store Connect record and confirm the final bundle identifier (currently `com.gutsdeterministic.nexusai`).
- Complete banking, tax, privacy, age-rating, export-compliance, content-rights, and app-review declarations.
- Provide App Review with a working demo account or a fully functional review mode if account-gated features remain.

## Engineering gates before store upload

- [ ] Android and iOS CI pass from a clean checkout.
- [ ] All deterministic cross-platform test vectors produce identical canonical JSON and SHA-256 values.
- [ ] No secret appears in source, APK, AAB, IPA, logs, screenshots, or repository history.
- [ ] Production AI calls use a protected backend; direct mobile Gemini calls remain disabled.
- [ ] Authentication is configured with real Google/Firebase and Apple credentials or removed from the release.
- [ ] Account deletion works on every platform where account creation is enabled.
- [ ] Local-data deletion and cloud-data deletion are documented and tested.
- [ ] Claim verification does not present model-generated or cached demonstration sources as authenticated evidence.
- [ ] Medical, legal, financial, defense, and government workflows enforce the appropriate review policy.
- [ ] Privacy policy and support pages are live over HTTPS.
- [ ] Store screenshots, description, keywords, support URL, privacy URL, and review notes are complete and accurate.
- [ ] Accessibility, offline behavior, network failures, rotation, small-screen layout, and crash behavior are tested.
- [ ] Android release App Bundle is signed with the company upload key and enrolled in Play App Signing.
- [ ] iOS archive is signed with the company team and validated through Xcode/App Store Connect.
- [ ] TestFlight and Google closed/internal testing are completed before production submission.

## Official references

- Google Play registration: https://support.google.com/googleplay/android-developer/answer/6112435
- Google Play target API policy: https://support.google.com/googleplay/android-developer/answer/11926878
- Google Play testing requirements: https://support.google.com/googleplay/android-developer/answer/14151465
- Google Play developer verification: https://support.google.com/googleplay/android-developer/answer/10841920
- Apple Developer enrollment: https://developer.apple.com/programs/enroll/
- Apple organization enrollment requirements: https://developer.apple.com/help/account/membership/program-enrollment/
- Apple App Review Guidelines: https://developer.apple.com/app-store/review/guidelines/
- Apple app privacy configuration: https://developer.apple.com/help/app-store-connect/manage-app-information/manage-app-privacy/
- Apple export compliance: https://developer.apple.com/help/app-store-connect/manage-app-information/overview-of-export-compliance/
