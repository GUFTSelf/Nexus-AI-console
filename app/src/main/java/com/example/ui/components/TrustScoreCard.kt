package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.TrustScores
import com.example.ui.theme.*

@Composable
fun TrustScoreCard(
    scores: TrustScores,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(CyberSurface, RoundedCornerShape(8.dp))
            .border(1.dp, MutedBorder, RoundedCornerShape(8.dp))
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "VEK MULTI-FACTOR TRUST ASSESSMENT",
                    color = ElectricLime,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "Separate metric decomposition - model confidence excluded as proof",
                    color = MutedText,
                    fontSize = 12.sp
                )
            }
            Box(
                modifier = Modifier
                    .background(
                        when {
                            scores.overallTrust >= 80 -> StatusVerified.copy(alpha = 0.2f)
                            scores.overallTrust >= 50 -> StatusConditional.copy(alpha = 0.2f)
                            else -> StatusUnsupported.copy(alpha = 0.2f)
                        },
                        RoundedCornerShape(6.dp)
                    )
                    .border(
                        1.dp,
                        when {
                            scores.overallTrust >= 80 -> StatusVerified
                            scores.overallTrust >= 50 -> StatusConditional
                            else -> StatusUnsupported
                        },
                        RoundedCornerShape(6.dp)
                    )
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Text(
                    text = "${scores.overallTrust}% OVERALL",
                    color = when {
                        scores.overallTrust >= 80 -> StatusVerified
                        scores.overallTrust >= 50 -> StatusConditional
                        else -> StatusUnsupported
                    },
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 14.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        ScoreBarItem(title = "Evidence Strength", score = scores.evidenceStrength, description = "Direct primary source backing vs missing proof")
        ScoreBarItem(title = "Source Reliability", score = scores.sourceReliability, description = "Publisher authority, domain TLS, & accreditation")
        ScoreBarItem(title = "Claim Consistency", score = scores.claimConsistency, description = "Absence of contradictions across official databases")
        ScoreBarItem(title = "Recency & Timeliness", score = scores.recency, description = "Age of citations relative to dynamic facts")
        ScoreBarItem(title = "Policy Compliance", score = scores.policyCompliance, description = "Alignment with active domain governance rules")
    }
}

@Composable
private fun ScoreBarItem(
    title: String,
    score: Int,
    description: String
) {
    val barColor = when {
        score >= 80 -> StatusVerified
        score >= 50 -> StatusConditional
        else -> StatusUnsupported
    }

    Column(modifier = Modifier.padding(vertical = 6.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = title, color = OffWhiteText, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
            Text(text = "$score / 100", color = barColor, fontSize = 13.sp, fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.height(4.dp))
        LinearProgressIndicator(
            progress = { score / 100f },
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp)),
            color = barColor,
            trackColor = CyberSurfaceVariant
        )
        Text(text = description, color = MutedText, fontSize = 10.sp, modifier = Modifier.padding(top = 2.dp))
    }
}
