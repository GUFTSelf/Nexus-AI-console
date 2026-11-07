# Business Ecosystem Network (BEN) — web prototype

This directory contains the first responsive web experience for the Business Ecosystem Network, designed as a companion module to Nexus AI.

## Current scope

- Podcast landing page with episode-format cards.
- Business directory layout with client-side category/text filtering.
- Business interest form UI.
- Responsive navigation and mobile layout.

## Important prototype boundaries

This is a front-end prototype only. It does not currently:
- authenticate users;
- store or submit business applications;
- read or write Firebase/Firestore data;
- upload or host podcast audio/video;
- publish episodes or accept payments;
- provide live business listings.

The form intentionally does not transmit or persist personal information. Sample business cards and episode concepts are placeholders.

## Next integration milestones

1. Confirm the intended Firebase project and enable the required Firestore collections and Storage bucket.
2. Add Firebase Authentication and role-based access (business owner, editor, administrator).
3. Create reviewed business-profile and podcast-submission workflows.
4. Connect episode media to an approved audio/video hosting provider or Firebase Storage, with appropriate size, bandwidth, and cost controls.
5. Add moderation, consent/release records, privacy policy, terms, and abuse reporting before public launch.
6. Add verified VEK audit events for administrative actions only after the business rules and event schema are agreed.

## Local preview

Open `web/index.html` in a browser. No build step or third-party JavaScript dependency is required.
