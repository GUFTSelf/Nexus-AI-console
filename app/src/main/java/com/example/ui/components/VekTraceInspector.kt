package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.VerificationCase
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun VekTraceInspector(
    caseItem: VerificationCase,
    modifier: Modifier = Modifier
) {
    var showRawJson by remember { mutableStateOf(false) }

    val formattedDate = remember(caseItem.timestamp) {
        SimpleDateFormat("yyyy-MM-dd HH:mm:ss z", Locale.US).format(Date(caseItem.timestamp))
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(CyberSurfaceHeader, RoundedCornerShape(8.dp))
            .border(1.dp, CyberCyan.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Shield,
                    contentDescription = null,
                    tint = CyberCyan,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "VEK DETERMINISTIC EXECUTION TRACE",
                    color = CyberCyan,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
            }
            TextButton(onClick = { showRawJson = !showRawJson }) {
                Icon(
                    imageVector = Icons.Default.Code,
                    contentDescription = null,
                    tint = ElectricLime,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = if (showRawJson) "Hide JSON" else "Inspect JSON Trace",
                    color = ElectricLime,
                    fontSize = 12.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        TraceRow(label = "TRACE ID", value = caseItem.traceId, isCode = true)
        TraceRow(label = "CASE ID", value = caseItem.caseId, isCode = true)
        TraceRow(
            label = "TRACE HASH",
            value = caseItem.demonstrationTraceHash,
            isCode = true,
            badge = if (caseItem.isDemonstration) "Demonstration Hash" else "Verified Kernel Trace"
        )
        TraceRow(label = "TIMESTAMP", value = formattedDate)
        TraceRow(label = "DOMAIN POLICY", value = caseItem.domain)
        TraceRow(label = "HUMAN REVIEW", value = if (caseItem.humanReviewRequired) "Mandatory Escalation Active" else "Not Required")

        if (showRawJson) {
            Spacer(modifier = Modifier.height(12.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(CyberBlack, RoundedCornerShape(6.dp))
                    .border(1.dp, MutedBorder, RoundedCornerShape(6.dp))
                    .padding(12.dp)
            ) {
                Text(
                    text = """
                    {
                      "traceId": "${caseItem.traceId}",
                      "caseId": "${caseItem.caseId}",
                      "hash": "${caseItem.demonstrationTraceHash}",
                      "domain": "${caseItem.domain}",
                      "contentType": "${caseItem.contentType}",
                      "status": "${caseItem.status.name}",
                      "riskLevel": "${caseItem.riskLevel}",
                      "isDemonstration": ${caseItem.isDemonstration},
                      "kernelVersion": "VEK-2026.4-GUTS"
                    }
                    """.trimIndent(),
                    color = ElectricLime,
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace
                )
            }
        }
    }
}

@Composable
private fun TraceRow(
    label: String,
    value: String,
    isCode: Boolean = false,
    badge: String? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            color = MutedText,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold
        )
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (badge != null) {
                Box(
                    modifier = Modifier
                        .padding(end = 8.dp)
                        .background(CyberSurfaceVariant, RoundedCornerShape(4.dp))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(text = badge, color = CyberCyan, fontSize = 10.sp)
                }
            }
            Text(
                text = value,
                color = OffWhiteText,
                fontSize = 12.sp,
                fontFamily = if (isCode) FontFamily.Monospace else FontFamily.Default,
                fontWeight = if (isCode) FontWeight.Bold else FontWeight.Normal
            )
        }
    }
}
