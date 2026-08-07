package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.ConsoleChatMessage
import com.example.ui.ConsoleMode
import com.example.ui.NexusViewModel
import com.example.ui.theme.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConsoleScreen(
    viewModel: NexusViewModel,
    modifier: Modifier = Modifier
) {
    val consoleMode by viewModel.consoleMode.collectAsState()
    val hyperparameters by viewModel.hyperparameters.collectAsState()
    val chatMessages by viewModel.chatMessages.collectAsState()
    val isChatGenerating by viewModel.isChatGenerating.collectAsState()
    val structuredSchema by viewModel.structuredSchema.collectAsState()
    val structuredResult by viewModel.structuredResult.collectAsState()
    val inputState by viewModel.inputState.collectAsState()
    val processState by viewModel.processState.collectAsState()
    val isEnterpriseMode by viewModel.isEnterpriseMode.collectAsState()

    var showInspector by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(CyberBackground)
            .padding(16.dp)
    ) {
        // Top Header & Mode Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "NEXUS AI CONSOLE",
                    color = ElectricLime,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "Powered by Gemini & Verifiable Execution Kernel (VEK)",
                    color = MutedText,
                    fontSize = 11.sp
                )
            }

            IconButton(
                onClick = { showInspector = !showInspector },
                modifier = Modifier
                    .background(if (showInspector) ElectricLime.copy(alpha = 0.2f) else CyberSurface, RoundedCornerShape(8.dp))
                    .border(1.dp, if (showInspector) ElectricLime else MutedBorder, RoundedCornerShape(8.dp))
            ) {
                Icon(
                    imageVector = Icons.Default.Tune,
                    contentDescription = "Hyperparameters",
                    tint = if (showInspector) ElectricLime else OffWhiteText
                )
            }
        }

        // Mode Switcher Tabs
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            ConsoleMode.values().forEach { mode ->
                val isSelected = consoleMode == mode
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isSelected) CyberSurfaceHeader else CyberSurface)
                        .border(
                            width = 1.dp,
                            color = if (isSelected) ElectricLime else MutedBorder,
                            shape = RoundedCornerShape(8.dp)
                        )
                        .clickable { viewModel.setConsoleMode(mode) }
                        .padding(vertical = 10.dp, horizontal = 4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = mode.displayName,
                            fontSize = 11.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) ElectricLime else MutedText,
                            maxLines = 1
                        )
                    }
                }
            }
        }

        // Expandable Hyperparameter Inspector Panel
        AnimatedVisibility(visible = showInspector) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp)
                    .background(CyberSurfaceHeader, RoundedCornerShape(8.dp))
                    .border(1.dp, CyberCyan.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "HYPERPARAMETERS & MODEL CONFIG",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = CyberCyan
                    )
                    Text(
                        text = "System Active",
                        fontSize = 10.sp,
                        color = ElectricLime
                    )
                }

                // Model Selection
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf("gemini-3.5-flash", "gemini-3.1-pro-preview", "gemini-3.1-flash-lite-preview").forEach { modelName ->
                        val selected = hyperparameters.selectedModel == modelName
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (selected) CyberCyan.copy(alpha = 0.2f) else CyberSurface)
                                .border(1.dp, if (selected) CyberCyan else MutedBorder, RoundedCornerShape(6.dp))
                                .clickable { viewModel.updateHyperparameters(model = modelName) }
                                .padding(vertical = 6.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = when (modelName) {
                                    "gemini-3.5-flash" -> "Gemini 3.5 Flash"
                                    "gemini-3.1-pro-preview" -> "Gemini 3.1 Pro"
                                    else -> "Flash Lite"
                                },
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (selected) OffWhiteText else MutedText
                            )
                        }
                    }
                }

                // Temperature Slider
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "Temperature", fontSize = 11.sp, color = MutedText)
                        Text(text = "%.2f".format(hyperparameters.temperature), fontSize = 11.sp, color = ElectricLime, fontWeight = FontWeight.Bold)
                    }
                    Slider(
                        value = hyperparameters.temperature,
                        onValueChange = { viewModel.updateHyperparameters(temp = it) },
                        valueRange = 0f..1f,
                        colors = SliderDefaults.colors(
                            thumbColor = ElectricLime,
                            activeTrackColor = ElectricLime,
                            inactiveTrackColor = MutedBorder
                        )
                    )
                }

                // System Instruction Input
                OutlinedTextField(
                    value = hyperparameters.systemInstruction,
                    onValueChange = { viewModel.updateHyperparameters(sysInstruction = it) },
                    label = { Text("System Instruction Prompt", fontSize = 10.sp, color = MutedText) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = CyberSurface,
                        unfocusedContainerColor = CyberSurface,
                        focusedBorderColor = CyberCyan,
                        unfocusedBorderColor = MutedBorder,
                        focusedTextColor = OffWhiteText,
                        unfocusedTextColor = OffWhiteText
                    ),
                    maxLines = 2,
                    textStyle = LocalTextStyle.current.copy(fontSize = 11.sp)
                )
            }
        }

        // Main Console Area Based on Mode
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            when (consoleMode) {
                ConsoleMode.VEK_KERNEL -> VekKernelView(
                    inputState = inputState,
                    processState = processState,
                    isEnterpriseMode = isEnterpriseMode,
                    onRawInputChanged = { viewModel.updateRawInput(it) },
                    onDomainChanged = { viewModel.updateDomain(it) },
                    onContentTypeChanged = { viewModel.updateContentType(it) },
                    onSimulateFileUpload = { viewModel.simulateFileUpload(it) },
                    onLoadDemoCase = { viewModel.loadDemoCase(it) },
                    onClearInput = { viewModel.clearInput() },
                    onRunVerification = { viewModel.runVerification() }
                )
                ConsoleMode.GENERAL_CHAT -> GeneralChatView(
                    messages = chatMessages,
                    isGenerating = isChatGenerating,
                    onSendMessage = { viewModel.sendChatMessage(it) },
                    onVerifyMessageWithVek = { viewModel.verifyChatMessageWithVek(it) }
                )
                ConsoleMode.STRUCTURED_JSON -> StructuredJsonView(
                    schema = structuredSchema,
                    result = structuredResult,
                    isGenerating = isChatGenerating,
                    onSchemaChanged = { viewModel.updateStructuredSchema(it) },
                    onRunStructured = { viewModel.runStructuredGeneration(it) }
                )
                ConsoleMode.CODE_ANALYSIS -> CodeAnalysisView(
                    viewModel = viewModel
                )
            }
        }
    }
}

// --------------------------------------------------
// 1. VEK Verifiable Kernel View
// --------------------------------------------------
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun VekKernelView(
    inputState: com.example.ui.ConsoleInputState,
    processState: com.example.ui.VerificationProcessState,
    isEnterpriseMode: Boolean,
    onRawInputChanged: (String) -> Unit,
    onDomainChanged: (String) -> Unit,
    onContentTypeChanged: (String) -> Unit,
    onSimulateFileUpload: (String) -> Unit,
    onLoadDemoCase: (String) -> Unit,
    onClearInput: () -> Unit,
    onRunVerification: () -> Unit
) {
    var domainExpanded by remember { mutableStateOf(false) }
    var contentTypeExpanded by remember { mutableStateOf(false) }

    val domains = listOf("General Consumer", "Healthcare", "Financial Services", "Defense", "Government", "Legal & Compliance", "AI Provider", "Critical Infrastructure", "Quantum Computing")
    val contentTypes = listOf("Text / Article", "SMS / Phishing Message", "Medical Article Excerpt", "Defense Procurement Compliance Document", "Financial Report", "AI Output Claim")

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Mode Subheader
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(CyberSurfaceHeader, RoundedCornerShape(8.dp))
                    .border(1.dp, ElectricLime.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                    .padding(14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.Shield, contentDescription = null, tint = ElectricLime, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "VERIFIABLE EXECUTION KERNEL",
                            color = ElectricLime,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Box(
                        modifier = Modifier
                            .background(CyberSurfaceVariant, RoundedCornerShape(4.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = if (isEnterpriseMode) "ENTERPRISE POLICY ENFORCER" else "CONSUMER GUARD",
                            color = if (isEnterpriseMode) CyberCyan else ElectricLime,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Executes 10-step claim decomposition, rule enforcement, and cryptographic trace hash generation.",
                    color = MutedText,
                    fontSize = 11.sp
                )
            }
        }

        // Demo Presets Selection
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(CyberSurface, RoundedCornerShape(8.dp))
                    .border(1.dp, MutedBorder, RoundedCornerShape(8.dp))
                    .padding(12.dp)
            ) {
                Text(
                    text = "LOAD VERIFICATION BENCHMARKS",
                    color = MutedText,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf(
                        "NX-2026-DEMO-01" to "Medical Claim",
                        "NX-2026-DEMO-02" to "Phishing SMS",
                        "NX-2026-DEMO-03" to "Procurement Log"
                    ).forEach { (id, label) ->
                        val isSelected = inputState.selectedDemoId == id
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (isSelected) ElectricLime.copy(alpha = 0.15f) else CyberSurfaceVariant)
                                .border(1.dp, if (isSelected) ElectricLime else MutedBorder, RoundedCornerShape(6.dp))
                                .clickable { onLoadDemoCase(id) }
                                .padding(vertical = 8.dp, horizontal = 4.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = label,
                                fontSize = 10.sp,
                                color = if (isSelected) ElectricLime else OffWhiteText,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1
                            )
                        }
                    }
                }
            }
        }

        // Domain & Content Type Config
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Domain Dropdown
                Box(modifier = Modifier.weight(1f)) {
                    ExposedDropdownMenuBox(
                        expanded = domainExpanded,
                        onExpandedChange = { domainExpanded = !domainExpanded }
                    ) {
                        OutlinedTextField(
                            value = inputState.domain,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Target Domain", fontSize = 10.sp, color = MutedText) },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = domainExpanded) },
                            modifier = Modifier.menuAnchor().fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = CyberSurface,
                                unfocusedContainerColor = CyberSurface,
                                focusedBorderColor = CyberCyan,
                                unfocusedBorderColor = MutedBorder,
                                focusedTextColor = OffWhiteText,
                                unfocusedTextColor = OffWhiteText
                            ),
                            textStyle = LocalTextStyle.current.copy(fontSize = 11.sp)
                        )
                        ExposedDropdownMenu(
                            expanded = domainExpanded,
                            onDismissRequest = { domainExpanded = false },
                            modifier = Modifier.background(CyberSurface)
                        ) {
                            domains.forEach { domain ->
                                DropdownMenuItem(
                                    text = { Text(domain, color = OffWhiteText, fontSize = 11.sp) },
                                    onClick = {
                                        onDomainChanged(domain)
                                        domainExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }

                // Content Type Dropdown
                Box(modifier = Modifier.weight(1f)) {
                    ExposedDropdownMenuBox(
                        expanded = contentTypeExpanded,
                        onExpandedChange = { contentTypeExpanded = !contentTypeExpanded }
                    ) {
                        OutlinedTextField(
                            value = inputState.contentType,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Content Format", fontSize = 10.sp, color = MutedText) },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = contentTypeExpanded) },
                            modifier = Modifier.menuAnchor().fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = CyberSurface,
                                unfocusedContainerColor = CyberSurface,
                                focusedBorderColor = CyberCyan,
                                unfocusedBorderColor = MutedBorder,
                                focusedTextColor = OffWhiteText,
                                unfocusedTextColor = OffWhiteText
                            ),
                            textStyle = LocalTextStyle.current.copy(fontSize = 11.sp)
                        )
                        ExposedDropdownMenu(
                            expanded = contentTypeExpanded,
                            onDismissRequest = { contentTypeExpanded = false },
                            modifier = Modifier.background(CyberSurface)
                        ) {
                            contentTypes.forEach { type ->
                                DropdownMenuItem(
                                    text = { Text(type, color = OffWhiteText, fontSize = 11.sp) },
                                    onClick = {
                                        onContentTypeChanged(type)
                                        contentTypeExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }

        // Input Field Card
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(CyberSurface, RoundedCornerShape(8.dp))
                    .border(1.dp, MutedBorder, RoundedCornerShape(8.dp))
                    .padding(14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "RAW CLAIM OR CONTENT INPUT",
                        color = ElectricLime,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Row {
                        IconButton(onClick = { onSimulateFileUpload("compliance_audit_2026.pdf") }) {
                            Icon(imageVector = Icons.Default.UploadFile, contentDescription = "Upload File", tint = CyberCyan)
                        }
                        IconButton(onClick = onClearInput) {
                            Icon(imageVector = Icons.Default.Clear, contentDescription = "Clear", tint = MutedText)
                        }
                    }
                }

                if (inputState.uploadedFileName != null) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp)
                            .background(CyberSurfaceVariant, RoundedCornerShape(4.dp))
                            .border(1.dp, CyberCyan.copy(alpha = 0.5f), RoundedCornerShape(4.dp))
                            .padding(8.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.AttachFile, contentDescription = null, tint = CyberCyan, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(text = "Uploaded: ${inputState.uploadedFileName}", color = CyberCyan, fontSize = 11.sp)
                        }
                    }
                }

                OutlinedTextField(
                    value = inputState.rawInput,
                    onValueChange = onRawInputChanged,
                    placeholder = { Text("Paste medical claim, phishing SMS, AI response, or audit text here...", color = MutedText, fontSize = 12.sp) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = CyberBackground,
                        unfocusedContainerColor = CyberBackground,
                        focusedBorderColor = ElectricLime,
                        unfocusedBorderColor = MutedBorder,
                        focusedTextColor = OffWhiteText,
                        unfocusedTextColor = OffWhiteText
                    )
                )

                Spacer(modifier = Modifier.height(14.dp))

                Button(
                    onClick = onRunVerification,
                    enabled = inputState.rawInput.trim().isNotEmpty() && !processState.isVerifying,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ElectricLime,
                        contentColor = CyberBlack,
                        disabledContainerColor = MutedBorder,
                        disabledContentColor = MutedText
                    )
                ) {
                    if (processState.isVerifying) {
                        CircularProgressIndicator(modifier = Modifier.size(18.dp), color = CyberBlack, strokeWidth = 2.dp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = processState.stepMessage, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    } else {
                        Icon(imageVector = Icons.Default.Gavel, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = "EXECUTE VEK VERIFICATION PIPELINE", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                }
            }
        }
    }
}

// --------------------------------------------------
// 2. General AI Chat View
// --------------------------------------------------
@Composable
private fun GeneralChatView(
    messages: List<ConsoleChatMessage>,
    isGenerating: Boolean,
    onSendMessage: (String) -> Unit,
    onVerifyMessageWithVek: (String) -> Unit
) {
    var inputText by remember { mutableStateOf("") }
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()
    val clipboardManager = LocalClipboardManager.current

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        // Messages Thread
        LazyColumn(
            state = listState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(messages, key = { it.id }) { msg ->
                val isUser = msg.sender == "USER"
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = if (isUser) Alignment.End else Alignment.Start
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.padding(bottom = 4.dp)
                    ) {
                        Icon(
                            imageVector = if (isUser) Icons.Default.Person else Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = if (isUser) CyberCyan else ElectricLime,
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = if (isUser) "You" else "Nexus AI (${msg.modelUsed})",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isUser) CyberCyan else ElectricLime
                        )
                        if (!isUser && msg.latencyMs > 0) {
                            Text(
                                text = "• ${msg.latencyMs}ms",
                                fontSize = 10.sp,
                                color = MutedText
                            )
                        }
                    }

                    Box(
                        modifier = Modifier
                            .widthIn(max = 320.dp)
                            .clip(
                                RoundedCornerShape(
                                    topStart = 12.dp,
                                    topEnd = 12.dp,
                                    bottomStart = if (isUser) 12.dp else 2.dp,
                                    bottomEnd = if (isUser) 2.dp else 12.dp
                                )
                            )
                            .background(if (isUser) CyberSurfaceVariant else CyberSurfaceHeader)
                            .border(1.dp, if (isUser) CyberCyan.copy(alpha = 0.3f) else ElectricLime.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                            .padding(12.dp)
                    ) {
                        SelectionContainer {
                            Text(
                                text = msg.text,
                                fontSize = 12.sp,
                                color = OffWhiteText,
                                lineHeight = 18.sp
                            )
                        }
                    }

                    if (!isUser) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.padding(top = 4.dp)
                        ) {
                            Text(
                                text = "Copy",
                                fontSize = 10.sp,
                                color = MutedText,
                                modifier = Modifier
                                    .clickable { clipboardManager.setText(AnnotatedString(msg.text)) }
                                    .padding(horizontal = 4.dp)
                            )
                            Text(
                                text = "• Verify in VEK",
                                fontSize = 10.sp,
                                color = ElectricLime,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier
                                    .clickable { onVerifyMessageWithVek(msg.text) }
                                    .padding(horizontal = 4.dp)
                            )
                        }
                    }
                }
            }

            if (isGenerating) {
                item {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.padding(8.dp)
                    ) {
                        CircularProgressIndicator(modifier = Modifier.size(16.dp), color = ElectricLime, strokeWidth = 2.dp)
                        Text(text = "Nexus AI is generating...", fontSize = 11.sp, color = MutedText)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Quick Preset Prompt Chips
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            listOf(
                "Explain Quantum Encryption",
                "Summarize VEK Guarantees",
                "Draft Python API Client"
            ).forEach { preset ->
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(CyberSurface)
                        .border(1.dp, MutedBorder, RoundedCornerShape(16.dp))
                        .clickable { onSendMessage(preset) }
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(text = preset, fontSize = 10.sp, color = MutedText)
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Input Field Bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = inputText,
                onValueChange = { inputText = it },
                placeholder = { Text("Ask Nexus AI anything or enter prompt...", fontSize = 12.sp, color = MutedText) },
                modifier = Modifier.weight(1f),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = CyberSurface,
                    unfocusedContainerColor = CyberSurface,
                    focusedBorderColor = ElectricLime,
                    unfocusedBorderColor = MutedBorder,
                    focusedTextColor = OffWhiteText,
                    unfocusedTextColor = OffWhiteText
                ),
                maxLines = 3,
                textStyle = LocalTextStyle.current.copy(fontSize = 12.sp)
            )

            Spacer(modifier = Modifier.width(8.dp))

            IconButton(
                onClick = {
                    if (inputText.trim().isNotEmpty()) {
                        onSendMessage(inputText)
                        inputText = ""
                    }
                },
                enabled = inputText.trim().isNotEmpty() && !isGenerating,
                modifier = Modifier
                    .size(48.dp)
                    .background(if (inputText.trim().isNotEmpty()) ElectricLime else CyberSurface, CircleShape)
            ) {
                Icon(
                    imageVector = Icons.Default.Send,
                    contentDescription = "Send",
                    tint = if (inputText.trim().isNotEmpty()) CyberBlack else MutedText
                )
            }
        }
    }
}

// --------------------------------------------------
// 3. Structured Output Mode
// --------------------------------------------------
@Composable
private fun StructuredJsonView(
    schema: String,
    result: String?,
    isGenerating: Boolean,
    onSchemaChanged: (String) -> Unit,
    onRunStructured: (String) -> Unit
) {
    var prompt by remember { mutableStateOf("") }
    val clipboardManager = LocalClipboardManager.current

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(CyberSurface, RoundedCornerShape(8.dp))
                    .border(1.dp, MutedBorder, RoundedCornerShape(8.dp))
                    .padding(12.dp)
            ) {
                Text(
                    text = "1. PROMPT & INSTRUCTION",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = CyberCyan
                )
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = prompt,
                    onValueChange = { prompt = it },
                    placeholder = { Text("Describe what data to extract or format...", fontSize = 11.sp, color = MutedText) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(80.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = CyberBackground,
                        unfocusedContainerColor = CyberBackground,
                        focusedBorderColor = CyberCyan,
                        unfocusedBorderColor = MutedBorder,
                        focusedTextColor = OffWhiteText,
                        unfocusedTextColor = OffWhiteText
                    )
                )
            }
        }

        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(CyberSurface, RoundedCornerShape(8.dp))
                    .border(1.dp, MutedBorder, RoundedCornerShape(8.dp))
                    .padding(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "2. DESIRED JSON SCHEMA",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = ElectricLime
                    )
                    Text(
                        text = "Valid JSON Format",
                        fontSize = 10.sp,
                        color = MutedText
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = schema,
                    onValueChange = onSchemaChanged,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(110.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = CyberBackground,
                        unfocusedContainerColor = CyberBackground,
                        focusedBorderColor = ElectricLime,
                        unfocusedBorderColor = MutedBorder,
                        focusedTextColor = OffWhiteText,
                        unfocusedTextColor = OffWhiteText
                    ),
                    textStyle = LocalTextStyle.current.copy(fontFamily = FontFamily.Monospace, fontSize = 11.sp)
                )
            }
        }

        item {
            Button(
                onClick = { onRunStructured(prompt) },
                enabled = prompt.trim().isNotEmpty() && !isGenerating,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = CyberCyan,
                    contentColor = CyberBlack
                )
            ) {
                if (isGenerating) {
                    CircularProgressIndicator(modifier = Modifier.size(16.dp), color = CyberBlack, strokeWidth = 2.dp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "Generating Structured Output...", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                } else {
                    Icon(imageVector = Icons.Default.Code, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "GENERATE STRUCTURED JSON", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        if (result != null) {
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(CyberSurfaceHeader, RoundedCornerShape(8.dp))
                        .border(1.dp, ElectricLime, RoundedCornerShape(8.dp))
                        .padding(14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "STRUCTURED OUTPUT RESULT",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = ElectricLime
                        )
                        IconButton(onClick = { clipboardManager.setText(AnnotatedString(result)) }) {
                            Icon(imageVector = Icons.Default.ContentCopy, contentDescription = "Copy", tint = ElectricLime, modifier = Modifier.size(18.dp))
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    SelectionContainer {
                        Text(
                            text = result,
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace,
                            color = OffWhiteText,
                            lineHeight = 16.sp
                        )
                    }
                }
            }
        }
    }
}

// --------------------------------------------------
// 4. Code & Reasoning Studio
// --------------------------------------------------
@Composable
private fun CodeAnalysisView(
    viewModel: NexusViewModel
) {
    var codeSnippet by remember {
        mutableStateOf(
            """fun verifyTransaction(amount: Double, userRole: String): Boolean {
    if (amount > 10000.0 && userRole != "ADMIN") {
        return false
    }
    return true
}"""
        )
    }

    var selectedLang by remember { mutableStateOf("Kotlin") }
    val isGenerating by viewModel.isChatGenerating.collectAsState()

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(CyberSurface, RoundedCornerShape(8.dp))
                    .border(1.dp, MutedBorder, RoundedCornerShape(8.dp))
                    .padding(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "CODE SNIPPET AUDIT & SYNTHESIS",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = ElectricLime
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        listOf("Kotlin", "Python", "Rust", "SQL").forEach { lang ->
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(if (selectedLang == lang) ElectricLime.copy(alpha = 0.2f) else CyberSurfaceVariant)
                                    .border(1.dp, if (selectedLang == lang) ElectricLime else MutedBorder, RoundedCornerShape(4.dp))
                                    .clickable { selectedLang = lang }
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(text = lang, fontSize = 9.sp, color = if (selectedLang == lang) ElectricLime else MutedText)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = codeSnippet,
                    onValueChange = { codeSnippet = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(130.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = CyberBackground,
                        unfocusedContainerColor = CyberBackground,
                        focusedBorderColor = ElectricLime,
                        unfocusedBorderColor = MutedBorder,
                        focusedTextColor = OffWhiteText,
                        unfocusedTextColor = OffWhiteText
                    ),
                    textStyle = LocalTextStyle.current.copy(fontFamily = FontFamily.Monospace, fontSize = 11.sp)
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = {
                            viewModel.sendChatMessage("Analyze this $selectedLang code for security vulnerabilities, logic flaws, and optimizations:\n\n```$selectedLang\n$codeSnippet\n```")
                            viewModel.setConsoleMode(ConsoleMode.GENERAL_CHAT)
                        },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(6.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = ElectricLime, contentColor = CyberBlack)
                    ) {
                        Text(text = "SECURITY AUDIT", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = {
                            viewModel.sendChatMessage("Wrap this $selectedLang code in a zero-knowledge VEK proof assertion layer:\n\n```$selectedLang\n$codeSnippet\n```")
                            viewModel.setConsoleMode(ConsoleMode.GENERAL_CHAT)
                        },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(6.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = CyberCyan, contentColor = CyberBlack)
                    ) {
                        Text(text = "GENERATE VEK PROOF", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
