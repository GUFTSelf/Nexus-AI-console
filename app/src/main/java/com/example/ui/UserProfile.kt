package com.example.ui

enum class AppTier(
    val displayName: String,
    val monthlyPrice: String,
    val hexColor: String,
    val description: String,
    val maxVerificationsPerDay: Int,
    val features: List<String>
) {
    FREE(
        displayName = "Free",
        monthlyPrice = "$0",
        hexColor = "#8E9AA8",
        description = "Standard verification guard with basic trace inspector.",
        maxVerificationsPerDay = 5,
        features = listOf(
            "5 Verifications / day",
            "Basic VEK Trace Inspector",
            "Phishing & Spam Filter"
        )
    ),
    PRO(
        displayName = "Pro",
        monthlyPrice = "$19/mo",
        hexColor = "#00F0FF", // CyberCyan
        description = "For analysts, journalists, and power users needing detailed reports.",
        maxVerificationsPerDay = 100,
        features = listOf(
            "100 Verifications / day",
            "Gemini 3.5 Flash Model",
            "JSON & Evidence Export",
            "Custom Policy Toggles",
            "Priority VEK Pipeline"
        )
    ),
    PREMIUM(
        displayName = "Premium",
        monthlyPrice = "$49/mo",
        hexColor = "#CCFF00", // ElectricLime
        description = "Full-featured AI Console suite for enterprises & security teams.",
        maxVerificationsPerDay = 10000,
        features = listOf(
            "Unlimited Verifications",
            "Gemini 3.1 Pro & Flash Models",
            "Multi-Tenant Industry Workspaces",
            "PDF & File Upload Audit",
            "Human Auditor Review Workflow"
        )
    ),
    ULTRA(
        displayName = "Ultra",
        monthlyPrice = "$199/mo",
        hexColor = "#E040FB", // StatusInconclusive / Quantum Purple
        description = "Quantum-grade zero-knowledge VEK proofs & 24/7 GUTS SLA.",
        maxVerificationsPerDay = Int.MAX_VALUE,
        features = listOf(
            "Zero-Knowledge VEK Proof Engine",
            "QVEK Quantum Acceleration Bridge",
            "Gemini Ultra & Fine-Tuned Models",
            "On-Premises / Air-Gapped API",
            "24/7 GUTS SLA Technical Support"
        )
    )
}

data class UserProfile(
    val uid: String,
    val displayName: String,
    val email: String,
    val photoUrl: String? = null,
    val tier: AppTier = AppTier.PRO,
    val isEnrolled: Boolean = true,
    val enrolledAt: Long = System.currentTimeMillis()
)
