package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.*
import com.example.ui.NexusViewModel
import com.example.ui.components.StatusBadge
import com.example.ui.components.TrustScoreCard
import com.example.ui.components.VekTraceInspector
import com.example.ui.theme.*

@Composable
fun ResultsScreen(
    viewModel: NexusViewModel,
    caseItem: VerificationCase?,
    onNavigateBackToConsole: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    if (caseItem == null) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(CyberBackground),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = "No active verification case selected.", color = MutedText)
                Spacer(modifier = Modifier.height(12.dp))
                Button(onClick = onNavigateBackToConsole, colors = ButtonDefaults.buttonColors(containerColor = ElectricLime, contentColor = CyberBlack)) {
                    Text("Go to Verification Console")
                }
            }
        }
        return
    }

    val claims = remember(caseItem.claimsJson) { viewModel.parseClaims(caseItem.claimsJson) }
    val scores = remember(caseItem.scoresJson) { viewModel.parseScores(caseItem.scoresJson) }
    val rules = remember(caseItem.rulesJson) { viewModel.parseRules(caseItem.rulesJson) }
    val warnings = remember(caseItem.warningsJson) { viewModel.parseStringList(caseItem.warningsJson) }
    val actions = remember(caseItem.actionsJson) { viewModel.parseStringList(caseItem.actionsJson) }

    var reviewerNotesInput by remember(caseItem.reviewerNotes) { mutableStateOf(caseItem.reviewerNotes ?: "") }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(CyberBackground)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Top Header Actions Bar
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedButton(
                    onClick = onNavigateBackToConsole,
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = OffWhiteText),
                    border = androidx.compose.foundation.BorderStroke(1.dp, MutedBorder)
                ) {
                    Icon(imageVector = Icons.Default.ArrowBack, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("New Search")
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                        onClick = {
                            Toast.makeText(context, "Verification Report exported to JSON", Toast.LENGTH_SHORT).show()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = CyberCyan, contentColor = CyberBlack)
                    ) {
                        Icon(imageVector = Icons.Default.Download, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Export Report", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }
            }
        }

        // Summary Card & Status
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(CyberSurfaceHeader, RoundedCornerShape(8.dp))
                    .border(1.dp, CyberCyan.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    StatusBadge(status = caseItem.status)
                    Box(
                        modifier = Modifier
                            .background(CyberSurfaceVariant, RoundedCornerShape(4.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "RISK: ${caseItem.riskLevel.uppercase()}",
                            color = when (caseItem.riskLevel.lowercase()) {
                                "critical" -> StatusHighRisk
                                "high" -> StatusHighRisk
                                "medium" -> StatusConditional
                                else -> StatusVerified
                            },
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = caseItem.title,
                    color = OffWhiteText,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = caseItem.summary,
                    color = MutedText,
                    fontSize = 13.sp,
                    lineHeight = 18.sp
                )
            }
        }

        // Multi-Factor Trust Score
        item {
            TrustScoreCard(scores = scores)
        }

        // Decomposed Claims & Evidence Analysis Section
        item {
            Column {
                Text(
                    text = "DECOMPOSED CLAIMS & EVIDENCE ANALYSIS",
                    color = ElectricLime,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                claims.forEach { claim ->
                    ClaimCardItem(claim = claim)
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }

        // Warnings & Recommended Actions
        item {
            if (warnings.isNotEmpty() || actions.isNotEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(CyberSurface, RoundedCornerShape(8.dp))
                        .border(1.dp, StatusConditional.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                        .padding(16.dp)
                ) {
                    Text(
                        text = "WARNING SIGNALS & RECOMMENDED ACTIONS",
                        color = StatusConditional,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    warnings.forEach { warning ->
                        Row(modifier = Modifier.padding(vertical = 2.dp), verticalAlignment = Alignment.Top) {
                            Text(text = "• ", color = StatusConditional, fontWeight = FontWeight.Bold)
                            Text(text = warning, color = OffWhiteText, fontSize = 12.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    actions.forEach { action ->
                        Row(modifier = Modifier.padding(vertical = 2.dp), verticalAlignment = Alignment.Top) {
                            Text(text = "✓ ", color = StatusVerified, fontWeight = FontWeight.Bold)
                            Text(text = action, color = MutedText, fontSize = 12.sp)
                        }
                    }
                }
            }
        }

        // Human Review & Escalation Panel
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(CyberSurface, RoundedCornerShape(8.dp))
                    .border(1.dp, MutedBorder, RoundedCornerShape(8.dp))
                    .padding(16.dp)
            ) {
                Text(
                    text = "HUMAN REVIEW & WORKFLOW GOVERNANCE",
                    color = ElectricLime,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Reviewer Status: ${caseItem.reviewerStatus ?: "Pending"}",
                    color = OffWhiteText,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = reviewerNotesInput,
                    onValueChange = { reviewerNotesInput = it },
                    label = { Text("Reviewer Notes / Escalation Justification", color = MutedText) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = CyberBlack,
                        unfocusedContainerColor = CyberBlack,
                        focusedBorderColor = ElectricLime,
                        unfocusedBorderColor = MutedBorder,
                        focusedTextColor = OffWhiteText,
                        unfocusedTextColor = OffWhiteText
                    )
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                        onClick = {
                            viewModel.updateHumanReviewStatus(caseItem.caseId, "Approved", reviewerNotesInput)
                            Toast.makeText(context, "Case decision approved", Toast.LENGTH_SHORT).show()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = StatusVerified, contentColor = CyberBlack),
                        shape = RoundedCornerShape(4.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Approve", fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = {
                            viewModel.updateHumanReviewStatus(caseItem.caseId, "Escalated", reviewerNotesInput)
                            Toast.makeText(context, "Case escalated to Senior Officer", Toast.LENGTH_SHORT).show()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = StatusConditional, contentColor = CyberBlack),
                        shape = RoundedCornerShape(4.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Escalate", fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = {
                            viewModel.updateHumanReviewStatus(caseItem.caseId, "Rejected", reviewerNotesInput)
                            Toast.makeText(context, "Case rejected", Toast.LENGTH_SHORT).show()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = StatusUnsupported, contentColor = OffWhiteText),
                        shape = RoundedCornerShape(4.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Reject", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // VEK Deterministic Trace Inspector
        item {
            VekTraceInspector(caseItem = caseItem)
        }
    }
}

@Composable
private fun ClaimCardItem(claim: DecomposedClaim) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(CyberSurface, RoundedCornerShape(6.dp))
            .border(1.dp, MutedBorder, RoundedCornerShape(6.dp))
            .padding(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .background(CyberSurfaceVariant, RoundedCornerShape(4.dp))
                    .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
                Text(text = claim.classification.displayName.uppercase(), color = CyberCyan, fontSize = 10.sp, fontWeight = FontWeight.Bold)
            }

            Box(
                modifier = Modifier
                    .background(
                        when (claim.assessment) {
                            ClaimAssessment.SUPPORTED -> StatusVerified.copy(alpha = 0.2f)
                            ClaimAssessment.PARTIALLY_SUPPORTED -> StatusConditional.copy(alpha = 0.2f)
                            ClaimAssessment.CONTRADICTED -> StatusUnsupported.copy(alpha = 0.2f)
                            ClaimAssessment.UNVERIFIED -> StatusInconclusive.copy(alpha = 0.2f)
                        },
                        RoundedCornerShape(4.dp)
                    )
                    .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
                Text(
                    text = claim.assessment.displayName.uppercase(),
                    color = when (claim.assessment) {
                        ClaimAssessment.SUPPORTED -> StatusVerified
                        ClaimAssessment.PARTIALLY_SUPPORTED -> StatusConditional
                        ClaimAssessment.CONTRADICTED -> StatusUnsupported
                        ClaimAssessment.UNVERIFIED -> StatusInconclusive
                    },
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(6.dp))
        Text(text = claim.claim, color = OffWhiteText, fontSize = 13.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = claim.reasoningSummary, color = MutedText, fontSize = 12.sp)

        if (claim.supportingEvidence.isNotEmpty()) {
            Spacer(modifier = Modifier.height(6.dp))
            Text(text = "Supporting Evidence:", color = StatusVerified, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            claim.supportingEvidence.forEach { ev ->
                Text(text = "• ${ev.title} (${ev.publisher}) [${ev.sourceType}]", color = OffWhiteText, fontSize = 11.sp)
            }
        }

        if (claim.conflictingEvidence.isNotEmpty()) {
            Spacer(modifier = Modifier.height(6.dp))
            Text(text = "Contradictions Detected:", color = StatusUnsupported, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            claim.conflictingEvidence.forEach { ev ->
                Text(text = "• ${ev.title} (${ev.publisher})", color = StatusUnsupported, fontSize = 11.sp)
            }
        }

        if (claim.missingEvidence.isNotEmpty()) {
            Spacer(modifier = Modifier.height(6.dp))
            Text(text = "Missing Required Proof:", color = StatusConditional, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            claim.missingEvidence.forEach { miss ->
                Text(text = "• $miss", color = MutedText, fontSize = 11.sp)
            }
        }
    }
}
