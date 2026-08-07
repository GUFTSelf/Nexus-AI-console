package com.example.model

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class VerificationStatus(val displayName: String, val description: String) {
    VERIFIED("Verified", "All claims corroborated by reliable primary sources."),
    CONDITIONALLY_VERIFIED("Conditionally Verified", "Verified subject to specific conditions or context limits."),
    INCONCLUSIVE("Inconclusive", "Insufficient evidence to prove or disprove core claims."),
    CONFLICTING_EVIDENCE("Conflicting Evidence", "Contradictory information detected across sources."),
    UNSUPPORTED("Unsupported", "Claims lack backing evidence or contain fabricated details."),
    HIGH_RISK_REVIEW_REQUIRED("High-Risk Review Required", "Sensitive domain claim requiring accredited human review.")
}

enum class ClaimClassification(val displayName: String) {
    FACTUAL("Factual"),
    PREDICTIVE("Predictive"),
    OPINION("Opinion"),
    INSTRUCTION("Instruction"),
    IDENTITY_RELATED("Identity-Related"),
    FINANCIAL("Financial"),
    MEDICAL("Medical"),
    LEGAL("Legal"),
    SECURITY_SENSITIVE("Security-Sensitive"),
    UNSUPPORTED("Unsupported / Unverifiable")
}

enum class ClaimAssessment(val displayName: String) {
    SUPPORTED("Supported"),
    CONTRADICTED("Contradicted"),
    UNVERIFIED("Unverified"),
    PARTIALLY_SUPPORTED("Partially Supported")
}

data class EvidenceItem(
    val title: String,
    val publisher: String,
    val date: String,
    val url: String = "",
    val sourceType: String = "Primary Source", // Primary Source, Secondary Source, User Provided, Official Portal
    val relevance: String = "High"
)

data class DecomposedClaim(
    val id: String,
    val claim: String,
    val classification: ClaimClassification,
    val assessment: ClaimAssessment,
    val supportingEvidence: List<EvidenceItem> = emptyList(),
    val conflictingEvidence: List<EvidenceItem> = emptyList(),
    val missingEvidence: List<String> = emptyList(),
    val reasoningSummary: String
)

data class TrustScores(
    val evidenceStrength: Int,    // 0 - 100
    val sourceReliability: Int,   // 0 - 100
    val claimConsistency: Int,    // 0 - 100
    val recency: Int,             // 0 - 100
    val policyCompliance: Int,    // 0 - 100
    val overallTrust: Int         // 0 - 100
)

data class RuleResult(
    val ruleId: String,
    val ruleName: String,
    val passed: Boolean,
    val description: String
)

@Entity(tableName = "verification_cases")
data class VerificationCase(
    @PrimaryKey val caseId: String,
    val title: String,
    val rawInput: String,
    val contentType: String, // Text, Article, Scam Message, Medical Claim, Supplier Authorization
    val domain: String,      // Consumer, Healthcare, Financial, Defense, Legal, Government, Quantum
    val timestamp: Long,
    val summary: String,
    val status: VerificationStatus,
    val riskLevel: String,   // Low, Medium, High, Critical
    val claimsJson: String,  // JSON serialized list of DecomposedClaim
    val scoresJson: String,  // JSON serialized TrustScores
    val rulesJson: String,   // JSON serialized list of RuleResult
    val warningsJson: String,// JSON serialized list of String
    val actionsJson: String, // JSON serialized list of String
    val humanReviewRequired: Boolean,
    val reviewerNotes: String? = null,
    val reviewerStatus: String? = "Pending", // Pending, Approved, Rejected, Escalated
    val traceId: String,
    val demonstrationTraceHash: String,
    val isDemonstration: Boolean = false
)

@Entity(tableName = "verification_policies")
data class VerificationPolicy(
    @PrimaryKey val id: String,
    val name: String,
    val domain: String,
    val requirePrimarySources: Boolean,
    val requireRecentSources: Boolean,
    val escalateConflicts: Boolean,
    val requireHumanApproval: Boolean,
    val enabled: Boolean = true
)

@Entity(tableName = "audit_logs")
data class AuditLogEntry(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val timestamp: Long,
    val caseId: String,
    val action: String,
    val userRole: String, // Consumer, Compliance Officer, Defense Analyst, Medical Reviewer
    val department: String,
    val details: String,
    val traceHash: String
)

@Entity(tableName = "local_execution_records")
data class LocalExecutionRecord(
    @PrimaryKey val id: String = java.util.UUID.randomUUID().toString(),
    val timestamp: Long = System.currentTimeMillis(),
    val mode: String, // "DETERMINISTIC_EXECUTION", "REPLAY_COMPARISON", "CLAIM_VERIFICATION"
    val title: String,
    val initialValue: Double = 0.0,
    val finalValue: Double = 0.0,
    val canonicalJson: String,
    val sha256Hash: String,
    val replayCount: Int = 1,
    val passStatus: Boolean = true,
    val rawInput: String = ""
)

