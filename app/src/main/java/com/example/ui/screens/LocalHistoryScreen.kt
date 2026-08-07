package com.example.ui.screens

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.LocalExecutionRecord
import com.example.ui.NexusViewModel
import com.example.ui.theme.*
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LocalHistoryScreen(
    viewModel: NexusViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    val records by viewModel.allExecutionRecords.collectAsState()

    var showDeleteAllDialog by remember { mutableStateOf(false) }
    var selectedRecordDetail by remember { mutableStateOf<LocalExecutionRecord?>(null) }
    var copiedFeedback by remember { mutableStateOf(false) }

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
                    .border(1.dp, ElectricLime.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.History,
                            contentDescription = "Local History",
                            tint = ElectricLime,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "LOCAL HISTORY & RECORDS",
                                color = ElectricLime,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Black,
                                letterSpacing = 1.sp
                            )
                            Text(
                                text = "${records.size} execution records stored in Room DB",
                                color = MutedText,
                                fontSize = 11.sp
                            )
                        }
                    }

                    if (records.isNotEmpty()) {
                        IconButton(onClick = { showDeleteAllDialog = true }) {
                            Icon(
                                imageVector = Icons.Default.DeleteForever,
                                contentDescription = "Delete All Data",
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }
            }
        }

        // Actions Bar
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = {
                        val exportJson = buildExportJson(records)
                        clipboardManager.setText(AnnotatedString(exportJson))
                        copiedFeedback = true
                    },
                    modifier = Modifier.weight(1f),
                    enabled = records.isNotEmpty(),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = ElectricLime),
                    border = androidx.compose.foundation.BorderStroke(1.dp, if (records.isNotEmpty()) ElectricLime else MutedBorder)
                ) {
                    Icon(imageVector = Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = if (copiedFeedback) "Copied JSON!" else "Export JSON", fontSize = 12.sp)
                }

                Button(
                    onClick = {
                        val exportJson = buildExportJson(records)
                        val intent = Intent(Intent.ACTION_SEND).apply {
                            type = "application/json"
                            putExtra(Intent.EXTRA_TEXT, exportJson)
                        }
                        context.startActivity(Intent.createChooser(intent, "Share Execution Records"))
                    },
                    modifier = Modifier.weight(1f),
                    enabled = records.isNotEmpty(),
                    colors = ButtonDefaults.buttonColors(containerColor = CyberCyan, contentColor = CyberBlack)
                ) {
                    Icon(imageVector = Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = "Share All Records", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        // Records List
        if (records.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(CyberSurface, RoundedCornerShape(8.dp))
                        .border(1.dp, MutedBorder, RoundedCornerShape(8.dp))
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.Inbox,
                            contentDescription = null,
                            tint = MutedText,
                            modifier = Modifier.size(36.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "No Local Execution Records Yet",
                            color = OffWhiteText,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                        Text(
                            text = "Run Deterministic Execution or Replay Comparison to record history.",
                            color = MutedText,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
            }
        } else {
            items(records, key = { it.id }) { rec ->
                RecordItemCard(
                    record = rec,
                    onSelect = { selectedRecordDetail = rec },
                    onDelete = { viewModel.deleteExecutionRecord(rec.id) }
                )
            }
        }
    }

    // Detail Modal Dialog
    if (selectedRecordDetail != null) {
        val rec = selectedRecordDetail!!
        AlertDialog(
            onDismissRequest = { selectedRecordDetail = null },
            confirmButton = {
                TextButton(onClick = { selectedRecordDetail = null }) {
                    Text(text = "Close", color = ElectricLime)
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    clipboardManager.setText(AnnotatedString(rec.canonicalJson))
                }) {
                    Text(text = "Copy JSON", color = CyberCyan)
                }
            },
            title = {
                Text(text = rec.title, color = OffWhiteText, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(text = "Mode: ${rec.mode}", color = MutedText, fontSize = 11.sp)
                    Text(text = "Timestamp: ${formatTime(rec.timestamp)}", color = MutedText, fontSize = 11.sp)
                    Text(text = "SHA-256 Hash:", color = OffWhiteText, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    Text(text = rec.sha256Hash, color = CyberCyan, fontSize = 10.sp, fontFamily = FontFamily.Monospace)
                    Text(text = "Canonical JSON:", color = OffWhiteText, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(CyberBlack, RoundedCornerShape(4.dp))
                            .padding(8.dp)
                    ) {
                        Text(text = rec.canonicalJson, color = ElectricLime, fontSize = 10.sp, fontFamily = FontFamily.Monospace)
                    }
                }
            },
            containerColor = CyberSurfaceHeader,
            shape = RoundedCornerShape(12.dp)
        )
    }

    // Delete All Confirmation Dialog
    if (showDeleteAllDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteAllDialog = false },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteAllLocalData()
                        showDeleteAllDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text(text = "Delete All Data", color = OffWhiteText)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteAllDialog = false }) {
                    Text(text = "Cancel", color = MutedText)
                }
            },
            title = { Text(text = "Delete All Local Data?", color = OffWhiteText) },
            text = { Text(text = "This action will purge all execution records, verification cases, and audit logs from your device's Room database. This action cannot be undone.", color = MutedText, fontSize = 12.sp) },
            containerColor = CyberSurfaceHeader,
            shape = RoundedCornerShape(12.dp)
        )
    }
}

@Composable
private fun RecordItemCard(
    record: LocalExecutionRecord,
    onSelect: () -> Unit,
    onDelete: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(CyberSurface, RoundedCornerShape(8.dp))
            .border(1.dp, MutedBorder, RoundedCornerShape(8.dp))
            .clickable { onSelect() }
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = record.title,
                color = OffWhiteText,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )

            IconButton(onClick = onDelete, modifier = Modifier.size(20.dp)) {
                Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete", tint = MutedText, modifier = Modifier.size(16.dp))
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = formatTime(record.timestamp),
                color = MutedText,
                fontSize = 10.sp
            )

            Text(
                text = "SHA: ${record.sha256Hash.take(12)}...",
                color = CyberCyan,
                fontSize = 10.sp,
                fontFamily = FontFamily.Monospace
            )
        }
    }
}

private fun formatTime(millis: Long): String {
    return try {
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US)
        sdf.format(Date(millis))
    } catch (e: Exception) {
        "$millis"
    }
}

private fun buildExportJson(records: List<LocalExecutionRecord>): String {
    val items = JSONArray()
    records.forEach { record ->
        val canonicalRecord = runCatching { JSONObject(record.canonicalJson) }
            .getOrElse { record.canonicalJson }
        items.put(
            JSONObject()
                .put("id", record.id)
                .put("mode", record.mode)
                .put("title", record.title)
                .put("sha256", record.sha256Hash)
                .put("canonicalRecord", canonicalRecord)
        )
    }
    return JSONObject()
        .put("exportedAt", System.currentTimeMillis())
        .put("records", items)
        .toString()
}
