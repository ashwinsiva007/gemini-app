package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.*
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.entity.TripEntity
import com.example.ui.theme.*
import com.example.ui.viewmodel.DashcamViewModel
import com.example.ui.viewmodel.LiveTripState
import com.example.ui.viewmodel.NavigationScreen
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun DashboardScreenView(viewModel: DashcamViewModel) {
    val trips by viewModel.trips.collectAsState()
    val alerts by viewModel.alerts.collectAsState()
    val userEntity by viewModel.userEntity.collectAsState()
    val liveTripState by viewModel.liveTrip.collectAsState()

    var selectedTripForDetail by remember { mutableStateOf<TripEntity?>(null) }

    val configuration = LocalConfiguration.current
    val isCompact = configuration.screenWidthDp < 768
    val paddingSize = if (isCompact) 14.dp else 24.dp
    val spacingSize = if (isCompact) 16.dp else 24.dp

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundDeep)
            .padding(paddingSize),
        verticalArrangement = Arrangement.spacedBy(spacingSize)
    ) {
        // Welcome and Profile Completion Row
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "PILOT: ${(userEntity?.name ?: "UNKNOWN").uppercase(Locale.US)}",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Black,
                        color = TextPrimary,
                        fontFamily = FontFamily.Monospace,
                        letterSpacing = (-0.5).sp
                    )
                    Text(
                        text = "SYS_STATUS: ACTIVE // CO-PILOT ONLINE_01",
                        fontSize = 11.sp,
                        color = AccentNeonBlue,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        letterSpacing = 0.5.sp
                    )
                }

                // Profile completion status widget
                userEntity?.let {
                    if (it.profileCompletion < 100) {
                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = "SETUP_MATRIX: ${it.profileCompletion}%",
                                fontSize = 10.sp,
                                color = WarningAmber,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            LinearProgressIndicator(
                                progress = { it.profileCompletion / 100f },
                                modifier = Modifier
                                    .width(130.dp)
                                    .height(5.dp)
                                    .clip(RoundedCornerShape(2.dp)),
                                color = WarningAmber,
                                trackColor = CardSurfaceElevated
                            )
                        }
                    } else {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .background(SuccessGreen.copy(alpha = 0.15f), RoundedCornerShape(6.dp))
                                .border(1.dp, SuccessGreen.copy(alpha = 0.4f), RoundedCornerShape(6.dp))
                                .padding(horizontal = 10.dp, vertical = 5.dp)
                        ) {
                            Icon(Icons.Default.Verified, contentDescription = "Verified Profile", tint = SuccessGreen, modifier = Modifier.size(12.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "SYS_PILOT_VERIFIED",
                                fontSize = 10.sp,
                                color = SuccessGreen,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }
                }
            }
        }

        // Active Status indicator banner
        item {
            AnimatedVisibility(visible = liveTripState.isRecording) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(SuccessGreen.copy(alpha = 0.12f), RoundedCornerShape(8.dp))
                        .border(1.dp, SuccessGreen.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(SuccessGreen)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                "DVR_STREAM: RUNNING",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary,
                                fontFamily = FontFamily.Monospace
                            )
                            Text(
                                "MAPPED ROUTE • VELOCITY: ${String.format(Locale.US, "%.1f", liveTripState.speedKmh)} KM/H",
                                fontSize = 9.sp,
                                color = TextSecondary,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }

                    Button(
                        onClick = { viewModel.navigateTo(NavigationScreen.LiveFeed) },
                        colors = ButtonDefaults.buttonColors(containerColor = SuccessGreen),
                        shape = RoundedCornerShape(6.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(
                            "HUD_FEED",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            }
        }

        // GRID - Stat Cards
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                StatCard(
                    icon = Icons.Default.Timer,
                    label = "Driving Time",
                    value = formatDrivingTime(liveTripState),
                    tint = AccentBlue,
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    icon = Icons.Default.DirectionsCar,
                    label = "Weekly Trips",
                    value = trips.size.toString(),
                    tint = AccentNeonBlue,
                    modifier = Modifier.weight(1f)
                )
            }
        }
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                StatCard(
                    icon = Icons.Default.ReportProblem,
                    label = "Total Alerts",
                    value = alerts.size.toString(),
                    tint = WarningAmber,
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    icon = Icons.Default.ShareLocation,
                    label = "Log Distance",
                    value = String.format(Locale.US, "%.1f km", calculateTotalDistance(trips, liveTripState)),
                    tint = SuccessGreen,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // QUICK ACTIONS
        item {
            Text(
                text = "Quick Command Console",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // START / STOP RECORDING
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(CardSurface)
                        .border(1.dp, CardSurfaceElevated, RoundedCornerShape(12.dp))
                        .clickable {
                            if (liveTripState.isRecording) viewModel.stopRecording() else viewModel.startRecording()
                        }
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = if (liveTripState.isRecording) Icons.Default.StopCircle else Icons.Default.PlayArrow,
                        contentDescription = "Record",
                        tint = if (liveTripState.isRecording) DangerRed else SuccessGreen,
                        modifier = Modifier.size(32.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = if (liveTripState.isRecording) "Stop Telemetry" else "Start Telemetry",
                        color = TextPrimary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                // LIVE HUD
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(CardSurface)
                        .border(1.dp, CardSurfaceElevated, RoundedCornerShape(12.dp))
                        .clickable { viewModel.navigateTo(NavigationScreen.LiveFeed) }
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(Icons.Default.Videocam, contentDescription = "Live", tint = AccentBlue, modifier = Modifier.size(32.dp))
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Live HUD Cam", color = TextPrimary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }

                // INCIDENTS (Reports)
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(CardSurface)
                        .border(1.dp, CardSurfaceElevated, RoundedCornerShape(12.dp))
                        .clickable { viewModel.navigateTo(NavigationScreen.Incidents) }
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(Icons.Default.Analytics, contentDescription = "Incidents", tint = WarningAmber, modifier = Modifier.size(32.dp))
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("View Reports", color = TextPrimary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        // HIGH-TECH DROWSINESS TREND CHART (Canvas based native rendering!)
        item {
            Text(
                text = "Drowsiness & Alert Trend (Last 7 Days)",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            Spacer(modifier = Modifier.height(12.dp))
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .border(1.dp, CardSurfaceElevated, RoundedCornerShape(16.dp)),
                colors = CardDefaults.cardColors(containerColor = CardSurface)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    DrowsinessTrendChart(alertsState = alerts)
                }
            }
        }

        // RECENT TRIPS TABLE
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Recent Trip logs",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Text(
                    text = "See All",
                    color = AccentNeonBlue,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable { viewModel.navigateTo(NavigationScreen.Trips) }
                )
            }
        }

        if (trips.isEmpty() && !liveTripState.isRecording) {
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(CardSurface, RoundedCornerShape(12.dp))
                        .padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(Icons.Default.Info, contentDescription = "No Trips", tint = TextSecondary, modifier = Modifier.size(48.dp))
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("No trip telemetry recorded yet.", color = TextPrimary, fontSize = 14.sp)
                    Text("Tap 'Start Telemetry' to begin logging route.", color = TextSecondary, fontSize = 11.sp)
                }
            }
        } else {
            items(trips.take(4)) { trip ->
                TripRowItem(trip = trip, onClick = { selectedTripForDetail = trip })
            }
        }
    }

    // DETAIL MODAL OVERLAY
    selectedTripForDetail?.let { trip ->
        TripDetailModal(trip = trip, onDismiss = { selectedTripForDetail = null })
    }
}

// Chart drawn manually with beautiful smooth path, grids and glow
@Composable
fun DrowsinessTrendChart(alertsState: List<com.example.data.entity.AlertEntity>) {
    Canvas(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Transparent)
    ) {
        val width = size.width
        val height = size.height

        // Simple mock analytics index mapping for 7 days
        val mockDataPoints = listOf(2f, 5f, 1f, 4f, 2f, 0f, 3f) // Base static trend
        val days = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")

        // Draw structural gridlines
        val gridLinesCount = 4
        for (i in 0..gridLinesCount) {
            val y = height * i / gridLinesCount
            drawLine(
                color = CardSurfaceElevated.copy(alpha = 0.5f),
                start = Offset(0f, y),
                end = Offset(width, y),
                strokeWidth = 1.dp.toPx()
            )
        }

        // Map trend coords
        val totalDays = mockDataPoints.size
        val points = mockDataPoints.mapIndexed { index, value ->
            val x = width * index / (totalDays - 1)
            val y = height - (height * (value / 6f)) // max value 6
            Offset(x, y)
        }

        // Draw glowing neon boundary path lines
        val linePath = Path().apply {
            if (points.isNotEmpty()) {
                moveTo(points[0].x, points[0].y)
                for (i in 1 until points.size) {
                    val pPrev = points[i - 1]
                    val pCurr = points[i]
                    // Bezier smooth curves mapping
                    cubicTo(
                        (pPrev.x + pCurr.x) / 2, pPrev.y,
                        (pPrev.x + pCurr.x) / 2, pCurr.y,
                        pCurr.x, pCurr.y
                    )
                }
            }
        }

        drawPath(
            path = linePath,
            color = AccentBlue,
            style = Stroke(width = 3.dp.toPx())
        )

        // Plot dynamic nodes with glows
        points.forEach { point ->
            drawCircle(
                color = AccentNeonBlue,
                radius = 5.dp.toPx(),
                center = point
            )
            drawCircle(
                color = AccentBlue.copy(alpha = 0.3f),
                radius = 11.dp.toPx(),
                center = point
            )
        }
    }
}

@Composable
fun StatCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String,
    tint: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .height(84.dp)
            .border(1.dp, CardSurfaceElevated, RoundedCornerShape(16.dp)),
        colors = CardDefaults.cardColors(containerColor = CardSurface)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(14.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = label.uppercase(),
                    fontSize = 9.sp,
                    color = TextSecondary,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.4.sp,
                    fontFamily = FontFamily.Monospace,
                    maxLines = 1
                )
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = tint,
                    modifier = Modifier.size(14.dp)
                )
            }
            Text(
                text = value,
                fontSize = 20.sp,
                fontWeight = FontWeight.Black,
                color = tint,
                fontFamily = FontFamily.Monospace
            )
        }
    }
}

@Composable
fun TripRowItem(trip: TripEntity, onClick: () -> Unit) {
    val formatter = remember { SimpleDateFormat("EEE, dd MMM hh:mm a", Locale.getDefault()) }
    val dateStr = formatter.format(Date(trip.startTime))

    val scoreColor = when {
        trip.drivingScore >= 80 -> SuccessGreen
        trip.drivingScore >= 50 -> WarningAmber
        else -> DangerRed
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(CardSurface)
            .border(1.dp, CardSurfaceElevated, RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(scoreColor.copy(alpha = 0.12f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Navigation, contentDescription = "Trip", tint = scoreColor, modifier = Modifier.size(18.dp))
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(text = dateStr, color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                Text(text = "Distance: ${String.format(Locale.US, "%.1f", trip.distanceKm)} km • Alerts: ${trip.alertCount}", color = TextSecondary, fontSize = 11.sp)
            }
        }

        Column(horizontalAlignment = Alignment.End) {
            Text(
                text = "${trip.drivingScore}",
                color = scoreColor,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace
            )
            Text(text = "Points", color = TextSecondary, fontSize = 10.sp)
        }
    }
}

@Composable
fun TripDetailModal(trip: TripEntity, onDismiss: () -> Unit) {
    val dateStr = remember(trip.startTime) {
        SimpleDateFormat("EEE, dd MMM yyyy - hh:mm a", Locale.getDefault()).format(Date(trip.startTime))
    }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Trip Telemetry Map & Logs", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Trip overview card
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(CardSurfaceElevated, RoundedCornerShape(12.dp))
                        .padding(16.dp)
                ) {
                    Text("Session details: $dateStr", color = TextSecondary, fontSize = 11.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Column {
                            Text("LOGGED SCORE", fontSize = 10.sp, color = TextSecondary)
                            Text("${trip.drivingScore}/100", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = if (trip.drivingScore >= 80) SuccessGreen else if (trip.drivingScore >= 50) WarningAmber else DangerRed)
                        }
                        Column {
                            Text("DISTANCE", fontSize = 10.sp, color = TextSecondary)
                            Text(String.format(Locale.US, "%.1f km", trip.distanceKm), fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                        }
                        Column {
                            Text("TEMPO", fontSize = 10.sp, color = TextSecondary)
                            Text(trip.status, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = if (trip.status == "Safe") SuccessGreen else DangerRed)
                        }
                    }
                }

                // ROUTE MAP CANVAS (Simulate visual routing polyline)
                Text("Route Polyline Geometry", color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp)
                        .background(BackgroundDeep, RoundedCornerShape(8.dp))
                        .border(1.dp, CardSurfaceElevated, RoundedCornerShape(8.dp))
                ) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val path = Path().apply {
                            moveTo(size.width * 0.15f, size.height * 0.8f)
                            quadraticTo(size.width * 0.4f, size.height * 0.2f, size.width * 0.6f, size.height * 0.6f)
                            lineTo(size.width * 0.85f, size.height * 0.15f)
                        }
                        drawPath(path, AccentBlue, style = Stroke(width = 4.dp.toPx()))
                        drawCircle(SuccessGreen, radius = 6.dp.toPx(), center = Offset(size.width * 0.15f, size.height * 0.8f))
                        drawCircle(DangerRed, radius = 6.dp.toPx(), center = Offset(size.width * 0.85f, size.height * 0.15f))
                    }
                    Text("START PIN (Green) • END (Red)", modifier = Modifier.align(Alignment.BottomCenter).padding(8.dp), color = TextSecondary, fontSize = 9.sp)
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Dismiss Telemetry", color = AccentNeonBlue)
            }
        },
        containerColor = CardSurface
    )
}

private fun formatDrivingTime(state: LiveTripState): String {
    val hrs = state.durationSeconds / 3600
    val mins = (state.durationSeconds % 3600) / 60
    val secs = state.durationSeconds % 60
    return String.format(Locale.US, "%02d:%02d:%02d", hrs, mins, secs)
}

private fun calculateTotalDistance(list: List<TripEntity>, live: LiveTripState): Double {
    var total = list.sumOf { it.distanceKm }
    if (live.isRecording) {
        total += live.distanceKm
    }
    return total
}
