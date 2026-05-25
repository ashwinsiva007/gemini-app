package com.example.ui.screens

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
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
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.example.ui.theme.*
import com.example.ui.viewmodel.DashcamViewModel
import com.example.ui.viewmodel.LiveTripState
import com.google.accompanist.permissions.*

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun LiveFeedScreenView(viewModel: DashcamViewModel) {
    val liveTrip by viewModel.liveTrip.collectAsState()
    val aiState by viewModel.aiState.collectAsState()

    // Request permissions using Compose accompanist permissions system
    val cameraPermissionState = rememberPermissionState(permission = Manifest.permission.CAMERA)
    val locationPermissionState = rememberPermissionState(permission = Manifest.permission.ACCESS_FINE_LOCATION)

    if (cameraPermissionState.status.isGranted && locationPermissionState.status.isGranted) {
        CameraActiveView(viewModel = viewModel, liveTrip = liveTrip, aiState = aiState)
    } else {
        PermissionRequiredView(
            cameraPermissionState = cameraPermissionState,
            locationPermissionState = locationPermissionState
        )
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun PermissionRequiredView(
    cameraPermissionState: PermissionState,
    locationPermissionState: PermissionState
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundDeep)
            .padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Default.CameraAlt,
            contentDescription = "Permission Needed",
            tint = AccentBlue,
            modifier = Modifier.size(72.dp)
        )
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = "Camera & Location Required",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = "DASHCAM PRO needs camera input for local drowsiness estimation and GPS permission to log route speed coordinates.",
            fontSize = 13.sp,
            color = TextSecondary,
            modifier = Modifier.padding(horizontal = 16.dp),
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = {
                cameraPermissionState.launchPermissionRequest()
                locationPermissionState.launchPermissionRequest()
            },
            colors = ButtonDefaults.buttonColors(containerColor = AccentBlue)
        ) {
            Text("Grant Required Permissions", fontWeight = FontWeight.Bold, color = TextPrimary)
        }
    }
}

@SuppressLint("DefaultLocale")
@Composable
fun CameraActiveView(
    viewModel: DashcamViewModel,
    liveTrip: LiveTripState,
    aiState: com.example.ui.viewmodel.AiSimulatorState
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    // Set up camera preview
    val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }
    var hasCameraHardware by remember { mutableStateOf(true) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundDeep)
    ) {
        // LAYER 1: Standard Active Window Render
        if (hasCameraHardware) {
            AndroidView(
                factory = { ctx ->
                    PreviewView(ctx).apply {
                        scaleType = PreviewView.ScaleType.FILL_CENTER
                        cameraProviderFuture.addListener({
                            try {
                                val cameraProvider = cameraProviderFuture.get()
                                val preview = Preview.Builder().build().also {
                                    it.setSurfaceProvider(surfaceProvider)
                                }
                                val cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA

                                cameraProvider.unbindAll()
                                cameraProvider.bindToLifecycle(
                                    lifecycleOwner,
                                    cameraSelector,
                                    preview
                                )
                            } catch (e: Exception) {
                                hasCameraHardware = false
                            }
                        }, ContextCompat.getMainExecutor(ctx))
                    }
                },
                modifier = Modifier.fillMaxSize(),
                update = { }
            )
        }

        // LAYER 1 Fallback - Sleek wireframe radar scanning telemetry
        if (!hasCameraHardware) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(CardSurface)
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    // Draw clean concentric radar circles
                    val center = androidx.compose.ui.geometry.Offset(size.width / 2, size.height / 2)
                    for (i in 1..4) {
                        drawCircle(
                            color = AccentBlue.copy(alpha = 0.08f * i),
                            radius = (100 * i).dp.toPx(),
                            center = center,
                            style = androidx.compose.ui.graphics.drawscope.Stroke(width = 1.dp.toPx())
                        )
                    }
                }
                Text(
                    text = "HARDWARE DVR SIMULATOR ACTIVE",
                    modifier = Modifier.align(Alignment.Center),
                    color = AccentNeonBlue.copy(alpha = 0.6f),
                    fontSize = 14.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // CRT overlay scan-lines effect on live feed (Canvas-based specialist diagnostic hardware pattern)
        Canvas(modifier = Modifier.fillMaxSize()) {
            val h = size.height
            val w = size.width
            val stepVal = 4.dp.toPx()
            if (stepVal > 0f) {
                var y = 0f
                while (y < h) {
                    drawLine(
                        color = Color.Black.copy(alpha = 0.18f),
                        start = androidx.compose.ui.geometry.Offset(0f, y),
                        end = androidx.compose.ui.geometry.Offset(w, y),
                        strokeWidth = 1.dp.toPx()
                    )
                    y += stepVal
                }
            }
            // Draw ghostly technical color tint layers (Specialist visual tool matrix)
            drawRect(
                color = Color(0x043B82F6), // subtle blueprint electric cyan glow
                size = size
            )
        }

        // LAYER 2: Recording HUD Stats
        val configuration = androidx.compose.ui.platform.LocalConfiguration.current
        val isCompact = configuration.screenWidthDp < 768
        val paddingSize = if (isCompact) 12.dp else 24.dp

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingSize),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // TOP PANEL Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // LIVEbadge + Duration Counter
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .background(Color.Black.copy(alpha = 0.6f), RoundedCornerShape(20.dp))
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(if (liveTrip.isRecording) DangerRed else TextSecondary)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (liveTrip.isRecording) "REC: ${formatDuration(liveTrip.durationSeconds)}" else "LIVE PREVIEW",
                        color = TextPrimary,
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold
                    )
                }

                // HUD Stats Block on top right
                Column(
                    horizontalAlignment = Alignment.End,
                    modifier = Modifier
                        .background(Color.Black.copy(alpha = 0.6f), RoundedCornerShape(12.dp))
                        .padding(12.dp)
                ) {
                    Text("SPEED: ${String.format("%.1f", liveTrip.speedKmh)} km/h", color = SuccessGreen, fontSize = 12.sp, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
                    Text("LAT: ${String.format("%.5f", liveTrip.currentLatitude)}", color = TextPrimary, fontSize = 10.sp, fontFamily = FontFamily.Monospace)
                    Text("LNG: ${String.format("%.5f", liveTrip.currentLongitude)}", color = TextPrimary, fontSize = 10.sp, fontFamily = FontFamily.Monospace)
                    if (liveTrip.isNightMode) {
                        Text("MODE: NIGHT BLOCK (+) ENHANCED", color = WarningAmber, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            // MIDDLE ROAD SIGN Alerts Area
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                liveTrip.activeRoadSign?.let { sign ->
                    Row(
                        modifier = Modifier
                            .background(WarningAmber.copy(alpha = 0.9f), RoundedCornerShape(20.dp))
                            .padding(horizontal = 18.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Signpost, contentDescription = "Sign Detected", tint = CardSurface, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "ROAD SIGN DETECTED: $sign",
                            color = CardSurface,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // BOTTOM PANEL CONFIGURATION Drawer
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.Black.copy(alpha = 0.75f), RoundedCornerShape(16.dp))
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Recording toggle buttons row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = { viewModel.toggleNightMode() },
                        modifier = Modifier
                            .background(if (liveTrip.isNightMode) WarningAmber.copy(alpha = 0.2f) else CardSurfaceElevated, CircleShape)
                    ) {
                        Icon(Icons.Default.Nightlight, contentDescription = "Night Mode", tint = if (liveTrip.isNightMode) WarningAmber else TextPrimary)
                    }

                    // Main recording button
                    Button(
                        onClick = {
                            if (liveTrip.isRecording) viewModel.stopRecording() else viewModel.startRecording()
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (liveTrip.isRecording) DangerRed else SuccessGreen
                        ),
                        modifier = Modifier
                            .height(48.dp)
                            .width(180.dp)
                            .testTag("dvr_record_toggle"),
                        shape = RoundedCornerShape(24.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = if (liveTrip.isRecording) Icons.Default.Stop else Icons.Default.FiberManualRecord,
                                contentDescription = null,
                                tint = TextPrimary
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (liveTrip.isRecording) "STOP TELEMETRY" else "START HUD DVR",
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = TextPrimary
                            )
                        }
                    }

                    IconButton(
                        onClick = { viewModel.togglePiP() },
                        modifier = Modifier
                            .background(if (liveTrip.isPiPEnabled) AccentBlue.copy(alpha = 0.2f) else CardSurfaceElevated, CircleShape)
                    ) {
                        Icon(Icons.Default.DoubleArrow, contentDescription = "PiP Window", tint = if (liveTrip.isPiPEnabled) AccentBlue else TextPrimary)
                    }
                }

                Divider(color = CardSurfaceElevated)

                // ACTIVE AI PRESETS SLIDERS PANEL (zero latency controller)
                Text("AI EST. PRESETS (DRIVERS DEMO SIM)", color = AccentNeonBlue, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Eyes (EAR): ${String.format("%.2f", aiState.ear)}", color = TextPrimary, fontSize = 9.sp)
                        Slider(
                            value = aiState.ear.toFloat(),
                            onValueChange = {
                                viewModel.updateSimulatedAiValues(
                                    ear = it.toDouble(),
                                    mar = aiState.mar,
                                    pitch = aiState.headPitch
                                )
                            },
                            valueRange = 0.10f..0.45f,
                            colors = SliderDefaults.colors(
                                activeTrackColor = if (aiState.ear < 0.25) DangerRed else AccentBlue,
                                thumbColor = if (aiState.ear < 0.25) DangerRed else AccentBlue
                            )
                        )
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Text("Mouth (MAR): ${String.format("%.2f", aiState.mar)}", color = TextPrimary, fontSize = 9.sp)
                        Slider(
                            value = aiState.mar.toFloat(),
                            onValueChange = {
                                viewModel.updateSimulatedAiValues(
                                    ear = aiState.ear,
                                    mar = it.toDouble(),
                                    pitch = aiState.headPitch
                                )
                            },
                            valueRange = 0.10f..0.80f,
                            colors = SliderDefaults.colors(
                                activeTrackColor = if (aiState.mar > 0.60) WarningAmber else AccentBlue,
                                thumbColor = if (aiState.mar > 0.60) WarningAmber else AccentBlue
                            )
                        )
                    }
                }
            }
        }

        // LAUNCH COUNTDOWN takeover for Level 3 Alerts
        if (aiState.alertLevel == 3) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Red.copy(alpha = 0.85f)),
                contentAlignment = Alignment.Center
            ) {
                val sosCountdown by viewModel.sosCountdown.collectAsState()
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = "Alert Level 3",
                        tint = TextPrimary,
                        modifier = Modifier.size(96.dp)
                    )
                    Text(
                        text = "CRITICAL DROWSINESS ESTIMATED!",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        text = "Emergency trigger SMS dispatcher in:",
                        fontSize = 14.sp,
                        color = TextPrimary
                    )
                    Text(
                        text = "$sosCountdown s",
                        fontSize = 54.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary,
                        fontFamily = FontFamily.Monospace
                    )

                    Button(
                        onClick = { viewModel.cancelSOS() },
                        colors = ButtonDefaults.buttonColors(containerColor = CardSurface),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("CANCEL AUTO EMERGENCY", color = TextPrimary, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

private fun formatDuration(sec: Long): String {
    val mins = sec / 60
    val secs = sec % 60
    return String.format("%02d:%02d", mins, secs)
}
