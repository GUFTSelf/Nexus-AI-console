package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LibraryBooks
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

data class RegistrySource(
    val name: String,
    val publisher: String,
    val domain: String,
    val type: String,
    val reliabilityScore: Int,
    val description: String
)

@Composable
fun EvidenceLibraryScreen(
    modifier: Modifier = Modifier
) {
    val registers = listOf(
        RegistrySource("WHOIS Domain ICANN Registry", "ICANN / VeriSign", "Identity / Security", "Primary Register", 99, "Authoritative domain ownership, DNSSEC status, and registrar data."),
        RegistrySource("IAF CertSearch Global Database", "International Accreditation Forum", "Defense / ISO", "Primary Register", 98, "Official global register for ISO/IEC 27001, 9001, and accredited ISMS certificates."),
        RegistrySource("CyberAB C3PAO Marketplace", "CMMC Accreditation Body", "Defense / Gov", "Primary Register", 98, "Official DoD CMMC 2.0 accredited assessor register."),
        RegistrySource("FDA DailyMed & Orange Book", "U.S. Food and Drug Administration", "Healthcare", "Primary Register", 99, "Official approved drug labels, therapeutic equivalences, and clinical safety alerts."),
        RegistrySource("SEC EDGAR Filing Database", "U.S. Securities & Exchange Commission", "Financial Services", "Primary Register", 99, "Public 10-K, 10-Q, and corporate financial disclosures.")
    )

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
                    .border(1.dp, CyberCyan, RoundedCornerShape(8.dp))
                    .padding(16.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.LibraryBooks, contentDescription = null, tint = CyberCyan)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "EVIDENCE & SOURCE LIBRARY", color = CyberCyan, fontSize = 13.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = "Catalog of verified primary source registers used by VEK Kernel.", color = MutedText, fontSize = 12.sp)
            }
        }

        items(registers) { source ->
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
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.Verified, contentDescription = null, tint = StatusVerified, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = source.name, color = OffWhiteText, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    }
                    Box(
                        modifier = Modifier
                            .background(StatusVerified.copy(alpha = 0.2f), RoundedCornerShape(4.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(text = "${source.reliabilityScore}% RELIABLE", color = StatusVerified, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))
                Text(text = "Publisher: ${source.publisher} | Domain: ${source.domain}", color = CyberCyan, fontSize = 11.sp)
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = source.description, color = MutedText, fontSize = 12.sp)
            }
        }
    }
}
