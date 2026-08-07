package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.VerificationStatus
import com.example.ui.theme.*

@Composable
fun StatusBadge(
    status: VerificationStatus,
    modifier: Modifier = Modifier
) {
    val (bgColor, textColor, borderColor) = when (status) {
        VerificationStatus.VERIFIED -> Triple(StatusVerified.copy(alpha = 0.15f), StatusVerified, StatusVerified.copy(alpha = 0.5f))
        VerificationStatus.CONDITIONALLY_VERIFIED -> Triple(StatusConditional.copy(alpha = 0.15f), StatusConditional, StatusConditional.copy(alpha = 0.5f))
        VerificationStatus.INCONCLUSIVE -> Triple(StatusInconclusive.copy(alpha = 0.15f), StatusInconclusive, StatusInconclusive.copy(alpha = 0.5f))
        VerificationStatus.CONFLICTING_EVIDENCE -> Triple(StatusConflicting.copy(alpha = 0.15f), StatusConflicting, StatusConflicting.copy(alpha = 0.5f))
        VerificationStatus.UNSUPPORTED -> Triple(StatusUnsupported.copy(alpha = 0.15f), StatusUnsupported, StatusUnsupported.copy(alpha = 0.5f))
        VerificationStatus.HIGH_RISK_REVIEW_REQUIRED -> Triple(StatusHighRisk.copy(alpha = 0.15f), StatusHighRisk, StatusHighRisk.copy(alpha = 0.5f))
    }

    Box(
        modifier = modifier
            .background(bgColor, RoundedCornerShape(4.dp))
            .border(1.dp, borderColor, RoundedCornerShape(4.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(
            text = status.displayName.uppercase(),
            color = textColor,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.5.sp
        )
    }
}
