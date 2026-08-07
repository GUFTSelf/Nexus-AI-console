package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.VerificationCase
import com.example.ui.NexusViewModel
import com.example.ui.components.StatusBadge
import com.example.ui.theme.*

@Composable
fun EnterpriseWorkspaceScreen(
    viewModel: NexusViewModel,
    allCases: List<VerificationCase>,
    selectedDomain: String,
    onSelectDomain: (String) -> Unit,
    onSelectCase: (VerificationCase) -> Unit,
    modifier: Modifier = Modifier
) {
    val domains = listOf("Healthcare", "Financial Services", "Defense", "Government", "Legal & Compliance", "AI Provider", "Critical Infrastructure", "Quantum Computing")

    val domainCases = remember(allCases, selectedDomain) {
        allCases.filter { it.domain.equals(selectedDomain, ignoreCase = true) || (selectedDomain == "Healthcare" && it.domain == "Healthcare") || (selectedDomain == "Defense" && it.domain == "Defense") }
    }

    val teamRoster = remember(selectedDomain) {
        when (selectedDomain) {
            "Healthcare" -> listOf("Dr. Sarah Jenkins (Cardiology Lead)", "Dr. Marcus Vance (Clinical Regulatory)", "Elena Rostova (Compliance Officer)")
            "Defense" -> listOf("Vance Mitchell (Defense Procurement)", "Col. David Vance (Cyber CMMC Auditor)", "Sarah Miller (Contract Security)")
            "Financial Services" -> listOf("Alexander Wright (Chief Risk Officer)", "Priya Sharma (AML / Fraud Lead)", "Michael Chen (Compliance Officer)")
            else -> listOf("Lead Compliance Auditor", "Senior Operations Officer", "Domain Regulatory Lead")
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(CyberBackground)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Banner
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(CyberSurfaceHeader, RoundedCornerShape(8.dp))
                    .border(1.dp, CyberCyan, RoundedCornerShape(8.dp))
                    .padding(16.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.Business, contentDescription = null, tint = CyberCyan)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "ENTERPRISE INDUSTRY WORKSPACES", color = CyberCyan, fontSize = 13.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = "Multi-tenant governance workspaces with role-based review queues.", color = MutedText, fontSize = 12.sp)
            }
        }

        // Domain Switcher Scrollable Row
        item {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(domains) { dom ->
                    FilterChip(
                        selected = selectedDomain == dom,
                        onClick = { onSelectDomain(dom) },
                        label = { Text(dom, fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = CyberCyan,
                            selectedLabelColor = CyberBlack,
                            containerColor = CyberSurface,
                            labelColor = OffWhiteText
                        )
                    )
                }
            }
        }

        // Mandatory Human Review Disclaimer Notice
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(StatusConditional.copy(alpha = 0.15f), RoundedCornerShape(6.dp))
                    .border(1.dp, StatusConditional, RoundedCornerShape(6.dp))
                    .padding(12.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.Warning, contentDescription = null, tint = StatusConditional, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "MANDATORY COMPLIANCE NOTICE: Healthcare, legal, financial, government, and defense outputs strictly require accredited human review before final operational execution.",
                        color = OffWhiteText,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }

        // Team Members Roster
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(CyberSurface, RoundedCornerShape(8.dp))
                    .border(1.dp, MutedBorder, RoundedCornerShape(8.dp))
                    .padding(14.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.Group, contentDescription = null, tint = ElectricLime, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = "$selectedDomain Workspace Team Roster", color = ElectricLime, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.height(8.dp))
                teamRoster.forEach { member ->
                    Text(text = "• $member", color = OffWhiteText, fontSize = 12.sp, modifier = Modifier.padding(vertical = 2.dp))
                }
            }
        }

        // Cases Assigned To Workspace
        item {
            Text(
                text = "WORKSPACE CASE REVIEW QUEUE (${domainCases.size})",
                color = ElectricLime,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
        }

        if (domainCases.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "No cases assigned to $selectedDomain workspace.", color = MutedText)
                }
            }
        } else {
            items(domainCases) { caseItem ->
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(CyberSurface, RoundedCornerShape(8.dp))
                        .border(1.dp, MutedBorder, RoundedCornerShape(8.dp))
                        .clickable { onSelectCase(caseItem) }
                        .padding(14.dp)
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
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(text = "Reviewer: ${caseItem.reviewerStatus ?: "Pending"}", color = CyberCyan, fontSize = 11.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = caseItem.title, color = OffWhiteText, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = caseItem.summary, color = MutedText, fontSize = 12.sp, maxLines = 2)

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "Click to inspect case & submit human reviewer decision ->", color = ElectricLime, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
