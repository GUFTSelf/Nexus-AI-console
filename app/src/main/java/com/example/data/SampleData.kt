package com.example.data

import com.example.model.AuditLogEntry
import com.example.model.VerificationCase
import com.example.model.VerificationPolicy
import com.example.model.VerificationStatus

object SampleData {

    fun getDemoCases(): List<VerificationCase> {
        return listOf(
            // Demo Case 1: Consumer Scam Message
            VerificationCase(
                caseId = "NX-2026-DEMO-01",
                title = "Urgent Account Suspension SMS Claim",
                rawInput = "ALERT: Your Chase Online Banking has been restricted due to unauthorized login attempts from IP 192.168.1.4. Visit https://chase-sec-verify-unlock.com/login immediately to verify your identity and restore access within 2 hours or your funds will be frozen.",
                contentType = "SMS / Phishing Message",
                domain = "General Consumer",
                timestamp = System.currentTimeMillis() - 3600000 * 2, // 2 hours ago
                summary = "High risk spoofing & phishing scam detected. Domain is unverified, non-official, and utilizes deceptive high-urgency pressure tactics.",
                status = VerificationStatus.UNSUPPORTED,
                riskLevel = "Critical",
                claimsJson = """[
                    {
                        "id": "c1",
                        "claim": "Chase Online Banking has restricted the account due to security breach",
                        "classification": "FINANCIAL",
                        "assessment": "CONTRADICTED",
                        "supportingEvidence": [],
                        "conflictingEvidence": [
                            {"title":"Official Chase Security Alert Guidelines","publisher":"JPMorgan Chase & Co.","date":"2026-01-15","url":"https://www.chase.com/digital/resources/privacy-security","sourceType":"Primary Source","relevance":"High"}
                        ],
                        "missingEvidence": ["Official banking transaction log", "Authenticated portal notice"],
                        "reasoningSummary": "Official Chase bank security protocols strictly state that account suspension notices never contain third-party external domain URLs or demand password re-entry on non-chase.com hostnames."
                    },
                    {
                        "id": "c2",
                        "claim": "URL chase-sec-verify-unlock.com is an official Chase verification portal",
                        "classification": "IDENTITY_RELATED",
                        "assessment": "CONTRADICTED",
                        "supportingEvidence": [],
                        "conflictingEvidence": [
                            {"title":"WHOIS Domain Registration Inspection","publisher":"ICANN Registry","date":"2026-08-01","url":"https://whois.icann.org","sourceType":"Primary Source","relevance":"High"}
                        ],
                        "missingEvidence": ["TLS Certificate belonging to JPMorgan Chase & Co."],
                        "reasoningSummary": "WHOIS registry reveals domain was registered 3 days ago via an anonymous offshore registrar. No SSL EV organization validation exists."
                    }
                ]""",
                scoresJson = """{
                    "evidenceStrength": 12,
                    "sourceReliability": 8,
                    "claimConsistency": 15,
                    "recency": 95,
                    "policyCompliance": 5,
                    "overallTrust": 9
                }""",
                rulesJson = """[
                    {"ruleId":"R-101","ruleName":"Official Domain TLS Validation","passed":false,"description":"Domain host fails official institution SSL/TLS certificate chain check."},
                    {"ruleId":"R-102","ruleName":"Urgency Pressure Pattern Check","passed":false,"description":"Message triggers high-velocity financial panic pattern (2-hour ultimatum)."},
                    {"ruleId":"R-103","ruleName":"Primary Banking Source Correlation","passed":false,"description":"No corresponding account notification exists in legitimate banking system."}
                ]""",
                warningsJson = """[
                    "CRITICAL: Do NOT click links or enter credentials on chase-sec-verify-unlock.com.",
                    "Phishing attack pattern detected matching active credential harvester campaign.",
                    "Contact official customer service directly via the phone number on your payment card."
                ]""",
                actionsJson = """[
                    "Report message to FTC Phishing Center and bank security department.",
                    "Block sender phone number / sender ID immediately.",
                    "Run device credential scan if link was clicked."
                ]""",
                humanReviewRequired = false,
                reviewerNotes = "Automated VEK Kernel deterministic scam filter flagged zero trust index.",
                reviewerStatus = "Approved",
                traceId = "VEK-TR-9901-SCAM",
                demonstrationTraceHash = "a7f82b01c342d991e0a2948270f821cc8392a831",
                isDemonstration = true
            ),

            // Demo Case 2: Medical Claim Requiring Professional Review
            VerificationCase(
                caseId = "NX-2026-DEMO-02",
                title = "Herbal Supplement Cardiac Efficacy Claim",
                rawInput = "Clinical studies prove that 500mg daily intake of Nano-Resveratrol Bio-Boost completely eliminates the need for FDA-approved statins and blood pressure medication in Stage 2 hypertension patients without any side effects.",
                contentType = "Medical Article Excerpt",
                domain = "Healthcare",
                timestamp = System.currentTimeMillis() - 3600000 * 18, // 18 hours ago
                summary = "Unsubstantiated medical claim asserting a natural supplement completely replaces prescription cardiac drugs. Requires accredited clinical reviewer escalation.",
                status = VerificationStatus.HIGH_RISK_REVIEW_REQUIRED,
                riskLevel = "High",
                claimsJson = """[
                    {
                        "id": "c1",
                        "claim": "Nano-Resveratrol Bio-Boost eliminates need for FDA-approved statins and antihypertensives",
                        "classification": "MEDICAL",
                        "assessment": "UNVERIFIED",
                        "supportingEvidence": [
                            {"title":"In-vitro anti-inflammatory effect of polyphenol extracts","publisher":"Journal of Dietary Bioactive Compounds","date":"2024-05-12","url":"","sourceType":"Secondary Source","relevance":"Low"}
                        ],
                        "conflictingEvidence": [
                            {"title":"AHA Clinical Practice Guidelines for Management of High Blood Pressure","publisher":"American Heart Association / ACC","date":"2025-11-10","url":"https://www.heart.org/guidelines","sourceType":"Primary Source","relevance":"High"}
                        ],
                        "missingEvidence": ["FDA Phase III Randomized Double-Blind Controlled Trial Data", "Peer-reviewed human clinical study publication"],
                        "reasoningSummary": "Supplements are not evaluated or approved by the FDA to treat or cure cardiovascular diseases. Discontinuing prescribed antihypertensives based on secondary cellular studies carries severe risk of stroke or cardiac failure."
                    }
                ]""",
                scoresJson = """{
                    "evidenceStrength": 35,
                    "sourceReliability": 42,
                    "claimConsistency": 28,
                    "recency": 80,
                    "policyCompliance": 30,
                    "overallTrust": 33
                }""",
                rulesJson = """[
                    {"ruleId":"R-MED-01","ruleName":"FDA Approved Drug Replacement Constraint","passed":false,"description":"Claims asserting natural supplements supersede FDA prescription drugs fail medical safety threshold."},
                    {"ruleId":"R-MED-02","ruleName":"Mandatory Clinical Escalation Policy","passed":false,"description":"High-risk cardiac claims require board-certified clinical review approval."}
                ]""",
                warningsJson = """[
                    "HEALTH WARNING: Never halt or modify prescribed cardiovascular medication without consulting a licensed physician.",
                    "Medical domain policy activated: Automated AI confidence scores CANNOT certify therapeutic safety.",
                    "Escalated to Healthcare Workspace Review Queue for Accredited Medical Reviewer evaluation."
                ]""",
                actionsJson = """[
                    "Assign case to Chief Medical Compliance Officer.",
                    "Attach AHA/ACC Guideline citations to patient advisory notice.",
                    "File FTC/FDA Health Fraud Alert notice if marketed commercially as a cure."
                ]""",
                humanReviewRequired = true,
                reviewerNotes = "Pending review by Dr. Sarah Jenkins (Cardiology Advisory Lead).",
                reviewerStatus = "Pending",
                traceId = "VEK-TR-8820-MED",
                demonstrationTraceHash = "c491e03a9876f11202837482a90192834b712c99",
                isDemonstration = true
            ),

            // Demo Case 3: Enterprise Supplier-Authorization Claim
            VerificationCase(
                caseId = "NX-2026-DEMO-03",
                title = "Aerospace Supplier CMMC Level 3 & ISO-27001 Certification",
                rawInput = "AeroTech Defense Systems LLC hereby certifies that all sub-tier manufacturing components supplied under Contract #DEF-2026-8819 comply fully with DoD CMMC 2.0 Level 3 cyber standards and ISO/IEC 27001:2022 ISMS requirements, verified by Third-Party Assessment Organization (C3PAO) Audit #C3P-9921.",
                contentType = "Defense Procurement Compliance Document",
                domain = "Defense",
                timestamp = System.currentTimeMillis() - 3600000 * 36, // 36 hours ago
                summary = "Conditionally verified enterprise compliance document. ISO 27001 certificate verified via IAF certSearch; CMMC Level 3 accreditation confirmed pending annual C3PAO site re-audit.",
                status = VerificationStatus.CONDITIONALLY_VERIFIED,
                riskLevel = "Medium",
                claimsJson = """[
                    {
                        "id": "c1",
                        "claim": "AeroTech Defense Systems holds valid ISO/IEC 27001:2022 ISMS Certification",
                        "classification": "SECURITY_SENSITIVE",
                        "assessment": "SUPPORTED",
                        "supportingEvidence": [
                            {"title":"IAF CertSearch Official Global Accreditation Database Record #ISO-27001-99812","publisher":"International Accreditation Forum","date":"2026-02-14","url":"https://www.iafcertsearch.org","sourceType":"Primary Source","relevance":"High"}
                        ],
                        "conflictingEvidence": [],
                        "missingEvidence": [],
                        "reasoningSummary": "Direct API cross-reference with International Accreditation Forum (IAF) confirms active ISO 27001 certification valid through Oct 2027."
                    },
                    {
                        "id": "c2",
                        "claim": "AeroTech Defense Systems holds DoD CMMC 2.0 Level 3 Accreditation via C3PAO Audit #C3P-9921",
                        "classification": "LEGAL",
                        "assessment": "PARTIALLY_SUPPORTED",
                        "supportingEvidence": [
                            {"title":"CyberAB C3PAO Marketplace Audit Registration #C3P-9921","publisher":"The CyberAB (CMMC Accreditation Body)","date":"2025-09-30","url":"https://cyberab.org","sourceType":"Primary Source","relevance":"High"}
                        ],
                        "conflictingEvidence": [],
                        "missingEvidence": ["2026 Annual On-site Surveillance Audit Renewal Signature"],
                        "reasoningSummary": "Audit record exists in CyberAB portal; however, annual surveillance audit is due within 30 days."
                    }
                ]""",
                scoresJson = """{
                    "evidenceStrength": 92,
                    "sourceReliability": 95,
                    "claimConsistency": 89,
                    "recency": 78,
                    "policyCompliance": 88,
                    "overallTrust": 88
                }""",
                rulesJson = """[
                    {"ruleId":"R-DEF-01","ruleName":"DoD CyberAB Portal Primary Verification","passed":true,"description":"Certification verified against CyberAB accreditation body database."},
                    {"ruleId":"R-DEF-02","ruleName":"IAF CertSearch Validation","passed":true,"description":"ISO 27001 certificate verified directly against IAF primary register."},
                    {"ruleId":"R-DEF-03","ruleName":"Annual Surveillance Audit Window Check","passed":false,"description":"Surveillance audit renewal window opens within 30 days."}
                ]""",
                warningsJson = """[
                    "CONDITION: Certification is valid but requires 2026 C3PAO annual surveillance audit proof within 30 days.",
                    "Defense Workspace rule enforced: Supplier remains authorized for non-classified component delivery during renewal window."
                ]""",
                actionsJson = """[
                    "Issue conditional procurement clearance for Contract #DEF-2026-8819.",
                    "Set automated VEK calendar trigger to request updated C3PAO audit documentation on Sept 1, 2026."
                ]""",
                humanReviewRequired = true,
                reviewerNotes = "Reviewed by Defense Compliance Officer Vance Mitchell. Conditional authorization granted.",
                reviewerStatus = "Approved",
                traceId = "VEK-TR-7001-DEFENSE",
                demonstrationTraceHash = "e912a7f01c82347b102938472a102938217ccb11",
                isDemonstration = true
            )
        )
    }

    fun getDemoPolicies(): List<VerificationPolicy> {
        return listOf(
            VerificationPolicy("POL-GEN", "General Consumer Default Guard", "General Consumer", requirePrimarySources = false, requireRecentSources = true, escalateConflicts = true, requireHumanApproval = false, enabled = true),
            VerificationPolicy("POL-FIN", "Financial Services & Banking Governance", "Financial Services", requirePrimarySources = true, requireRecentSources = true, escalateConflicts = true, requireHumanApproval = true, enabled = true),
            VerificationPolicy("POL-MED", "Healthcare Clinical Review & Therapeutics", "Healthcare", requirePrimarySources = true, requireRecentSources = true, escalateConflicts = true, requireHumanApproval = true, enabled = true),
            VerificationPolicy("POL-DEF", "Defense & National Security Supply Chain", "Defense", requirePrimarySources = true, requireRecentSources = true, escalateConflicts = true, requireHumanApproval = true, enabled = true),
            VerificationPolicy("POL-GOV", "Government Public Information Integrity", "Government", requirePrimarySources = true, requireRecentSources = true, escalateConflicts = true, requireHumanApproval = false, enabled = true),
            VerificationPolicy("POL-QVEK", "QVEK Quantum Workflows & Cryptography", "Quantum Computing", requirePrimarySources = true, requireRecentSources = true, escalateConflicts = true, requireHumanApproval = true, enabled = true)
        )
    }

    fun getDemoAuditLogs(): List<AuditLogEntry> {
        return listOf(
            AuditLogEntry(
                timestamp = System.currentTimeMillis() - 3600000 * 2,
                caseId = "NX-2026-DEMO-01",
                action = "Automated Scam Filter Rule Execution",
                userRole = "Consumer Guardian Engine",
                department = "Threat Intelligence",
                details = "VEK Kernel evaluated domain chase-sec-verify-unlock.com against official banking registers. Result: UNSUPPORTED (Phishing).",
                traceHash = "a7f82b01c342d991e0a2948270f821cc8392a831"
            ),
            AuditLogEntry(
                timestamp = System.currentTimeMillis() - 3600000 * 18,
                caseId = "NX-2026-DEMO-02",
                action = "Escalation to Healthcare Reviewer Queue",
                userRole = "VEK Compliance Engine",
                department = "Medical Regulatory Affairs",
                details = "High-risk cardiac therapeutic claim detected. Automated confidence score suppressed per medical safety mandate. Assigned to Dr. Sarah Jenkins.",
                traceHash = "c491e03a9876f11202837482a90192834b712c99"
            ),
            AuditLogEntry(
                timestamp = System.currentTimeMillis() - 3600000 * 36,
                caseId = "NX-2026-DEMO-03",
                action = "Conditional Authorization Approval",
                userRole = "Defense Compliance Analyst",
                department = "Defense Procurement",
                details = "Vance Mitchell approved conditional authorization for Contract #DEF-2026-8819 based on IAF CertSearch ISO 27001 validation.",
                traceHash = "e912a7f01c82347b102938472a102938217ccb11"
            )
        )
    }
}
