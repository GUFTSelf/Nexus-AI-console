package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
fun ReplayComparisonScreen(
    viewModel: NexusViewModel,
    modifier: Modifier = Modifier
) {
    val replayResult by viewModel.replayResult.collectAsState()

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

    var replayCount by remember { mutableStateOf(2) }
    var selectedOpType by remember { mutableStateOf(MathOpType.ADD) }
    var operandInput by remember { mutableStateOf("5") }
    var validationError by remember { mutableStateOf<String?>(null) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(CyberBackground)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Mode Header
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(CyberSurfaceHeader, RoundedCornerShape(12.dp))
                    .border(1.dp, CyberCyan.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                    .padding(16.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Repeat,
                        contentDescription = "Replay Comparison",
                        tint = CyberCyan,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "REPLAY COMPARISON",
                            color = CyberCyan,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = "Byte-for-byte canonical JSON & SHA-256 equivalence check across 1 to 10 runs",
                            color = MutedText,
                            fontSize = 11.sp
                        )
                    }
                }
            }
        }

        // Configuration Card
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
                    text = "REPLAY COUNT (1 - 10 RUNS)",
                    color = OffWhiteText,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Slider(
                        value = replayCount.toFloat(),
                        onValueChange = { replayCount = it.toInt() },
                        valueRange = 1f..10f,
                        steps = 8,
                        modifier = Modifier.weight(1f),
                        colors = SliderDefaults.colors(
                            thumbColor = ElectricLime,
                            activeTrackColor = ElectricLime
                        )
                    )

                    Text(
                        text = "$replayCount RUNS",
                        color = ElectricLime,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.Monospace
                    )
                }

                Divider(color = MutedBorder)

                Text(
                    text = "INITIAL VALUE",
                    color = OffWhiteText,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )

                OutlinedTextField(
                    value = initialValueInput,
                    onValueChange = {
                        initialValueInput = it
                        validationError = null
                    },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Initial Value") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = CyberCyan,
                        unfocusedBorderColor = MutedBorder,
                        focusedTextColor = OffWhiteText,
                        unfocusedTextColor = OffWhiteText
                    ),
                    singleLine = true
                )

                Divider(color = MutedBorder)

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "PIPELINE OPERATIONS (${operations.size})",
                        color = CyberCyan,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )

                    OutlinedButton(
                        onClick = {
                            initialValueInput = "7"
                            operations = listOf(
                                MathOp(MathOpType.MULTIPLY, 8.0),
                                MathOp(MathOpType.SUBTRACT, 11.0),
                                MathOp(MathOpType.DIVIDE, 5.0),
                                MathOp(MathOpType.ADD, 9.0)
                            )
                        },
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = CyberCyan),
                        border = androidx.compose.foundation.BorderStroke(1.dp, CyberCyan)
                    ) {
                        Text(text = "Load Validation Vector", fontSize = 11.sp)
                    }
                }

                operations.forEachIndexed { idx, op ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(CyberSurfaceHeader, RoundedCornerShape(4.dp))
                            .padding(8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Op ${idx + 1}: ${op.op.name} ${if (op.value % 1.0 == 0.0) op.value.toLong() else op.value}",
                            color = OffWhiteText,
                            fontSize = 12.sp,
                            fontFamily = FontFamily.Monospace
                        )
                        IconButton(
                            onClick = { operations = operations.filterIndexed { i, _ -> i != idx } },
                            modifier = Modifier.size(20.dp)
                        ) {
                            Icon(imageVector = Icons.Default.Close, contentDescription = "Remove", tint = MutedText)
                        }
                    }
                }

                if (validationError != null) {
                    Text(
                        text = validationError!!,
                        color = MaterialTheme.colorScheme.error,
                        fontSize = 11.sp
                    )
                }

                Button(
                    onClick = {
                        val initVal = initialValueInput.toDoubleOrNull()
                        if (initVal == null) {
                            validationError = "Initial value must be a valid number"
                        } else if (operations.isEmpty()) {
                            validationError = "Add at least one operation to test replays"
                        } else {
                            validationError = null
                            viewModel.executeReplay(initVal, operations, replayCount)
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = CyberCyan, contentColor = CyberBlack),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(imageVector = Icons.Default.RepeatOne, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "RUN $replayCount REPLAYS & COMPARE", fontWeight = FontWeight.Black)
                }
            }
        }

        // Replay Comparison Results
        if (replayResult != null) {
            val res = replayResult!!
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(CyberSurfaceHeader, RoundedCornerShape(8.dp))
                        .border(
                            width = 2.dp,
                            color = if (res.pass) ElectricLime else MaterialTheme.colorScheme.error,
                            shape = RoundedCornerShape(8.dp)
                        )
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "REPLAY COMPARISON RESULT",
                            color = OffWhiteText,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Box(
                            modifier = Modifier
                                .background(
                                    if (res.pass) ElectricLime.copy(alpha = 0.2f) else MaterialTheme.colorScheme.error.copy(alpha = 0.2f),
                                    RoundedCornerShape(4.dp)
                                )
                                .border(
                                    1.dp,
                                    if (res.pass) ElectricLime else MaterialTheme.colorScheme.error,
                                    RoundedCornerShape(4.dp)
                                )
                                .padding(horizontal = 12.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = if (res.pass) "PASS" else "FAIL",
                                color = if (res.pass) ElectricLime else MaterialTheme.colorScheme.error,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Black
                            )
                        }
                    }

                    Divider(color = MutedBorder)

                    if (res.pass) {
                        Text(
                            text = "All ${res.replayCount} runs yielded identical normalized canonical JSON records and matching SHA-256 integrity commitments.",
                            color = ElectricLime,
                            fontSize = 12.sp
                        )
                    } else {
                        Text(
                            text = "Divergence detected on run ${res.firstDivergenceStep ?: 2}: ${res.divergenceReason}",
                            color = MaterialTheme.colorScheme.error,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "RUN DETAILS & COMMITMENTS",
                        color = OffWhiteText,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )

                    res.runs.forEachIndexed { i, run ->
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(CyberSurface, RoundedCornerShape(4.dp))
                                .border(1.dp, MutedBorder, RoundedCornerShape(4.dp))
                                .padding(10.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "RUN ${i + 1}",
                                    color = ElectricLime,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace
                                )

                                Text(
                                    text = "Final Value: ${run.finalValue}",
                                    color = OffWhiteText,
                                    fontSize = 11.sp,
                                    fontFamily = FontFamily.Monospace
                                )
                            }

                            Text(
                                text = "SHA-256: ${run.sha256Hash}",
                                color = CyberCyan,
                                fontSize = 10.sp,
                                fontFamily = FontFamily.Monospace,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
