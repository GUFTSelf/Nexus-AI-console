package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.VerificationCase
import com.example.ui.NexusViewModel
import com.example.ui.components.StatusBadge
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ConsumerDashboardScreen(
    viewModel: NexusViewModel,
    allCases: List<VerificationCase>,
    onSelectCase: (VerificationCase) -> Unit,
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }

    val filteredCases = remember(allCases, searchQuery) {
        if (searchQuery.isBlank()) allCases
        else allCases.filter { it.title.contains(searchQuery, ignoreCase = true) || it.rawInput.contains(searchQuery, ignoreCase = true) }
    }

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
                    Icon(imageVector = Icons.Default.Folder, contentDescription = null, tint = ElectricLime)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "CONSUMER VERIFICATION VAULT", color = ElectricLime, fontSize = 13.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = "Saved verification cases, evidence records, and trust assessments.", color = MutedText, fontSize = 12.sp)
            }
        }

        item {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search vault cases by keyword or ID...", color = MutedText, fontSize = 12.sp) },
                leadingIcon = { Icon(imageVector = Icons.Default.Search, contentDescription = null, tint = MutedText) },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = CyberSurface,
                    unfocusedContainerColor = CyberSurface,
                    focusedBorderColor = ElectricLime,
                    unfocusedBorderColor = MutedBorder,
                    focusedTextColor = OffWhiteText,
                    unfocusedTextColor = OffWhiteText
                )
            )
        }

        if (filteredCases.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "No cases found in Consumer Vault.", color = MutedText)
                }
            }
        } else {
            items(filteredCases, key = { it.caseId }) { caseItem ->
                val dateStr = remember(caseItem.timestamp) {
                    SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.US).format(Date(caseItem.timestamp))
                }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(CyberSurface, RoundedCornerShape(8.dp))
                        .border(1.dp, MutedBorder, RoundedCornerShape(8.dp))
                        .clickable { onSelectCase(caseItem) }
                        .padding(14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        StatusBadge(status = caseItem.status)
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = dateStr, color = MutedText, fontSize = 11.sp)
                            IconButton(
                                onClick = { viewModel.deleteCase(caseItem.caseId) },
                                modifier = Modifier.size(24.dp).padding(start = 8.dp)
                            ) {
                                Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete Case", tint = StatusUnsupported, modifier = Modifier.size(16.dp))
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = caseItem.title, color = OffWhiteText, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = caseItem.summary, color = MutedText, fontSize = 12.sp, maxLines = 2)

                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "Domain: ${caseItem.domain}", color = CyberCyan, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                        Text(text = "Trace: ${caseItem.traceId}", color = ElectricLime, fontSize = 11.sp)
                    }
                }
            }
        }
    }
}
