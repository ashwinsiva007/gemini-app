package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import com.example.ui.viewmodel.DashcamViewModel

@Composable
fun SettingsScreenView(viewModel: DashcamViewModel) {
    var expandedSection by remember { mutableIntStateOf(-1) } // -1: none, 0: Recording, 1: Alerts, 2: Appearances & Accents, 3: Account Privacy

    val configuration = androidx.compose.ui.platform.LocalConfiguration.current
    val isCompact = configuration.screenWidthDp < 768
    val paddingSize = if (isCompact) 14.dp else 24.dp

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundDeep)
            .padding(paddingSize)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Column {
            Text(
                text = "System Settings Console",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            Text(
                text = "Fine-tune system sensitivity and DVR loops",
                fontSize = 12.sp,
                color = TextSecondary
            )
        }

        // ACCORDION 1: Recording settings
        AccordionItem(
            index = 0,
            title = "DVR Recording Metrics",
            icon = Icons.Default.Circle,
            expandedIndex = expandedSection,
            onToggle = { expandedSection = if (expandedSection == 0) -1 else 0 }
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                var clipDuration by remember { mutableStateOf("3 Min") }
                val durations = listOf("1 Min", "3 Min", "5 Min")
                Text("Select Default Clip Duration", color = TextSecondary, fontSize = 11.sp)
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    durations.forEach { d ->
                        val isSel = clipDuration == d
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .background(if (isSel) AccentBlue else CardSurfaceElevated, RoundedCornerShape(8.dp))
                                .clickable { clipDuration = d }
                                .padding(vertical = 12.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(d, color = TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                var storageLim by remember { mutableStateOf("2 GB") }
                val limits = listOf("1 GB", "2 GB", "5 GB")
                Text("Storage Allocation Limit Threshold", color = TextSecondary, fontSize = 11.sp)
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    limits.forEach { lim ->
                        val isLimitSel = storageLim == lim
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .background(if (isLimitSel) AccentBlue else CardSurfaceElevated, RoundedCornerShape(8.dp))
                                .clickable { storageLim = lim }
                                .padding(vertical = 12.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(lim, color = TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // ACCORDION 2: Alert Sensitivity and preferences
        AccordionItem(
            index = 1,
            title = "AI Alert Preferences & Volume",
            icon = Icons.Default.Sensors,
            expandedIndex = expandedSection,
            onToggle = { expandedSection = if (expandedSection == 1) -1 else 1 }
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                var alertDrowsiness by remember { mutableStateOf(true) }
                var alertSpeeding by remember { mutableStateOf(true) }
                var alertLane by remember { mutableStateOf(false) }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Enable Drowsiness Tracker (AI Face)", color = TextPrimary, fontSize = 13.sp)
                    Switch(checked = alertDrowsiness, onCheckedChange = { alertDrowsiness = it })
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Enable Speed Limit Compliance Warnings", color = TextPrimary, fontSize = 13.sp)
                    Switch(checked = alertSpeeding, onCheckedChange = { alertSpeeding = it })
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Lane Drifting Cautions (Optional Hough)", color = TextPrimary, fontSize = 13.sp)
                    Switch(checked = alertLane, onCheckedChange = { alertLane = it })
                }

                // Sensitivity slider
                var sensorSensitivity by remember { mutableStateOf(75f) }
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Drowsiness EAR Trigger trigger bound", color = TextSecondary, fontSize = 11.sp)
                        Text("${String.format("%.2f", 0.15 + (sensorSensitivity / 100f) * 0.15)} EAR", color = AccentBlue, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                    Slider(
                        value = sensorSensitivity,
                        onValueChange = { sensorSensitivity = it },
                        valueRange = 0f..100f,
                        colors = SliderDefaults.colors(
                            activeTrackColor = AccentBlue,
                            thumbColor = AccentBlue
                        )
                    )
                }
            }
        }

        // ACCORDION 3: App Appearance and Accents (Color Picker presets!)
        AccordionItem(
            index = 2,
            title = "App Appearance & Accents",
            icon = Icons.Default.Palette,
            expandedIndex = expandedSection,
            onToggle = { expandedSection = if (expandedSection == 2) -1 else 2 }
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                var activeTheme by remember { mutableStateOf("Dark (Default)") }
                Text("Visual theme mode", color = TextSecondary, fontSize = 11.sp)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(CardSurfaceElevated, RoundedCornerShape(8.dp))
                        .padding(14.dp)
                ) {
                    Text(activeTheme, color = TextPrimary, fontSize = 13.sp)
                }

                // 6 Preset accents row
                val presets = listOf(
                    "#3B82F6" to "Electric Blue",
                    "#EF4444" to "Retro Red",
                    "#22C55E" to "Cyber Green",
                    "#F59E0B" to "Neon Amber",
                    "#8B5CF6" to "Synthwave",
                    "#06B6D4" to "Cyan glow"
                )
                Text("Select HUD accent preset color", color = TextSecondary, fontSize = 11.sp)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    presets.forEach { (hexCode, name) ->
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(Color(android.graphics.Color.parseColor(hexCode)))
                                .border(1.dp, CardSurfaceElevated, CircleShape)
                                .clickable {
                                    activeTheme = "Preset: $name Updated"
                                }
                        )
                    }
                }
            }
        }

        // ACCORDION 4: Data Erasers / Account settings
        AccordionItem(
            index = 3,
            title = "Privacy & Telemetry Data Clearer",
            icon = Icons.Default.Lock,
            expandedIndex = expandedSection,
            onToggle = { expandedSection = if (expandedSection == 3) -1 else 3 }
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                val context = LocalContext.current

                Text("Safeguard local credentials against identity tracing.", color = TextSecondary, fontSize = 11.sp)

                Button(
                    onClick = {
                        viewModel.clearAllUserData()
                        Toast.makeText(context, "All logs, charts and telemetry lists cleared!", Toast.LENGTH_LONG).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = WarningAmber),
                    modifier = Modifier.fillMaxWidth()
                        .testTag("clear_data_button"),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.DeleteSweep, contentDescription = null, tint = TextPrimary)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Delete all speed loop history", color = TextPrimary, fontWeight = FontWeight.Bold)
                    }
                }

                Button(
                    onClick = {
                        Toast.makeText(context, "Delete action prompts initialized. Confirm account termination...", Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = DangerRed),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.RemoveCircle, contentDescription = null, tint = TextPrimary)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Delete Dashcam account", color = TextPrimary, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun AccordionItem(
    index: Int,
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    expandedIndex: Int,
    onToggle: () -> Unit,
    content: @Composable () -> Unit
) {
    val isExpanded = index == expandedIndex

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(CardSurface)
            .border(1.dp, CardSurfaceElevated, RoundedCornerShape(12.dp))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onToggle() }
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(icon, contentDescription = null, tint = AccentBlue, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(12.dp))
                Text(title, color = TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            }
            Icon(
                imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                contentDescription = if (isExpanded) "Collapse" else "Expand",
                tint = TextSecondary
            )
        }

        AnimatedVisibility(
            visible = isExpanded,
            enter = expandVertically() + fadeIn(),
            exit = shrinkVertically() + fadeOut()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 16.dp, bottom = 16.dp)
            ) {
                content()
            }
        }
    }
}
