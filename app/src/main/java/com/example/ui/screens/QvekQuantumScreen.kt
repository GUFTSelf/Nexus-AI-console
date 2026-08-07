package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.Science
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

@Composable
fun QvekQuantumScreen(
    modifier: Modifier = Modifier
) {
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
                    .padding(20.dp)
            ) {
                Box(
                    modifier = Modifier
                        .background(CyberCyan.copy(alpha = 0.2f), RoundedCornerShape(4.dp))
                        .border(1.dp, CyberCyan, RoundedCornerShape(4.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(text = "RESEARCH PROTOTYPE & ROADMAP", color = CyberCyan, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.Memory, contentDescription = null, tint = CyberCyan, modifier = Modifier.size(28.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "QVEK — QUANTUM TRUST LAYER", color = OffWhiteText, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "A proposed VEK-based software trust and verification layer designed for hybrid classical-quantum computing workflows.",
                    color = MutedText,
                    fontSize = 12.sp,
                    lineHeight = 17.sp
                )
            }
        }

        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(CyberSurface, RoundedCornerShape(8.dp))
                    .border(1.dp, MutedBorder, RoundedCornerShape(8.dp))
                    .padding(16.dp)
            ) {
                Text(text = "QVEK CAPABILITY SPECIFICATION", color = ElectricLime, fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                Spacer(modifier = Modifier.height(12.dp))

                QvekPillar(title = "Hybrid Workflow Governance", desc = "Verifies classical inputs before submission to QPU quantum execution pipelines.")
                QvekPillar(title = "Error-Aware Execution Records", desc = "Binds noisy intermediate-scale quantum (NISQ) measurement results with error mitigation metadata.")
                QvekPillar(title = "Reproducibility Traces", desc = "Records deterministic seed state, circuit compilation hash, and QPU calibration snapshot.")
                QvekPillar(title = "Quantum Job Policy Enforcement", desc = "Enforces cryptographic post-quantum signature verification on all remote jobs.")
                QvekPillar(title = "IBM Quantum Integration Concept", desc = "Future API bridge pathway connecting Qiskit quantum runtime jobs to VEK audit ledgers.")
            }
        }
    }
}

@Composable
private fun QvekPillar(title: String, desc: String) {
    Column(modifier = Modifier.padding(vertical = 6.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(imageVector = Icons.Default.Science, contentDescription = null, tint = CyberCyan, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text(text = title, color = OffWhiteText, fontSize = 13.sp, fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.height(2.dp))
        Text(text = desc, color = MutedText, fontSize = 12.sp)
    }
}
