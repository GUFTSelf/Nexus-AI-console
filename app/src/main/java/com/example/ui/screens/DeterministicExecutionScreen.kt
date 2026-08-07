package com.example.ui.screens

import android.content.Context
import android.content.Intent
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.service.MathOp
import com.example.service.MathOpType
import com.example.ui.NexusViewModel
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeterministicExecutionScreen(
    viewModel: NexusViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    val result by viewModel.deterministicResult.collectAsState()

    var initialValueInput by remember { mutableStateOf("7") }
    var operations by remember {
        mutableStateOf(
            listOf(
                MathOp(MathOpType.MULTIPLY, 8.0),
                MathOp(MathOpType.SUBTRACT, 11.0),
                MathOp(MathOpType.DIVIDE, 5.0),
                MathOp(MathOpType.ADD, 9.0)
            )
        )
    }

    var selectedOpType by remember { mutableStateOf(MathOpType.ADD) }
    var operandInput by remember { mutableStateOf("10") }
    var validationError by remember { mutableStateOf<String?>(null) }
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
                        imageVector = Icons.Default.Calculate,
                        contentDescription = "Deterministic Execution",
                        tint = ElectricLime,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "DETERMINISTIC EXECUTION",
                            color = ElectricLime,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = "Pure offline mathematical pipeline with canonical JSON & SHA-256 commitment",
                            color = MutedText,
                            fontSize = 11.sp
                        )
                    }
                }
            }
        }

        // Preset Vector Action Button
        item {
            OutlinedButton(
                onClick = {
                    initialValueInput = "7"
                    operations = listOf(
                        MathOp(MathOpType.MULTIPLY, 8.0),
                        MathOp(MathOpType.SUBTRACT, 11.0),
                        MathOp(MathOpType.DIVIDE, 5.0),
                        MathOp(MathOpType.ADD, 9.0)
                    )
                    validationError = null
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = CyberCyan),
                border = androidx.compose.foundation.BorderStroke(1.dp, CyberCyan),
                shape = RoundedCornerShape(8.dp)
            ) {
                Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "Load Fixed Validation Vector (Start: 7, *8, -11, /5, +9)", fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }

        // Input Configuration Card
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
                    text = "INITIAL NUMERIC VALUE",
                    color = OffWhiteText,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp
                )

                OutlinedTextField(
                    value = initialValueInput,
                    onValueChange = {
                        initialValueInput = it
                        validationError = null
                    },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Initial Value (e.g. 7)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ElectricLime,
                        unfocusedBorderColor = MutedBorder,
                        focusedTextColor = OffWhiteText,
                        unfocusedTextColor = OffWhiteText
                    ),
                    singleLine = true
                )

                Divider(color = MutedBorder, thickness = 1.dp)

                Text(
                    text = "ADD ORDERED OPERATION",
                    color = OffWhiteText,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Op Selector
                    var expandedDropdown by remember { mutableStateOf(false) }
                    Box(modifier = Modifier.weight(1f)) {
                        OutlinedButton(
                            onClick = { expandedDropdown = true },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = ElectricLime),
                            border = androidx.compose.foundation.BorderStroke(1.dp, MutedBorder),
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text(text = selectedOpType.name, fontWeight = FontWeight.Bold)
                            Icon(imageVector = Icons.Default.ArrowDropDown, contentDescription = null)
                        }

                        DropdownMenu(
                            expanded = expandedDropdown,
                            onDismissRequest = { expandedDropdown = false }
                        ) {
                            MathOpType.values().forEach { type ->
                                DropdownMenuItem(
                                    text = { Text(type.name) },
                                    onClick = {
                                        selectedOpType = type
                                        expandedDropdown = false
                                    }
                                )
                            }
                        }
                    }

                    // Operand Value
                    OutlinedTextField(
                        value = operandInput,
                        onValueChange = { operandInput = it },
                        modifier = Modifier.weight(1f),
                        label = { Text("Operand") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = ElectricLime,
                            unfocusedBorderColor = MutedBorder,
                            focusedTextColor = OffWhiteText,
                            unfocusedTextColor = OffWhiteText
                        ),
                        singleLine = true
                    )

                    // Add Button
                    Button(
                        onClick = {
                            val opVal = operandInput.toDoubleOrNull()
                            if (opVal != null) {
                                operations = operations + MathOp(selectedOpType, opVal)
                                validationError = null
                            } else {
                                validationError = "Please enter a valid numeric operand"
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = ElectricLime, contentColor = CyberBlack),
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = "Add Operation")
                    }
                }

                if (validationError != null) {
                    Text(
                        text = validationError!!,
                        color = MaterialTheme.colorScheme.error,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // Ordered Operations List
        item {
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
                    Text(
                        text = "ORDERED PIPELINE (${operations.size} OPS)",
                        color = CyberCyan,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    )

                    if (operations.isNotEmpty()) {
                        TextButton(onClick = { operations = emptyList() }) {
                            Text(text = "Clear All", color = MaterialTheme.colorScheme.error, fontSize = 11.sp)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                if (operations.isEmpty()) {
                    Text(
                        text = "No operations added yet. Add an operation above to build pipeline.",
                        color = MutedText,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                } else {
                    operations.forEachIndexed { idx, op ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .background(CyberSurfaceHeader, RoundedCornerShape(4.dp))
                                .border(1.dp, MutedBorder, RoundedCornerShape(4.dp))
                                .padding(horizontal = 12.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Step ${idx + 1}: ${op.op.name} ${if (op.value % 1.0 == 0.0) op.value.toLong() else op.value}",
                                color = OffWhiteText,
                                fontSize = 13.sp,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold
                            )

                            IconButton(
                                onClick = {
                                    operations = operations.filterIndexed { i, _ -> i != idx }
                                },
                                modifier = Modifier.size(24.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Delete step",
                                    tint = MutedText,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = {
                        val initVal = initialValueInput.toDoubleOrNull()
                        if (initVal == null) {
                            validationError = "Initial value must be a valid number"
                        } else if (operations.isEmpty()) {
                            validationError = "Add at least one operation to execute"
                        } else {
                            validationError = null
                            viewModel.executeDeterministic(initVal, operations)
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = ElectricLime, contentColor = CyberBlack),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(imageVector = Icons.Default.PlayArrow, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "EXECUTE DETERMINISTIC PIPELINE", fontWeight = FontWeight.Black)
                }
            }
        }

        // Execution Output Section
        if (result != null) {
            val res = result!!
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(CyberSurfaceHeader, RoundedCornerShape(8.dp))
                        .border(1.dp, ElectricLime, RoundedCornerShape(8.dp))
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "EXECUTION RESULT",
                            color = ElectricLime,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 1.sp
                        )

                        Text(
                            text = "FINAL RESULT: ${if (res.finalValue % 1.0 == 0.0) res.finalValue.toLong() else res.finalValue}",
                            color = OffWhiteText,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Black,
                            fontFamily = FontFamily.Monospace
                        )
                    }

                    Divider(color = ElectricLime.copy(alpha = 0.3f))

                    Text(
                        text = "STATE TRANSITIONS",
                        color = OffWhiteText,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )

                    res.transitions.forEach { step ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(CyberSurface, RoundedCornerShape(4.dp))
                                .padding(8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Step ${step.stepIndex}: ${step.previousState} -> ${step.op.name} ${step.operand}",
                                color = MutedText,
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace
                            )
                            Text(
                                text = "= ${step.nextState}",
                                color = ElectricLime,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "CANONICAL JSON RECORD",
                        color = OffWhiteText,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(CyberBlack, RoundedCornerShape(4.dp))
                            .border(1.dp, MutedBorder, RoundedCornerShape(4.dp))
                            .padding(10.dp)
                    ) {
                        Text(
                            text = res.canonicalJson,
                            color = ElectricLime,
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "SHA-256 INTEGRITY COMMITMENT",
                        color = OffWhiteText,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(CyberBlack, RoundedCornerShape(4.dp))
                            .border(1.dp, CyberCyan, RoundedCornerShape(4.dp))
                            .padding(10.dp)
                    ) {
                        Text(
                            text = res.sha256Hash,
                            color = CyberCyan,
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    // Copy & Share Actions
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = {
                                clipboardManager.setText(AnnotatedString(res.canonicalJson))
                                copiedFeedback = true
                            },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = ElectricLime),
                            border = androidx.compose.foundation.BorderStroke(1.dp, ElectricLime)
                        ) {
                            Icon(imageVector = Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(text = if (copiedFeedback) "Copied!" else "Copy JSON", fontSize = 12.sp)
                        }

                        Button(
                            onClick = {
                                val shareText = "Nexus AI Deterministic Verification Record:\nResult: ${res.finalValue}\nJSON: ${res.canonicalJson}\nSHA-256: ${res.sha256Hash}"
                                val intent = Intent(Intent.ACTION_SEND).apply {
                                    type = "text/plain"
                                    putExtra(Intent.EXTRA_TEXT, shareText)
                                }
                                context.startActivity(Intent.createChooser(intent, "Share Verification Commitment"))
                            },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(containerColor = CyberCyan, contentColor = CyberBlack)
                        ) {
                            Icon(imageVector = Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(text = "Share Record", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}
