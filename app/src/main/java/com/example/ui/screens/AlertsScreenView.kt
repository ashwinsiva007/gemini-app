package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.entity.AlertEntity
import com.example.ui.theme.*
import com.example.ui.viewmodel.DashcamViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun AlertsScreenView(viewModel: DashcamViewModel) {
    val alerts by viewModel.alerts.collectAsState()
    val context = LocalContext.current

    var selectedSeverityFilter by remember { mutableStateOf("All") } // All, Level 1, Level 2, Level 3
    var hideResolved by remember { mutableStateOf(false) }

    val filteredAlerts = alerts.filter {
        (selectedSeverityFilter == "All" || it.severity.contains(selectedSeverityFilter)) &&
        (!hideResolved || !it.resolved)
    }

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
        // Timeline Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Alert Dispatch Timeline",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Text(
                    text = "AI drowsiness & road rule incident history",
                    fontSize = 12.sp,
                    color = TextSecondary
                )
            }

            // CSV Export button
            Button(
                onClick = {
                    Toast.makeText(context, "Exporting ${filteredAlerts.size} alerts as telemetry CSV...", Toast.LENGTH_LONG).show()
                },
                colors = ButtonDefaults.buttonColors(containerColor = AccentBlue),
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Download, contentDescription = "CSV", modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Export CSV", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        // Timeline Filter Bar (Horizontal Pill Selection)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf("All", "Level 1", "Level 2", "Level 3").forEach { label ->
                val isSelected = selectedSeverityFilter == label
                Box(
                    modifier = Modifier
                        .background(
                            if (isSelected) AccentBlue else CardSurface,
                            RoundedCornerShape(20.dp)
                        )
                        .border(
                            1.dp,
                            if (isSelected) AccentBlue else CardSurfaceElevated,
                            RoundedCornerShape(20.dp)
                        )
                        .clickable { selectedSeverityFilter = label }
                        .padding(horizontal = 14.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = label,
                        color = if (isSelected) TextPrimary else TextSecondary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // Hide resolved filter
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            Checkbox(checked = hideResolved, onCheckedChange = { hideResolved = it })
            Text("Hide resolved warning items", color = TextSecondary, fontSize = 12.sp)
        }

        // Timeline Lists
        if (filteredAlerts.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.CheckCircle, contentDescription = "Safe", tint = SuccessGreen, modifier = Modifier.size(54.dp))
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("All telemetry logs clear!", color = TextPrimary, fontSize = 14.sp)
                    Text("No unresolved alert signals captured.", color = TextSecondary, fontSize = 11.sp)
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(filteredAlerts) { alert ->
                    AlertTimelineItem(alert = alert, onResolve = { viewModel.resolveAlert(alert.id) })
                }
            }
        }
    }
}

@Composable
fun AlertTimelineItem(alert: AlertEntity, onResolve: () -> Unit) {
    val dateStr = remember(alert.timestamp) {
        SimpleDateFormat("EEE, dd MMM - hh:mm a", Locale.getDefault()).format(Date(alert.timestamp))
    }

    val color = when {
        alert.severity.contains("3") -> DangerRed
        alert.severity.contains("2") -> WarningAmber
        else -> AccentBlue
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(CardSurface)
            .border(1.dp, CardSurfaceElevated, RoundedCornerShape(12.dp))
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(modifier = Modifier.weight(1f)) {
            // Severity Color Indicator Line
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .height(60.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(color)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = alert.type,
                        color = TextPrimary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Box(
                        modifier = Modifier
                            .background(color.copy(alpha = 0.12f), RoundedCornerShape(4.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(alert.severity, color = color, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                    }
                }
                Text("Logged at: $dateStr", color = TextSecondary, fontSize = 11.sp)
                Text(
                    text = "Coords: ${String.format(Locale.US, "%.4f, %.4f", alert.latitude, alert.longitude)}",
                    color = TextSecondary,
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace
                )
            }
        }

        // Action Status Mark resolved button
        if (!alert.resolved) {
            Button(
                onClick = onResolve,
                colors = ButtonDefaults.buttonColors(containerColor = CardSurfaceElevated),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Resolve", fontSize = 11.sp, color = AccentNeonBlue, fontWeight = FontWeight.Bold)
            }
        } else {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.DoneAll, contentDescription = "Resolved", tint = SuccessGreen, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Resolved", color = SuccessGreen, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}
