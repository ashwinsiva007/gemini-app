package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.screens.*
import com.example.ui.theme.*
import com.example.ui.viewmodel.DashcamViewModel
import com.example.ui.viewmodel.NavigationScreen
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                val viewModel: DashcamViewModel = viewModel()
                MainAppEntry(viewModel)
            }
        }
    }
}

@Composable
fun MainAppEntry(viewModel: DashcamViewModel) {
    val currentUserEmail by viewModel.currentUserEmail.collectAsState()

    if (currentUserEmail == null) {
        AuthScreenView(viewModel = viewModel)
    } else {
        DcpDashboardSkeleton(viewModel = viewModel)
    }
}

@Composable
fun DcpDashboardSkeleton(viewModel: DashcamViewModel) {
    val currentScreen by viewModel.currentScreen.collectAsState()
    val liveTrip by viewModel.liveTrip.collectAsState()
    val isSosActive by viewModel.sosActive.collectAsState()
    
    val configuration = LocalConfiguration.current
    val isCompact = configuration.screenWidthDp < 768

    var showUserMenu by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundDeep)
    ) {
        // Main responsive horizontal layout splits (Adaptive design rail)
        Row(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.safeDrawing)
        ) {
            if (!isCompact) {
                // SIDEBAR COMPONENT (Shown on mid-to large screens for luxury telemetry control)
                SidebarNav(
                    currentScreen = currentScreen,
                    onNavigate = { viewModel.navigateTo(it) },
                    onLogout = { viewModel.handleSignOut() }
                )
            }

            // Content Column Area
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
            ) {
                // TOP NAVBAR indicator stats
                TopNavbar(
                    viewModel = viewModel,
                    isCompact = isCompact,
                    onAvatarClick = { showUserMenu = !showUserMenu }
                )

                Divider(color = CardSurfaceElevated)

                // Content panels router
                Box(
                    modifier = Modifier.weight(1f)
                ) {
                    when (currentScreen) {
                        NavigationScreen.Dashboard -> DashboardScreenView(viewModel = viewModel)
                        NavigationScreen.LiveFeed -> LiveFeedScreenView(viewModel = viewModel)
                        NavigationScreen.Trips -> TripsScreenView(viewModel = viewModel)
                        NavigationScreen.Alerts -> AlertsScreenView(viewModel = viewModel)
                        NavigationScreen.Incidents -> IncidentsScreenView(viewModel = viewModel)
                        NavigationScreen.Profile -> ProfileScreenView(viewModel = viewModel)
                        NavigationScreen.Settings -> SettingsScreenView(viewModel = viewModel)
                    }
                }

                if (isCompact) {
                    // Modern High-Tech Bottom Navigation Bar for Mobile
                    DcpBottomNavBar(
                        currentScreen = currentScreen,
                        onNavigate = { viewModel.navigateTo(it) },
                        viewModel = viewModel
                    )
                }
            }
        }

        // Pulse taking Emergency SOS overlay screen takeover
        if (isSosActive) {
            val countdown by viewModel.sosCountdown.collectAsState()
            if (countdown > 0) {
                // Countdown is handled locally inside screens or globally. We keep them synced
            } else {
                FullscreenSosTakeover(onCancel = { viewModel.cancelSOS() })
            }
        }

        // Simple overlay dropdown menu items
        if (showUserMenu) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clickable { showUserMenu = false }
            ) {
                Card(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(top = 70.dp, end = 24.dp)
                        .width(180.dp)
                        .border(1.dp, CardSurfaceElevated, RoundedCornerShape(12.dp)),
                    colors = CardDefaults.cardColors(containerColor = CardSurface)
                ) {
                    Column(modifier = Modifier.padding(8.dp)) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    viewModel.navigateTo(NavigationScreen.Profile)
                                    showUserMenu = false
                                }
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Person, contentDescription = null, tint = AccentBlue, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(10.dp))
                            Text("My Profile", fontSize = 12.sp, color = TextPrimary)
                        }

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    viewModel.navigateTo(NavigationScreen.Settings)
                                    showUserMenu = false
                                }
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Settings, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(10.dp))
                            Text("Settings", fontSize = 12.sp, color = TextPrimary)
                        }

                        Divider(color = CardSurfaceElevated, modifier = Modifier.padding(vertical = 4.dp))

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    viewModel.handleSignOut()
                                    showUserMenu = false
                                }
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Logout, contentDescription = null, tint = DangerRed, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(10.dp))
                            Text("Log Out", fontSize = 12.sp, color = DangerRed, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SidebarNav(
    currentScreen: NavigationScreen,
    onNavigate: (NavigationScreen) -> Unit,
    onLogout: () -> Unit
) {
    Column(
        modifier = Modifier
            .width(230.dp)
            .fillMaxHeight()
            .background(CardSurface)
            .border(width = (0.5).dp, color = CardSurfaceElevated)
            .padding(16.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(24.dp)) {
            // Dashcam Pro Specialty Label Logo
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 12.dp)
            ) {
                Icon(Icons.Default.CompassCalibration, contentDescription = "DPC", tint = AccentBlue, modifier = Modifier.size(24.dp))
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "DASHCAM PRO",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Black,
                    color = TextPrimary,
                    fontFamily = FontFamily.Monospace,
                    letterSpacing = 1.sp
                )
            }

            // Menu Items column
            val items = listOf(
                Triple(NavigationScreen.Dashboard, Icons.Default.Dashboard, "DASHBOARD"),
                Triple(NavigationScreen.LiveFeed, Icons.Default.Videocam, "LIVE FEED HUD"),
                Triple(NavigationScreen.Trips, Icons.Default.Map, "TRIP CHRONO"),
                Triple(NavigationScreen.Incidents, Icons.Default.Assignment, "INCIDENT BRIEF"),
                Triple(NavigationScreen.Alerts, Icons.Default.NotificationsActive, "ALERT MATRIX"),
                Triple(NavigationScreen.Profile, Icons.Default.ManageAccounts, "PILOT PROFILE"),
                Triple(NavigationScreen.Settings, Icons.Default.RoomPreferences, "SYS CONFIG")
            )

            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                items.forEach { (screen, icon, label) ->
                    val isSelected = currentScreen == screen
                    val bg = if (isSelected) AccentBlue.copy(alpha = 0.15f) else Color.Transparent
                    val borderCol = if (isSelected) AccentBlue else Color.Transparent
                    val tc = if (isSelected) AccentNeonBlue else TextSecondary

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(6.dp))
                            .background(bg)
                            .border(1.dp, borderCol, RoundedCornerShape(6.dp))
                            .clickable { onNavigate(screen) }
                            .padding(horizontal = 12.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = label,
                            tint = if (isSelected) AccentNeonBlue else TextSecondary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = label,
                            color = tc,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            letterSpacing = 0.5.sp
                        )
                    }
                }
            }
        }

        // Bottom log out info
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .clickable { onLogout() }
                .padding(vertical = 12.dp, horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.Logout, contentDescription = "Log out", tint = DangerRed, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = "DISCONNECT SYS",
                color = DangerRed,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                letterSpacing = 0.5.sp
            )
        }
    }
}

@Composable
fun TopNavbar(
    viewModel: DashcamViewModel,
    isCompact: Boolean,
    onAvatarClick: () -> Unit
) {
    val liveTrip by viewModel.liveTrip.collectAsState()
    val alerts by viewModel.alerts.collectAsState()

    var activeLiveTime by remember { mutableStateOf("") }
    LaunchedEffect(Unit) {
        while (true) {
            val cal = Calendar.getInstance()
            activeLiveTime = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(cal.time)
            delay(1000)
        }
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(if (isCompact) 56.dp else 64.dp)
            .background(CardSurface)
            .border(width = (0.5).dp, color = CardSurfaceElevated)
            .padding(horizontal = if (isCompact) 10.dp else 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // LEFT portion: DC/DP Badge and Specialist style labels
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(if (isCompact) 6.dp else 10.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(if (isCompact) 28.dp else 32.dp)
                    .background(AccentBlue, RoundedCornerShape(6.dp))
                    .border(1.dp, AccentNeonBlue.copy(alpha = 0.5f), RoundedCornerShape(6.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "DC",
                    fontSize = if (isCompact) 10.sp else 12.sp,
                    fontWeight = FontWeight.Black,
                    color = TextPrimary,
                    fontFamily = FontFamily.Monospace,
                    letterSpacing = (-0.5).sp
                )
            }
            Column {
                Text(
                    text = "Dashcam Pro",
                    fontSize = if (isCompact) 11.sp else 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                    fontFamily = FontFamily.Monospace,
                    letterSpacing = (-0.2).sp
                )
                Text(
                    text = if (liveTrip.isRecording) "ACTIVE_01" else "STANDBY",
                    fontSize = 8.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (liveTrip.isRecording) SuccessGreen else AccentNeonBlue,
                    fontFamily = FontFamily.Monospace
                )
            }
        }

        // MIDDLE: GPS Telemetry HUD (Shown on tablet / widescreen only)
        if (!isCompact) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .background(Color.Black.copy(alpha = 0.4f), RoundedCornerShape(6.dp))
                    .border(1.dp, CardSurfaceElevated, RoundedCornerShape(6.dp))
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(Icons.Default.GpsFixed, contentDescription = "GPS Status", tint = if (liveTrip.isRecording) SuccessGreen else TextSecondary, modifier = Modifier.size(10.dp))
                Text(
                    text = if (liveTrip.isRecording) "GPS: ${String.format(Locale.US, "%.4f", liveTrip.currentLatitude)}, ${String.format(Locale.US, "%.4f", liveTrip.currentLongitude)}" else "GPS: STANDBY",
                    color = TextSecondary,
                    fontSize = 9.sp,
                    fontFamily = FontFamily.Monospace
                )
            }
        }

        // RIGHT: Clock, ONLINE, SOS, Alerts, Avatar
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(if (isCompact) 8.dp else 12.dp)
        ) {
            // Live clock & Status HUD
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = activeLiveTime,
                    color = TextPrimary,
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    lineHeight = 11.sp
                )
                Text(
                    text = "ONLINE",
                    color = SuccessGreen,
                    fontSize = 8.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    lineHeight = 8.sp
                )
            }

            // PULSING SOS BUTTON
            Button(
                onClick = { viewModel.triggerEmergencySOSNow() },
                colors = ButtonDefaults.buttonColors(containerColor = DangerRed),
                shape = RoundedCornerShape(6.dp),
                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                modifier = Modifier
                    .height(28.dp)
                    .testTag("sos_navbar_button")
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(Icons.Default.Sos, contentDescription = "SOS EMERGENCY", tint = Color.White, modifier = Modifier.size(12.dp))
                    Text("SOS", color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                }
            }

            // Only show alerts and notifications avatar on non-compact screen (mobile uses tab layout in bottom bar)
            if (!isCompact) {
                // Notification Bell with Badge
                Box {
                    IconButton(
                        onClick = { viewModel.navigateTo(NavigationScreen.Alerts) },
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(Icons.Default.Notifications, contentDescription = "Alerts notifications", tint = TextPrimary, modifier = Modifier.size(18.dp))
                    }
                    val unresolvedCount = alerts.count { !it.resolved }
                    if (unresolvedCount > 0) {
                        Box(
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .size(14.dp)
                                .background(DangerRed, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = unresolvedCount.toString(),
                                color = Color.White,
                                fontSize = 8.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }
                }

                // User dropdown avatar
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(AccentBlue.copy(alpha = 0.2f))
                        .border(1.dp, AccentBlue, CircleShape)
                        .clickable { onAvatarClick() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Person, contentDescription = "User settings list", tint = AccentNeonBlue, modifier = Modifier.size(16.dp))
                }
            }
        }
    }
}

@Composable
fun DcpBottomNavBar(
    currentScreen: NavigationScreen,
    onNavigate: (NavigationScreen) -> Unit,
    viewModel: DashcamViewModel
) {
    val alerts by viewModel.alerts.collectAsState()
    val unresolvedCount = alerts.count { !it.resolved }

    val items = listOf(
        Triple(NavigationScreen.Dashboard, Icons.Default.Dashboard, "HUD"),
        Triple(NavigationScreen.LiveFeed, Icons.Default.Videocam, "LIVE FEED"),
        Triple(NavigationScreen.Trips, Icons.Default.Map, "CHRONO"),
        Triple(NavigationScreen.Alerts, Icons.Default.NotificationsActive, "ALERTS"),
        Triple(NavigationScreen.Settings, Icons.Default.Settings, "CONFIG")
    )

    NavigationBar(
        containerColor = CardSurface,
        tonalElevation = 0.dp,
        modifier = Modifier
            .fillMaxWidth()
            .border(width = (0.5).dp, color = CardSurfaceElevated)
            .windowInsetsPadding(WindowInsets.navigationBars)
    ) {
        items.forEach { (screen, icon, label) ->
            val isSelected = currentScreen == screen || 
                (screen == NavigationScreen.Settings && currentScreen == NavigationScreen.Profile) ||
                (screen == NavigationScreen.Trips && currentScreen == NavigationScreen.Incidents)

            NavigationBarItem(
                selected = isSelected,
                onClick = { onNavigate(screen) },
                icon = {
                    Box {
                        Icon(
                            imageVector = icon,
                            contentDescription = label,
                            modifier = Modifier.size(20.dp)
                        )
                        if (screen == NavigationScreen.Alerts && unresolvedCount > 0) {
                            Box(
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .offset(x = 8.dp, y = (-4).dp)
                                    .size(12.dp)
                                    .background(DangerRed, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = unresolvedCount.toString(),
                                    color = Color.White,
                                    fontSize = 7.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                        }
                    }
                },
                label = {
                    Text(
                        text = label,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        letterSpacing = (-0.2).sp
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = AccentNeonBlue,
                    selectedTextColor = AccentNeonBlue,
                    indicatorColor = AccentBlue.copy(alpha = 0.2f),
                    unselectedIconColor = TextSecondary,
                    unselectedTextColor = TextSecondary
                )
            )
        }
    }
}

@Composable
fun FullscreenSosTakeover(onCancel: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Red.copy(alpha = 0.95f)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Icon(Icons.Default.FlashOn, contentDescription = "SOS Triggered", tint = Color.White, modifier = Modifier.size(120.dp))
            Text(
                text = "EMERGENCY SOS DISPATCH TRIGGERED!",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                textAlign = TextAlign.Center
            )
            Text(
                text = "Automatic SMS & emergency medical dossiers with GPS location\nhave been transmitted to Sarah Siva and designated doctor s lines.",
                fontSize = 13.sp,
                color = Color.White.copy(alpha = 0.9f),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 24.dp)
            )

            Button(
                onClick = onCancel,
                colors = ButtonDefaults.buttonColors(containerColor = BackgroundDeep),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .padding(top = 16.dp)
                    .height(48.dp)
                    .border(1.dp, Color.White, RoundedCornerShape(12.dp))
                    .testTag("sos_cancel_button")
            ) {
                Text("DEACTIVATE SOS & NOTIFY TARGETS", color = Color.White, fontWeight = FontWeight.Bold)
            }
        }
    }
}
