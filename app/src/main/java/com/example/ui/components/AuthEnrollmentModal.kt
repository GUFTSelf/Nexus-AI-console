package com.example.ui.components

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.ui.AppTier
import com.example.ui.NexusViewModel
import com.example.ui.UserProfile
import com.example.ui.theme.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthEnrollmentModal(
    viewModel: NexusViewModel,
    onDismiss: () -> Unit
) {
    val currentUser by viewModel.currentUserProfile.collectAsState()
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    var selectedTier by remember { mutableStateOf(currentUser?.tier ?: AppTier.PRO) }
    var selectedAuthTab by remember { mutableStateOf(0) } // 0: Google Sign-In / Enrollment, 1: Email / Password, 2: Choose Tier
    var isAuthenticating by remember { mutableStateOf(false) }
    var showDeleteConfirmation by remember { mutableStateOf(false) }

    var emailInput by remember { mutableStateOf(currentUser?.email ?: "") }
    var passwordInput by remember { mutableStateOf("") }
    var nameInput by remember { mutableStateOf(currentUser?.displayName ?: "") }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .wrapContentHeight()
                .padding(16.dp)
                .clip(RoundedCornerShape(16.dp))
                .border(1.dp, ElectricLime, RoundedCornerShape(16.dp)),
            color = CyberSurface
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                // Header Bar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(ElectricLime.copy(alpha = 0.2f))
                                .border(1.dp, ElectricLime, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(imageVector = Icons.Default.Shield, contentDescription = null, tint = ElectricLime, modifier = Modifier.size(20.dp))
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "NEXUS AUTH & ENROLLMENT",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Black,
                                color = OffWhiteText,
                                letterSpacing = 0.5.sp
                            )
                            Text(
                                text = "Firebase Google Sign-In & Subscription Tiers",
                                fontSize = 10.sp,
                                color = MutedText
                            )
                        }
                    }

                    IconButton(onClick = onDismiss) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = MutedText)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Active Account Summary Card (if logged in)
                if (currentUser != null && currentUser?.isEnrolled == true) {
                    AccountStatusCard(
                        user = currentUser!!,
                        onUpgradeTier = { selectedAuthTab = 2 },
                        onSignOut = {
                            viewModel.signOut()
                            Toast.makeText(context, "Signed out from Nexus AI", Toast.LENGTH_SHORT).show()
                        },
                        onDeleteAccount = {
                            showDeleteConfirmation = true
                        }
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }

                // Auth Mode Switcher Tabs
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(CyberSurfaceHeader, RoundedCornerShape(8.dp))
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    listOf("Google Auth", "Email Enroll", "App Tiers").forEachIndexed { index, label ->
                        val isSelected = selectedAuthTab == index
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (isSelected) ElectricLime else Color.Transparent)
                                .clickable { selectedAuthTab = index }
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = label,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) CyberBlack else MutedText
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Tab Content
                when (selectedAuthTab) {
                    0 -> GoogleSignInTab(
                        isAuthenticating = isAuthenticating,
                        selectedTier = selectedTier,
                        onSelectTier = { selectedTier = it },
                        onGoogleSignInClick = {
                            scope.launch {
                                isAuthenticating = true
                                val result = viewModel.signInWithGoogle()
                                isAuthenticating = false
                                result.onSuccess { user ->
                                    viewModel.updateUserTier(selectedTier)
                                    Toast.makeText(context, "Welcome, ${user.displayName}! Enrolled in ${selectedTier.displayName} tier.", Toast.LENGTH_LONG).show()
                                    onDismiss()
                                }.onFailure { err ->
                                    Toast.makeText(context, "Signed in via Firebase Auth: ${err.message}", Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                    )

                    1 -> EmailEnrollmentTab(
                        name = nameInput,
                        email = emailInput,
                        password = passwordInput,
                        selectedTier = selectedTier,
                        isAuthenticating = isAuthenticating,
                        onNameChange = { nameInput = it },
                        onEmailChange = { emailInput = it },
                        onPasswordChange = { passwordInput = it },
                        onSelectTier = { selectedTier = it },
                        onSubmit = {
                            scope.launch {
                                isAuthenticating = true
                                viewModel.signInWithEmail(emailInput, passwordInput, nameInput, selectedTier)
                                isAuthenticating = false
                                Toast.makeText(context, "Account enrolled successfully under ${selectedTier.displayName} tier!", Toast.LENGTH_LONG).show()
                                onDismiss()
                            }
                        }
                    )

                    2 -> TierSelectionTab(
                        selectedTier = selectedTier,
                        onSelectTier = { tier ->
                            selectedTier = tier
                            viewModel.updateUserTier(tier)
                            Toast.makeText(context, "Upgraded active subscription to ${tier.displayName} Tier (${tier.monthlyPrice})!", Toast.LENGTH_SHORT).show()
                        }
                    )
                }
            }
        }
    }

    if (showDeleteConfirmation) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirmation = false },
            title = { Text("Delete Nexus account?") },
            text = {
                Text("This deletes the signed-in account and clears verification cases and audit records stored on this device. This action cannot be undone.")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteConfirmation = false
                        scope.launch {
                            val result = viewModel.deleteAccountAndLocalData()
                            Toast.makeText(
                                context,
                                if (result.isSuccess) "Account and local records deleted" else "Sign in again, then retry account deletion",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
                ) {
                    Text("Delete", color = StatusUnsupported)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirmation = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

// --------------------------------------------------
// 1. Account Status Summary Card
// --------------------------------------------------
@Composable
private fun AccountStatusCard(
    user: UserProfile,
    onUpgradeTier: () -> Unit,
    onSignOut: () -> Unit,
    onDeleteAccount: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(CyberSurfaceHeader, RoundedCornerShape(10.dp))
            .border(1.dp, ElectricLime.copy(alpha = 0.5f), RoundedCornerShape(10.dp))
            .padding(14.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(CyberCyan.copy(alpha = 0.2f))
                        .border(1.dp, CyberCyan, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = user.displayName.take(1).uppercase(),
                        fontWeight = FontWeight.Bold,
                        color = CyberCyan,
                        fontSize = 18.sp
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(text = user.displayName, fontWeight = FontWeight.Bold, color = OffWhiteText, fontSize = 13.sp)
                    Text(text = user.email, color = MutedText, fontSize = 11.sp)
                }
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(android.graphics.Color.parseColor(user.tier.hexColor)).copy(alpha = 0.2f))
                    .border(1.dp, Color(android.graphics.Color.parseColor(user.tier.hexColor)), RoundedCornerShape(12.dp))
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Text(
                    text = "${user.tier.displayName.uppercase()} MEMBER",
                    color = Color(android.graphics.Color.parseColor(user.tier.hexColor)),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Black
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "UID: ${user.uid}", fontSize = 10.sp, color = MutedText)

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                TextButton(onClick = onUpgradeTier) {
                    Text(text = "Change Tier", fontSize = 11.sp, color = ElectricLime, fontWeight = FontWeight.Bold)
                }
                TextButton(onClick = onSignOut) {
                    Text(text = "Sign Out", fontSize = 11.sp, color = StatusUnsupported)
                }
                TextButton(onClick = onDeleteAccount) {
                    Text(text = "Delete Account", fontSize = 11.sp, color = StatusUnsupported)
                }
            }
        }
    }
}

// --------------------------------------------------
// 2. Google Sign In & Enrollment View
// --------------------------------------------------
@Composable
private fun GoogleSignInTab(
    isAuthenticating: Boolean,
    selectedTier: AppTier,
    onSelectTier: (AppTier) -> Unit,
    onGoogleSignInClick: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Text(
            text = "FAST FIREBASE GOOGLE SIGN-IN",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = CyberCyan
        )

        Text(
            text = "Authenticate securely with your Google Workspace or Personal account to unlock deterministic VEK trace auditing.",
            fontSize = 11.sp,
            color = MutedText,
            textAlign = TextAlign.Center
        )

        // Selected Tier Selector Pills
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(text = "SELECT ENROLLMENT TIER:", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = MutedText)
            Spacer(modifier = Modifier.height(6.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf(AppTier.PRO, AppTier.PREMIUM, AppTier.ULTRA).forEach { tier ->
                    val isSelected = selectedTier == tier
                    val tierColor = Color(android.graphics.Color.parseColor(tier.hexColor))
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isSelected) tierColor.copy(alpha = 0.2f) else CyberSurfaceHeader)
                            .border(1.dp, if (isSelected) tierColor else MutedBorder, RoundedCornerShape(8.dp))
                            .clickable { onSelectTier(tier) }
                            .padding(vertical = 10.dp, horizontal = 4.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = tier.displayName, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = if (isSelected) tierColor else OffWhiteText)
                            Text(text = tier.monthlyPrice, fontSize = 10.sp, color = MutedText)
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Google Sign-In Button
        Button(
            onClick = onGoogleSignInClick,
            enabled = !isAuthenticating,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = OffWhiteText,
                contentColor = CyberBlack
            ),
            shape = RoundedCornerShape(8.dp)
        ) {
            if (isAuthenticating) {
                CircularProgressIndicator(modifier = Modifier.size(18.dp), color = CyberBlack, strokeWidth = 2.dp)
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "Connecting to Firebase Google Auth...", fontSize = 12.sp, fontWeight = FontWeight.Bold)
            } else {
                Icon(imageVector = Icons.Default.AccountCircle, contentDescription = null, tint = Color(0xFF4285F4))
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "Sign in / Enroll with Google",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = CyberBlack
                )
            }
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(imageVector = Icons.Default.VerifiedUser, contentDescription = null, tint = ElectricLime, modifier = Modifier.size(14.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text(text = "Firebase OAuth 2.0 Encrypted Token Exchange", fontSize = 10.sp, color = MutedText)
        }
    }
}

// --------------------------------------------------
// 3. Email & Password Tab
// --------------------------------------------------
@Composable
private fun EmailEnrollmentTab(
    name: String,
    email: String,
    password: String,
    selectedTier: AppTier,
    isAuthenticating: Boolean,
    onNameChange: (String) -> Unit,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onSelectTier: (AppTier) -> Unit,
    onSubmit: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        OutlinedTextField(
            value = name,
            onValueChange = onNameChange,
            label = { Text("Full Name", fontSize = 10.sp, color = MutedText) },
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = CyberBackground,
                unfocusedContainerColor = CyberBackground,
                focusedBorderColor = ElectricLime,
                unfocusedBorderColor = MutedBorder,
                focusedTextColor = OffWhiteText,
                unfocusedTextColor = OffWhiteText
            ),
            singleLine = true
        )

        OutlinedTextField(
            value = email,
            onValueChange = onEmailChange,
            label = { Text("Email Address", fontSize = 10.sp, color = MutedText) },
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = CyberBackground,
                unfocusedContainerColor = CyberBackground,
                focusedBorderColor = ElectricLime,
                unfocusedBorderColor = MutedBorder,
                focusedTextColor = OffWhiteText,
                unfocusedTextColor = OffWhiteText
            ),
            singleLine = true
        )

        OutlinedTextField(
            value = password,
            onValueChange = onPasswordChange,
            label = { Text("Password", fontSize = 10.sp, color = MutedText) },
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = CyberBackground,
                unfocusedContainerColor = CyberBackground,
                focusedBorderColor = ElectricLime,
                unfocusedBorderColor = MutedBorder,
                focusedTextColor = OffWhiteText,
                unfocusedTextColor = OffWhiteText
            ),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(4.dp))

        Button(
            onClick = onSubmit,
            enabled = email.trim().isNotEmpty() && !isAuthenticating,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(containerColor = ElectricLime, contentColor = CyberBlack)
        ) {
            Text(text = "ENROLL & ACTIVATE ${selectedTier.displayName.uppercase()} TIER", fontWeight = FontWeight.Bold, fontSize = 12.sp)
        }
    }
}

// --------------------------------------------------
// 4. App Tier Selection Tab
// --------------------------------------------------
@Composable
private fun TierSelectionTab(
    selectedTier: AppTier,
    onSelectTier: (AppTier) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text(
            text = "NEXUS AI SUBSCRIPTION TIERS",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = ElectricLime
        )

        listOf(AppTier.PRO, AppTier.PREMIUM, AppTier.ULTRA).forEach { tier ->
            val isSelected = selectedTier == tier
            val tierColor = Color(android.graphics.Color.parseColor(tier.hexColor))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (isSelected) CyberSurfaceHeader else CyberSurface)
                    .border(1.dp, if (isSelected) tierColor else MutedBorder, RoundedCornerShape(8.dp))
                    .clickable { onSelectTier(tier) }
                    .padding(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .clip(CircleShape)
                                .background(tierColor)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = tier.displayName, fontWeight = FontWeight.Bold, color = OffWhiteText, fontSize = 14.sp)
                        if (tier == AppTier.ULTRA) {
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(text = "⚡ QUANTUM", fontSize = 9.sp, color = tierColor, fontWeight = FontWeight.Black)
                        }
                    }

                    Text(text = tier.monthlyPrice, fontWeight = FontWeight.Bold, color = tierColor, fontSize = 13.sp)
                }

                Spacer(modifier = Modifier.height(4.dp))
                Text(text = tier.description, fontSize = 10.sp, color = MutedText)

                Spacer(modifier = Modifier.height(6.dp))
                tier.features.take(3).forEach { feature ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "✓ ", color = tierColor, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        Text(text = feature, fontSize = 10.sp, color = OffWhiteText)
                    }
                }
            }
        }
    }
}
