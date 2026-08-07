package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.UserProfile
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VekHeaderBar(
    isEnterpriseMode: Boolean,
    onToggleEnterpriseMode: (Boolean) -> Unit,
    onOpenDrawer: () -> Unit,
    currentUser: UserProfile? = null,
    onOpenAuthDialog: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    TopAppBar(
        modifier = modifier.border(0.dp, Color.Transparent),
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = CyberBlack,
            titleContentColor = OffWhiteText
        ),
        navigationIcon = {
            IconButton(onClick = onOpenDrawer) {
                Icon(
                    imageVector = Icons.Default.Menu,
                    contentDescription = "Open Navigation Menu",
                    tint = OffWhiteText
                )
            }
        },
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Shield,
                    contentDescription = null,
                    tint = ElectricLime,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "NEXUS AI",
                            fontWeight = FontWeight.Black,
                            fontSize = 15.sp,
                            letterSpacing = 0.5.sp,
                            color = OffWhiteText
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Box(
                            modifier = Modifier
                                .background(ElectricLime.copy(alpha = 0.2f), RoundedCornerShape(4.dp))
                                .border(1.dp, ElectricLime, RoundedCornerShape(4.dp))
                                .padding(horizontal = 4.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "VEK KERNEL",
                                color = ElectricLime,
                                fontSize = 8.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                    Text(
                        text = "The Trust Layer for AI",
                        color = MutedText,
                        fontSize = 10.sp
                    )
                }
            }
        },
        actions = {
            // Mode Toggle Pill
            Row(
                modifier = Modifier
                    .padding(end = 6.dp)
                    .background(CyberSurface, RoundedCornerShape(20.dp))
                    .border(1.dp, MutedBorder, RoundedCornerShape(20.dp))
                    .padding(2.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Consumer Tab
                Box(
                    modifier = Modifier
                        .background(
                            if (!isEnterpriseMode) ElectricLime else Color.Transparent,
                            RoundedCornerShape(18.dp)
                        )
                        .clickable { onToggleEnterpriseMode(false) }
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Consumer",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (!isEnterpriseMode) CyberBlack else MutedText
                    )
                }

                // Enterprise Tab
                Box(
                    modifier = Modifier
                        .background(
                            if (isEnterpriseMode) CyberCyan else Color.Transparent,
                            RoundedCornerShape(18.dp)
                        )
                        .clickable { onToggleEnterpriseMode(true) }
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Enterprise",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isEnterpriseMode) CyberBlack else MutedText
                    )
                }
            }

            // User Tier Badge & Google Profile Avatar
            Box(
                modifier = Modifier
                    .padding(end = 8.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .background(CyberSurfaceHeader)
                    .border(1.dp, ElectricLime, RoundedCornerShape(18.dp))
                    .clickable { onOpenAuthDialog() }
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(18.dp)
                            .clip(CircleShape)
                            .background(ElectricLime),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = currentUser?.displayName?.take(1)?.uppercase() ?: "G",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Black,
                            color = CyberBlack
                        )
                    }

                    Spacer(modifier = Modifier.width(4.dp))

                    Text(
                        text = currentUser?.tier?.displayName?.uppercase() ?: "PRO",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Black,
                        color = ElectricLime
                    )
                }
            }
        }
    )
}
