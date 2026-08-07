package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.AppTier
import com.example.ui.NexusViewModel
import com.example.ui.theme.*

@Composable
fun LicensingScreen(
    viewModel: NexusViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val currentUser by viewModel.currentUserProfile.collectAsState()
    var showContactDialog by remember { mutableStateOf(false) }

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
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.MonetizationOn, contentDescription = null, tint = ElectricLime)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "NEXUS AI SUBSCRIPTION TIERS",
                            color = ElectricLime,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                    }

                    if (currentUser != null) {
                        Box(
                            modifier = Modifier
                                .background(CyberSurfaceVariant, RoundedCornerShape(12.dp))
                                .border(1.dp, ElectricLime, RoundedCornerShape(12.dp))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "ACTIVE: ${currentUser?.tier?.displayName?.uppercase()}",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Black,
                                color = ElectricLime
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Select your enrollment plan: Pro, Premium, or Ultra. Authenticate with Google to activate instant access.",
                    color = MutedText,
                    fontSize = 12.sp
                )
            }
        }

        // Tier 1: Pro
        item {
            PricingTierCard(
                tier = AppTier.PRO,
                isCurrentTier = currentUser?.tier == AppTier.PRO,
                onEnrollClick = {
                    viewModel.updateUserTier(AppTier.PRO)
                    Toast.makeText(context, "Upgraded to Nexus Pro Tier ($19/mo)!", Toast.LENGTH_LONG).show()
                },
                onGoogleAuthClick = {
                    viewModel.toggleAuthDialog(true)
                }
            )
        }

        // Tier 2: Premium
        item {
            PricingTierCard(
                tier = AppTier.PREMIUM,
                isCurrentTier = currentUser?.tier == AppTier.PREMIUM,
                isHighlighted = true,
                onEnrollClick = {
                    viewModel.updateUserTier(AppTier.PREMIUM)
                    Toast.makeText(context, "Upgraded to Nexus Premium Tier ($49/mo)!", Toast.LENGTH_LONG).show()
                },
                onGoogleAuthClick = {
                    viewModel.toggleAuthDialog(true)
                }
            )
        }

        // Tier 3: Ultra
        item {
            PricingTierCard(
                tier = AppTier.ULTRA,
                isCurrentTier = currentUser?.tier == AppTier.ULTRA,
                isUltra = true,
                onEnrollClick = {
                    viewModel.updateUserTier(AppTier.ULTRA)
                    Toast.makeText(context, "Activated Nexus Ultra Tier ($199/mo) with Quantum Zero-Knowledge Proofs!", Toast.LENGTH_LONG).show()
                },
                onGoogleAuthClick = {
                    viewModel.toggleAuthDialog(true)
                }
            )
        }

        // VEK Enterprise Custom Infrastructure
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(CyberSurface, RoundedCornerShape(8.dp))
                    .border(1.dp, MutedBorder, RoundedCornerShape(8.dp))
                    .padding(16.dp)
            ) {
                Text(text = "VEK Infrastructure Enterprise License", color = OffWhiteText, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = "On-premises, air-gapped defense deployments and custom C++ / Rust deterministic kernel bindings.", color = MutedText, fontSize = 12.sp)

                Spacer(modifier = Modifier.height(10.dp))
                Button(
                    onClick = { showContactDialog = true },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = CyberCyan, contentColor = CyberBlack),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(text = "Request Enterprise Defense License", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
            }
        }
    }

    if (showContactDialog) {
        AlertDialog(
            onDismissRequest = { showContactDialog = false },
            title = { Text("Request VEK Enterprise Partnership", color = ElectricLime, fontWeight = FontWeight.Bold) },
            text = { Text("Submit partnership inquiry to GUTS Deterministic Technology, LLC. Our defense & aerospace team will contact you within 24 hours.", color = OffWhiteText, fontSize = 13.sp) },
            confirmButton = {
                Button(
                    onClick = {
                        showContactDialog = false
                        Toast.makeText(context, "Partnership request submitted successfully.", Toast.LENGTH_LONG).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ElectricLime, contentColor = CyberBlack)
                ) {
                    Text("Submit Request", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showContactDialog = false }) {
                    Text("Cancel", color = MutedText)
                }
            },
            containerColor = CyberSurface,
            shape = RoundedCornerShape(8.dp)
        )
    }
}

@Composable
private fun PricingTierCard(
    tier: AppTier,
    isCurrentTier: Boolean,
    isHighlighted: Boolean = false,
    isUltra: Boolean = false,
    onEnrollClick: () -> Unit,
    onGoogleAuthClick: () -> Unit
) {
    val tierColor = Color(android.graphics.Color.parseColor(tier.hexColor))

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(if (isHighlighted || isUltra) CyberSurfaceHeader else CyberSurface, RoundedCornerShape(8.dp))
            .border(1.dp, if (isCurrentTier) ElectricLime else if (isUltra) tierColor else if (isHighlighted) ElectricLime else MutedBorder, RoundedCornerShape(8.dp))
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = "Nexus ${tier.displayName}", color = OffWhiteText, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                if (isUltra) {
                    Spacer(modifier = Modifier.width(6.dp))
                    Box(
                        modifier = Modifier
                            .background(tierColor.copy(alpha = 0.2f), RoundedCornerShape(4.dp))
                            .border(1.dp, tierColor, RoundedCornerShape(4.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(text = "QUANTUM PROOF", color = tierColor, fontSize = 9.sp, fontWeight = FontWeight.Black)
                    }
                }
            }

            Text(text = tier.monthlyPrice, color = tierColor, fontSize = 15.sp, fontWeight = FontWeight.Black)
        }

        Spacer(modifier = Modifier.height(4.dp))
        Text(text = tier.description, color = MutedText, fontSize = 12.sp)

        Spacer(modifier = Modifier.height(10.dp))
        tier.features.forEach { ft ->
            Row(modifier = Modifier.padding(vertical = 2.dp), verticalAlignment = Alignment.CenterVertically) {
                Text(text = "✓ ", color = tierColor, fontWeight = FontWeight.Bold)
                Text(text = ft, color = OffWhiteText, fontSize = 12.sp)
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (isCurrentTier) {
                Button(
                    onClick = {},
                    enabled = false,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(disabledContainerColor = ElectricLime.copy(alpha = 0.2f), disabledContentColor = ElectricLime),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(text = "ACTIVE TIER", fontWeight = FontWeight.Bold)
                }
            } else {
                Button(
                    onClick = onEnrollClick,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = tierColor, contentColor = CyberBlack),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(text = "Enroll ${tier.displayName} Tier", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                }
            }

            IconButton(
                onClick = onGoogleAuthClick,
                modifier = Modifier
                    .background(CyberSurfaceVariant, RoundedCornerShape(6.dp))
                    .border(1.dp, MutedBorder, RoundedCornerShape(6.dp))
            ) {
                Icon(imageVector = Icons.Default.AccountCircle, contentDescription = "Firebase Google Sign In", tint = ElectricLime)
            }
        }
    }
}
