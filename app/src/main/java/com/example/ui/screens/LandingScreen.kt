package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.AppDestination
import com.example.ui.theme.*

@Composable
fun LandingScreen(
    onNavigateToConsole: () -> Unit,
    onNavigateToEnterprise: () -> Unit,
    onNavigateToQvek: () -> Unit,
    onNavigateToLicensing: () -> Unit,
    onNavigateToDeterministic: () -> Unit = {},
    onNavigateToReplay: () -> Unit = {},
    onNavigateToClaims: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(CyberBackground)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // Hero Section
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(CyberSurfaceHeader, RoundedCornerShape(12.dp))
                    .border(1.dp, ElectricLime.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .background(ElectricLime.copy(alpha = 0.15f), RoundedCornerShape(20.dp))
                        .border(1.dp, ElectricLime, RoundedCornerShape(20.dp))
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "POWERED BY VEK — VERIFIABLE EXECUTION KERNEL",
                        color = ElectricLime,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Nexus AI\nPowered by VEK",
                    color = OffWhiteText,
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Black,
                    textAlign = TextAlign.Center,
                    lineHeight = 32.sp
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Offline-first deterministic execution engine, byte-for-byte replay verification, and claim assessment pipeline.",
                    color = MutedText,
                    fontSize = 13.sp,
                    textAlign = TextAlign.Center,
                    lineHeight = 18.sp
                )
            }
        }

        // Three Core Modes Selection Grid
        item {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = "SELECT VERIFICATION MODE",
                    color = ElectricLime,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )

                // Mode 1: Deterministic Execution
                Card(
                    onClick = onNavigateToDeterministic,
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = CyberSurface),
                    border = androidx.compose.foundation.BorderStroke(1.dp, ElectricLime.copy(alpha = 0.6f)),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(imageVector = Icons.Default.Calculate, contentDescription = null, tint = ElectricLime, modifier = Modifier.size(28.dp))
                        Spacer(modifier = Modifier.width(16.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = "Deterministic Execution", color = OffWhiteText, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                            Text(text = "Sequential arithmetic transitions, canonical JSON & SHA-256 commitment.", color = MutedText, fontSize = 11.sp)
                        }
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, tint = ElectricLime)
                    }
                }

                // Mode 2: Replay Comparison
                Card(
                    onClick = onNavigateToReplay,
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = CyberSurface),
                    border = androidx.compose.foundation.BorderStroke(1.dp, CyberCyan.copy(alpha = 0.6f)),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(imageVector = Icons.Default.Repeat, contentDescription = null, tint = CyberCyan, modifier = Modifier.size(28.dp))
                        Spacer(modifier = Modifier.width(16.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = "Replay Comparison", color = OffWhiteText, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                            Text(text = "Run 1 to 10 replays to compare canonical records and detect divergence.", color = MutedText, fontSize = 11.sp)
                        }
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, tint = CyberCyan)
                    }
                }

                // Mode 3: Claim Verification
                Card(
                    onClick = onNavigateToClaims,
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = CyberSurface),
                    border = androidx.compose.foundation.BorderStroke(1.dp, MutedBorder),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(imageVector = Icons.Default.VerifiedUser, contentDescription = null, tint = OffWhiteText, modifier = Modifier.size(28.dp))
                        Spacer(modifier = Modifier.width(16.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = "Claim Verification", color = OffWhiteText, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                            Text(text = "Assess claims against non-fabrication policy & authenticated evidence rules.", color = MutedText, fontSize = 11.sp)
                        }
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, tint = OffWhiteText)
                    }
                }
            }
        }

        // The AI Trust Problem Section
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(CyberSurface, RoundedCornerShape(8.dp))
                    .border(1.dp, MutedBorder, RoundedCornerShape(8.dp))
                    .padding(16.dp)
            ) {
                Text(
                    text = "THE AI TRUST CRISIS",
                    color = ElectricLime,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Why Traditional AI Confidence Scores Fail",
                    color = OffWhiteText,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Generative models produce fluent text with zero intrinsic regard for factual truth. Conventional systems mask hallucination behind a single confidence score. VEK isolates primary proof, detects contradictions, and enforces deterministic verification rules.",
                    color = MutedText,
                    fontSize = 12.sp,
                    lineHeight = 17.sp
                )
            }
        }

        // 10-Step Sequence Visual Flow
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(CyberSurface, RoundedCornerShape(8.dp))
                    .border(1.dp, MutedBorder, RoundedCornerShape(8.dp))
                    .padding(16.dp)
            ) {
                Text(
                    text = "THE VEK VERIFICATION SEQUENCE",
                    color = CyberCyan,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(12.dp))

                SequenceStepItem(step = "1", title = "Ingest & Normalize", desc = "Parses user text or document uploads.")
                SequenceStepItem(step = "2", title = "Decompose Claims", desc = "Splits compound text into testable atomic claims.")
                SequenceStepItem(step = "3", title = "Classify Domain", desc = "Categorizes as Factual, Medical, Financial, Legal, or Security.")
                SequenceStepItem(step = "4", title = "Evidence Analysis", desc = "Distinguishes primary registers from secondary unverified sources.")
                SequenceStepItem(step = "5", title = "Policy Enforcement", desc = "Applies strict domain rules & constraint filters.")
                SequenceStepItem(step = "6", title = "Status Assessment", desc = "Returns Verified, Conditionally Verified, or High-Risk Escalated.")
                SequenceStepItem(step = "7", title = "Multi-Factor Scoring", desc = "Calculates 6 separate trust metrics (no single black-box score).")
                SequenceStepItem(step = "8", title = "VEK Trace Generation", desc = "Generates inspectable SHA-256 reproducible trace record.")
                SequenceStepItem(step = "9", title = "Human Review Routing", desc = "Escalates high-risk cases to accredited human experts.")
                SequenceStepItem(step = "10", title = "Exportable Report", desc = "Generates print/JSON verification audit reports.")
            }
        }

        // Industry Pathways Grid
        item {
            Column {
                Text(
                    text = "TAILORED INDUSTRY WORKSPACES",
                    color = ElectricLime,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    IndustryCard(icon = Icons.Default.LocalHospital, title = "Healthcare", desc = "Clinical review & therapeutic claims", modifier = Modifier.weight(1f))
                    IndustryCard(icon = Icons.Default.AccountBalance, title = "Financial Services", desc = "Audit & anti-phishing defense", modifier = Modifier.weight(1f))
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    IndustryCard(icon = Icons.Default.Security, title = "Defense & Gov", desc = "CMMC & supply chain clearance", modifier = Modifier.weight(1f))
                    IndustryCard(icon = Icons.Default.Gavel, title = "Legal & Compliance", desc = "Statutory rule enforcement", modifier = Modifier.weight(1f))
                }
            }
        }

        // QVEK Quantum Teaser Card
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(CyberSurfaceHeader, RoundedCornerShape(8.dp))
                    .border(1.dp, CyberCyan, RoundedCornerShape(8.dp))
                    .padding(16.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.Memory, contentDescription = null, tint = CyberCyan)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "QVEK — QUANTUM READINESS", color = CyberCyan, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Proposed VEK software trust layer for hybrid classical-quantum workflows with error-aware execution records and IBM Quantum concept integration.",
                    color = MutedText,
                    fontSize = 12.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                TextButton(onClick = onNavigateToQvek) {
                    Text(text = "Explore QVEK Research ->", color = ElectricLime, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
            }
        }

        // Footer & Copyright
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = "NEXUS AI", color = OffWhiteText, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Text(text = "Powered by VEK — Verifiable Execution Kernel", color = ElectricLime, fontSize = 11.sp)
                Text(text = "GUTS Deterministic Technology, LLC", color = MutedText, fontSize = 11.sp, modifier = Modifier.padding(top = 4.dp))
                Text(text = "Invented by Thoeun Thien", color = CyberCyan, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(top = 2.dp))
            }
        }
    }
}

@Composable
private fun SequenceStepItem(step: String, title: String, desc: String) {
    Row(
        modifier = Modifier.padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(24.dp)
                .background(ElectricLime.copy(alpha = 0.2f), RoundedCornerShape(12.dp))
                .border(1.dp, ElectricLime, RoundedCornerShape(12.dp)),
            contentAlignment = Alignment.Center
        ) {
            Text(text = step, color = ElectricLime, fontSize = 10.sp, fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.width(10.dp))
        Column {
            Text(text = title, color = OffWhiteText, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            Text(text = desc, color = MutedText, fontSize = 11.sp)
        }
    }
}

@Composable
private fun IndustryCard(icon: ImageVector, title: String, desc: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .background(CyberSurface, RoundedCornerShape(6.dp))
            .border(1.dp, MutedBorder, RoundedCornerShape(6.dp))
            .padding(12.dp)
    ) {
        Icon(imageVector = icon, contentDescription = null, tint = ElectricLime, modifier = Modifier.size(20.dp))
        Spacer(modifier = Modifier.height(6.dp))
        Text(text = title, color = OffWhiteText, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        Text(text = desc, color = MutedText, fontSize = 10.sp)
    }
}
