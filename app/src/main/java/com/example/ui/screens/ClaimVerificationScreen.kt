package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.NexusViewModel
import com.example.ui.theme.*
import org.json.JSONObject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClaimVerificationScreen(
    viewModel: NexusViewModel,
    modifier: Modifier = Modifier
) {
    val clipboardManager = LocalClipboardManager.current
    var claimInput by remember { mutableStateOf("") }
    var evaluatedClaim by remember { mutableStateOf<ClaimEvaluationResult?>(null) }
    var copiedFeedback by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(CyberBackground)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Mode Title Header
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(CyberSurfaceHeader, RoundedCornerShape(12.dp))
                    .border(1.dp, ElectricLime.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                    .padding(16.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.VerifiedUser,
                        contentDescription = "Claim Verification",
                        tint = ElectricLime,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "CLAIM VERIFICATION",
                            color = ElectricLime,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = "Strict offline trust evaluator enforcing non-fabrication & evidence boundary policy",
                            color = MutedText,
                            fontSize = 11.sp
                        )
                    }
                }
            }
        }

        // Factual Verification Rules Disclaimer Card
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(CyberSurface, RoundedCornerShape(8.dp))
                    .border(1.dp, CyberCyan.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Gavel,
                        contentDescription = null,
                        tint = CyberCyan,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "VEK VERIFICATION POLICY & GUARANTEES",
                        color = CyberCyan,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    )
                }

                Text(
                    text = "• Factual verification strictly requires authenticated primary evidence or an approved online verification service.\n" +
                            "• Sources and references are NEVER fabricated or hallucinated.\n" +
                            "• Unauthenticated model responses are NEVER labeled as Verified.\n" +
                            "• When adequate primary evidence is unavailable offline, the evaluation automatically returns INCONCLUSIVE.",
                    color = MutedText,
                    fontSize = 12.sp,
                    lineHeight = 17.sp
                )
            }
        }

        // Input Card
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(CyberSurface, RoundedCornerShape(8.dp))
                    .border(1.dp, MutedBorder, RoundedCornerShape(8.dp))
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "SUBMIT CLAIM FOR ASSESSMENT",
                    color = OffWhiteText,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )

                OutlinedTextField(
                    value = claimInput,
                    onValueChange = { claimInput = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(110.dp),
                    placeholder = { Text("Enter statement or claim to evaluate (e.g., 'The system achieved 99.9% uptime according to ISO-27001 audit')...") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ElectricLime,
                        unfocusedBorderColor = MutedBorder,
                        focusedTextColor = OffWhiteText,
                        unfocusedTextColor = OffWhiteText
                    )
                )

                Button(
                    onClick = {
                        val text = claimInput.trim()
                        if (text.isNotEmpty()) {
                            evaluatedClaim = evaluateClaimOffline(text)
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = claimInput.trim().isNotEmpty(),
                    colors = ButtonDefaults.buttonColors(containerColor = ElectricLime, contentColor = CyberBlack),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(imageVector = Icons.Default.FactCheck, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "EVALUATE CLAIM", fontWeight = FontWeight.Black)
                }
            }
        }

        // Evaluation Result
        if (evaluatedClaim != null) {
            val eval = evaluatedClaim!!
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(CyberSurfaceHeader, RoundedCornerShape(8.dp))
                        .border(1.dp, CyberCyan, RoundedCornerShape(8.dp))
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "CLAIM ASSESSMENT RESULT",
                            color = OffWhiteText,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Box(
                            modifier = Modifier
                                .background(eval.statusColor.copy(alpha = 0.2f), RoundedCornerShape(4.dp))
                                .border(1.dp, eval.statusColor, RoundedCornerShape(4.dp))
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = eval.statusLabel,
                                color = eval.statusColor,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Black
                            )
                        }
                    }

                    Divider(color = MutedBorder)

                    Text(
                        text = "Claim Evaluated:",
                        color = MutedText,
                        fontSize = 11.sp
                    )

                    Text(
                        text = "\"${eval.claimText}\"",
                        color = OffWhiteText,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold
                    )

                    Text(
                        text = "Reasoning & Policy Findings:",
                        color = MutedText,
                        fontSize = 11.sp,
                        modifier = Modifier.padding(top = 4.dp)
                    )

                    Text(
                        text = eval.explanation,
                        color = CyberCyan,
                        fontSize = 12.sp,
                        lineHeight = 17.sp
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "CANONICAL AUDIT RECORD",
                        color = MutedText,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(CyberBlack, RoundedCornerShape(4.dp))
                            .border(1.dp, MutedBorder, RoundedCornerShape(4.dp))
                            .padding(8.dp)
                    ) {
                        Text(
                            text = eval.canonicalJson,
                            color = MutedText,
                            fontSize = 10.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }

                    OutlinedButton(
                        onClick = {
                            clipboardManager.setText(AnnotatedString(eval.canonicalJson))
                            copiedFeedback = true
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = ElectricLime),
                        border = androidx.compose.foundation.BorderStroke(1.dp, ElectricLime)
                    ) {
                        Icon(imageVector = Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = if (copiedFeedback) "Copied Record!" else "Copy Audit JSON", fontSize = 12.sp)
                    }
                }
            }
        }
    }
}

private data class ClaimEvaluationResult(
    val claimText: String,
    val statusLabel: String,
    val statusColor: androidx.compose.ui.graphics.Color,
    val explanation: String,
    val canonicalJson: String
)

private fun evaluateClaimOffline(claim: String): ClaimEvaluationResult {
    // Offline verification policy: Without authenticated online evidence or cryptographically signed local proof, claim is INCONCLUSIVE
    val json = JSONObject()
        .put("claim", claim)
        .put("evidence", "UNAUTHENTICATED")
        .put("status", "INCONCLUSIVE")
        .put("policy", "VEK-OFFLINE-POLICY-001")
        .toString()
    return ClaimEvaluationResult(
        claimText = claim,
        statusLabel = "INCONCLUSIVE",
        statusColor = ElectricLime,
        explanation = "In accordance with VEK Non-Fabrication Rules: Factual verification requires authenticated evidence or an approved online verification service. Unauthenticated claims cannot be marked as Verified offline. Adequate primary evidence is unavailable.",
        canonicalJson = json
    )
}
