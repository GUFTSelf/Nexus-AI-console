package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.VerificationPolicy
import com.example.ui.NexusViewModel
import com.example.ui.theme.*

@Composable
fun PolicyManagerScreen(
    viewModel: NexusViewModel,
    policies: List<VerificationPolicy>,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(CyberBackground)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(CyberSurfaceHeader, RoundedCornerShape(8.dp))
                    .border(1.dp, ElectricLime.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                    .padding(16.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.Gavel, contentDescription = null, tint = ElectricLime)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "VEK POLICY & RULES MANAGER", color = ElectricLime, fontSize = 13.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = "Configure active domain constraints, mandatory primary source rules, and escalation limits.", color = MutedText, fontSize = 12.sp)
            }
        }

        items(policies, key = { it.id }) { policy ->
            Column(
                modifier = Modifier
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
                        Text(text = policy.name, color = OffWhiteText, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        Text(text = "Domain: ${policy.domain}", color = CyberCyan, fontSize = 11.sp)
                    }
                    Switch(
                        checked = policy.enabled,
                        onCheckedChange = { viewModel.togglePolicy(policy.id, it) },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = CyberBlack,
                            checkedTrackColor = ElectricLime,
                            uncheckedThumbColor = MutedText,
                            uncheckedTrackColor = CyberSurfaceVariant
                        )
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))
                Divider(color = MutedBorder)
                Spacer(modifier = Modifier.height(8.dp))

                PolicyRuleCheck(label = "Require Primary Sources", enabled = policy.requirePrimarySources)
                PolicyRuleCheck(label = "Require Recent Sources", enabled = policy.requireRecentSources)
                PolicyRuleCheck(label = "Escalate Conflicting Evidence", enabled = policy.escalateConflicts)
                PolicyRuleCheck(label = "Mandatory Human Approval", enabled = policy.requireHumanApproval)
            }
        }
    }
}

@Composable
private fun PolicyRuleCheck(label: String, enabled: Boolean) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, color = MutedText, fontSize = 12.sp)
        Text(
            text = if (enabled) "ENABLED" else "DISABLED",
            color = if (enabled) StatusVerified else StatusUnsupported,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold
        )
    }
}
