package com.example.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.components.AuthEnrollmentModal
import com.example.ui.components.VekHeaderBar
import com.example.ui.screens.*
import com.example.ui.theme.*
import kotlinx.coroutines.launch

@Composable
fun NexusMainScreen(
    viewModel: NexusViewModel = viewModel()
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    val currentDestination by viewModel.currentDestination.collectAsState()
    val isEnterpriseMode by viewModel.isEnterpriseMode.collectAsState()
    val currentUserProfile by viewModel.currentUserProfile.collectAsState()
    val showAuthDialog by viewModel.showAuthDialog.collectAsState()

    val inputState by viewModel.inputState.collectAsState()
    val processState by viewModel.processState.collectAsState()
    val selectedCase by viewModel.selectedCase.collectAsState()
    val allCases by viewModel.allCases.collectAsState()
    val allPolicies by viewModel.allPolicies.collectAsState()
    val allAuditLogs by viewModel.allAuditLogs.collectAsState()
    val selectedWorkspaceDomain by viewModel.selectedWorkspaceDomain.collectAsState()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                drawerContainerColor = CyberSurface,
                drawerContentColor = OffWhiteText,
                modifier = Modifier.width(300.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxHeight()
                        .padding(16.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    // Header in drawer
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(bottom = 12.dp, top = 8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Shield,
                            contentDescription = null,
                            tint = ElectricLime,
                            modifier = Modifier.size(28.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "NEXUS AI",
                                fontWeight = FontWeight.Black,
                                fontSize = 18.sp,
                                color = OffWhiteText
                            )
                            Text(
                                text = "VEK Control Suite",
                                fontSize = 11.sp,
                                color = MutedText
                            )
                        }
                    }

                    // User Profile & Subscription Card
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp)
                            .background(CyberSurfaceHeader, RoundedCornerShape(8.dp))
                            .border(1.dp, ElectricLime.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
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
                                        .size(28.dp)
                                        .clip(CircleShape)
                                        .background(ElectricLime),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = currentUserProfile?.displayName?.take(1)?.uppercase() ?: "G",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Black,
                                        color = CyberBlack
                                    )
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(
                                        text = currentUserProfile?.displayName ?: "Nexus Sentinel",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = OffWhiteText
                                    )
                                    Text(
                                        text = currentUserProfile?.email ?: "user@nexusai.io",
                                        fontSize = 9.sp,
                                        color = MutedText
                                    )
                                }
                            }

                            Box(
                                modifier = Modifier
                                    .background(ElectricLime.copy(alpha = 0.2f), RoundedCornerShape(4.dp))
                                    .border(1.dp, ElectricLime, RoundedCornerShape(4.dp))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = currentUserProfile?.tier?.displayName?.uppercase() ?: "PRO",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Black,
                                    color = ElectricLime
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Button(
                            onClick = {
                                viewModel.toggleAuthDialog(true)
                                scope.launch { drawerState.close() }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = ElectricLime, contentColor = CyberBlack),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Icon(imageVector = Icons.Default.AccountCircle, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(text = "Google Sign In & Tiers", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    HorizontalDivider(color = MutedBorder, modifier = Modifier.padding(vertical = 8.dp))

                    Text(
                        text = "CORE OPERATIONS",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = ElectricLime,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )

                    DrawerItem(
                        title = AppDestination.LANDING.title,
                        icon = Icons.Default.Home,
                        isSelected = currentDestination == AppDestination.LANDING,
                        onClick = {
                            viewModel.navigateTo(AppDestination.LANDING)
                            scope.launch { drawerState.close() }
                        }
                    )

                    DrawerItem(
                        title = AppDestination.CONSOLE.title,
                        icon = Icons.Default.Terminal,
                        isSelected = currentDestination == AppDestination.CONSOLE,
                        onClick = {
                            viewModel.navigateTo(AppDestination.CONSOLE)
                            scope.launch { drawerState.close() }
                        }
                    )

                    DrawerItem(
                        title = AppDestination.CONSUMER_DASHBOARD.title,
                        icon = Icons.Default.Lock,
                        isSelected = currentDestination == AppDestination.CONSUMER_DASHBOARD,
                        onClick = {
                            viewModel.navigateTo(AppDestination.CONSUMER_DASHBOARD)
                            scope.launch { drawerState.close() }
                        }
                    )

                    DrawerItem(
                        title = AppDestination.ENTERPRISE_WORKSPACE.title,
                        icon = Icons.Default.Business,
                        isSelected = currentDestination == AppDestination.ENTERPRISE_WORKSPACE,
                        onClick = {
                            viewModel.navigateTo(AppDestination.ENTERPRISE_WORKSPACE)
                            scope.launch { drawerState.close() }
                        }
                    )

                    HorizontalDivider(color = MutedBorder, modifier = Modifier.padding(vertical = 8.dp))

                    Text(
                        text = "GOVERNANCE & AUDIT",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = CyberCyan,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )

                    DrawerItem(
                        title = AppDestination.POLICY_MANAGER.title,
                        icon = Icons.Default.Gavel,
                        isSelected = currentDestination == AppDestination.POLICY_MANAGER,
                        onClick = {
                            viewModel.navigateTo(AppDestination.POLICY_MANAGER)
                            scope.launch { drawerState.close() }
                        }
                    )

                    DrawerItem(
                        title = AppDestination.EVIDENCE_LIBRARY.title,
                        icon = Icons.Default.LibraryBooks,
                        isSelected = currentDestination == AppDestination.EVIDENCE_LIBRARY,
                        onClick = {
                            viewModel.navigateTo(AppDestination.EVIDENCE_LIBRARY)
                            scope.launch { drawerState.close() }
                        }
                    )

                    DrawerItem(
                        title = AppDestination.AUDIT_LOG.title,
                        icon = Icons.Default.History,
                        isSelected = currentDestination == AppDestination.AUDIT_LOG,
                        onClick = {
                            viewModel.navigateTo(AppDestination.AUDIT_LOG)
                            scope.launch { drawerState.close() }
                        }
                    )

                    HorizontalDivider(color = MutedBorder, modifier = Modifier.padding(vertical = 8.dp))

                    Text(
                        text = "ADVANCED ENGINES",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = OffWhiteText,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )

                    DrawerItem(
                        title = AppDestination.QVEK_QUANTUM.title,
                        icon = Icons.Default.Memory,
                        isSelected = currentDestination == AppDestination.QVEK_QUANTUM,
                        onClick = {
                            viewModel.navigateTo(AppDestination.QVEK_QUANTUM)
                            scope.launch { drawerState.close() }
                        }
                    )

                    DrawerItem(
                        title = AppDestination.LICENSING.title,
                        icon = Icons.Default.VerifiedUser,
                        isSelected = currentDestination == AppDestination.LICENSING,
                        onClick = {
                            viewModel.navigateTo(AppDestination.LICENSING)
                            scope.launch { drawerState.close() }
                        }
                    )
                }
            }
        }
    ) {
        Scaffold(
            topBar = {
                VekHeaderBar(
                    isEnterpriseMode = isEnterpriseMode,
                    onToggleEnterpriseMode = { viewModel.toggleEnterpriseMode(it) },
                    onOpenDrawer = { scope.launch { drawerState.open() } },
                    currentUser = currentUserProfile,
                    onOpenAuthDialog = { viewModel.toggleAuthDialog(true) }
                )
            },
            containerColor = CyberBlack
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .background(CyberBlack)
            ) {
                when (currentDestination) {
                    AppDestination.LANDING -> LandingScreen(
                        onNavigateToConsole = { viewModel.navigateTo(AppDestination.CONSOLE) },
                        onNavigateToEnterprise = { viewModel.navigateTo(AppDestination.ENTERPRISE_WORKSPACE) },
                        onNavigateToQvek = { viewModel.navigateTo(AppDestination.QVEK_QUANTUM) },
                        onNavigateToLicensing = { viewModel.navigateTo(AppDestination.LICENSING) }
                    )
                    AppDestination.CONSOLE -> ConsoleScreen(
                        viewModel = viewModel
                    )
                    AppDestination.RESULTS -> ResultsScreen(
                        viewModel = viewModel,
                        caseItem = selectedCase,
                        onNavigateBackToConsole = { viewModel.navigateTo(AppDestination.CONSOLE) }
                    )
                    AppDestination.CONSUMER_DASHBOARD -> ConsumerDashboardScreen(
                        viewModel = viewModel,
                        allCases = allCases,
                        onSelectCase = { viewModel.selectCaseForDetail(it) }
                    )
                    AppDestination.ENTERPRISE_WORKSPACE -> EnterpriseWorkspaceScreen(
                        viewModel = viewModel,
                        allCases = allCases,
                        selectedDomain = selectedWorkspaceDomain,
                        onSelectDomain = { viewModel.setWorkspaceDomain(it) },
                        onSelectCase = { viewModel.selectCaseForDetail(it) }
                    )
                    AppDestination.POLICY_MANAGER -> PolicyManagerScreen(
                        viewModel = viewModel,
                        policies = allPolicies
                    )
                    AppDestination.EVIDENCE_LIBRARY -> EvidenceLibraryScreen()
                    AppDestination.AUDIT_LOG -> AuditLogScreen(
                        auditLogs = allAuditLogs
                    )
                    AppDestination.QVEK_QUANTUM -> QvekQuantumScreen()
                    AppDestination.LICENSING -> LicensingScreen(
                        viewModel = viewModel
                    )
                    AppDestination.ABOUT -> LandingScreen(
                        onNavigateToConsole = { viewModel.navigateTo(AppDestination.CONSOLE) },
                        onNavigateToEnterprise = { viewModel.navigateTo(AppDestination.ENTERPRISE_WORKSPACE) },
                        onNavigateToQvek = { viewModel.navigateTo(AppDestination.QVEK_QUANTUM) },
                        onNavigateToLicensing = { viewModel.navigateTo(AppDestination.LICENSING) }
                    )
                }

                if (showAuthDialog) {
                    AuthEnrollmentModal(
                        viewModel = viewModel,
                        onDismiss = { viewModel.toggleAuthDialog(false) }
                    )
                }
            }
        }
    }
}

@Composable
private fun DrawerItem(
    title: String,
    icon: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp)
            .clip(RoundedCornerShape(8.dp))
            .clickable { onClick() },
        color = if (isSelected) ElectricLime.copy(alpha = 0.15f) else CyberSurface,
        border = if (isSelected) androidx.compose.foundation.BorderStroke(1.dp, ElectricLime) else null
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (isSelected) ElectricLime else MutedText,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = title,
                fontSize = 13.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                color = if (isSelected) ElectricLime else OffWhiteText
            )
        }
    }
}
