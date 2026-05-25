package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.entity.IncidentReportEntity
import com.example.ui.theme.*
import com.example.ui.viewmodel.DashcamViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun IncidentsScreenView(viewModel: DashcamViewModel) {
    val incidents by viewModel.incidents.collectAsState()
    val userEntity by viewModel.userEntity.collectAsState()
    val context = LocalContext.current

    var selectedIncidentForDetail by remember { mutableStateOf<IncidentReportEntity?>(null) }

    val configuration = androidx.compose.ui.platform.LocalConfiguration.current
    val isCompact = configuration.screenWidthDp < 768
    val paddingSize = if (isCompact) 14.dp else 24.dp

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundDeep)
            .padding(paddingSize),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Column {
            Text(
                text = "Incident Dossier",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            Text(
                text = "Auto-generated accident reports ready for insurance / dispatch",
                fontSize = 12.sp,
                color = TextSecondary
            )
        }

        if (incidents.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.VerifiedUser, contentDescription = "Safe", tint = SuccessGreen, modifier = Modifier.size(54.dp))
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("No incidents reported!", color = TextPrimary, fontSize = 14.sp)
                    Text("Auto-dossiers are automatically compile on critical events.", color = TextSecondary, fontSize = 11.sp)
                }
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(1),
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(incidents) { incident ->
                    IncidentCardItem(incident = incident, onClick = { selectedIncidentForDetail = incident })
                }
            }
        }
    }

    // Incident details modal
    selectedIncidentForDetail?.let { incident ->
        IncidentDetailModal(
            incident = incident,
            driverName = userEntity?.name ?: "Pilot",
            driverBlood = userEntity?.bloodGroup ?: "O+",
            vehicleMake = userEntity?.vehicle?.make ?: "N/A",
            vehicleReg = userEntity?.vehicle?.registrationNumber ?: "N/A",
            medicalProblems = userEntity?.medicalInfo?.knownConditions?.joinToString(", ") ?: "None",
            onDismiss = { selectedIncidentForDetail = null }
        )
    }
}

@Composable
fun IncidentCardItem(incident: IncidentReportEntity, onClick: () -> Unit) {
    val dateStr = remember(incident.generatedAt) {
        SimpleDateFormat("dd MMM yyyy - hh:mm a", Locale.getDefault()).format(Date(incident.generatedAt))
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, CardSurfaceElevated, RoundedCornerShape(12.dp))
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = CardSurface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "INCIDENT BRIEF #${incident.id}",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = AccentNeonBlue
                )
                Box(
                    modifier = Modifier
                        .background(DangerRed.copy(alpha = 0.12f), RoundedCornerShape(4.dp))
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Text(text = incident.status, color = DangerRed, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = dateStr, color = TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Medium)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = "Auto-collated from critical Eye-Closer alerts.", color = TextSecondary, fontSize = 12.sp)
        }
    }
}

@Composable
fun IncidentDetailModal(
    incident: IncidentReportEntity,
    driverName: String,
    driverBlood: String,
    vehicleMake: String,
    vehicleReg: String,
    medicalProblems: String,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val dateStr = remember(incident.generatedAt) {
        SimpleDateFormat("EEE, dd MMM yyyy - hh:mm a", Locale.getDefault()).format(Date(incident.generatedAt))
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Official Incident Telex Summary", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(CardSurfaceElevated, RoundedCornerShape(8.dp))
                        .padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("PILOT DOSSIER INFO", color = AccentBlue, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    Text("Driver: $driverName", color = TextPrimary, fontSize = 12.sp)
                    Text("Blood Group: $driverBlood", color = TextPrimary, fontSize = 12.sp)
                    Text("Medical Notes: $medicalProblems", color = TextPrimary, fontSize = 12.sp)
                    Divider(color = CardSurface)
                    Text("VEHICLE MATRIX", color = AccentBlue, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    Text("Specs: $vehicleMake", color = TextPrimary, fontSize = 12.sp)
                    Text("Plate: $vehicleReg", color = TextPrimary, fontSize = 12.sp)
                    Divider(color = CardSurface)
                    Text("TIME & LOGISTICS", color = AccentBlue, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    Text("Occurred: $dateStr", color = TextPrimary, fontSize = 12.sp)
                }

                // Core Export Options
                Text("Export Dossier", color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Export PDF mockup
                    Button(
                        onClick = {
                            Toast.makeText(context, "Exporting official PDF to local storage...", Toast.LENGTH_SHORT).show()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = CardSurfaceElevated),
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(Icons.Default.FileDownload, contentDescription = "PDF", tint = AccentNeonBlue, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("PDF", fontSize = 11.sp, color = TextPrimary)
                    }

                    // Share WhatsApp Mock
                    Button(
                        onClick = {
                            Toast.makeText(context, "Sharing secure telemetry link via WhatsApp...", Toast.LENGTH_SHORT).show()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = CardSurfaceElevated),
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(Icons.Default.Share, contentDescription = "Share", tint = SuccessGreen, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("WhatsApp", fontSize = 11.sp, color = TextPrimary)
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Dismantle dossier", color = AccentNeonBlue)
            }
        },
        containerColor = CardSurface
    )
}
