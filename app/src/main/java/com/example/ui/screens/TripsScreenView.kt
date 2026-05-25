package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.entity.TripEntity
import com.example.ui.theme.*
import com.example.ui.viewmodel.DashcamViewModel

@Composable
fun TripsScreenView(viewModel: DashcamViewModel) {
    val trips by viewModel.trips.collectAsState()
    var searchFilter by remember { mutableStateOf("") }
    var scoreLimit by remember { mutableFloatStateOf(0f) }
    var selectedTripForDetail by remember { mutableStateOf<TripEntity?>(null) }

    val filteredTrips = trips.filter {
        (searchFilter.isEmpty() || it.status.contains(searchFilter, ignoreCase = true)) &&
        it.drivingScore >= scoreLimit
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
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Trip Telemetry Logs",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Text(
                    text = "Filter and replay historic speed files",
                    fontSize = 12.sp,
                    color = TextSecondary
                )
            }
        }

        // Search options filters
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(
                value = searchFilter,
                onValueChange = { searchFilter = it },
                label = { Text("Filter Status (Safe/Warning)", fontSize = 11.sp) },
                leadingIcon = { Icon(Icons.Default.Search, "Search", modifier = Modifier.size(16.dp)) },
                modifier = Modifier.weight(1f),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary,
                    focusedBorderColor = AccentBlue,
                    unfocusedBorderColor = CardSurfaceElevated
                ),
                singleLine = true
            )

            IconButton(
                onClick = { searchFilter = ""; scoreLimit = 0f },
                modifier = Modifier
                    .background(CardSurface, RoundedCornerShape(12.dp))
                    .border(1.dp, CardSurfaceElevated, RoundedCornerShape(12.dp))
                    .size(48.dp)
            ) {
                Icon(Icons.Default.FilterList, contentDescription = "Clear Filters", tint = AccentNeonBlue)
            }
        }

        // Score boundary slider
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(CardSurface, RoundedCornerShape(12.dp))
                .border(1.dp, CardSurfaceElevated, RoundedCornerShape(12.dp))
                .padding(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Min Driving Score threshold", color = TextSecondary, fontSize = 11.sp)
                Text("${scoreLimit.toInt()}+ pts", color = AccentBlue, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
            Slider(
                value = scoreLimit,
                onValueChange = { scoreLimit = it },
                valueRange = 0f..100f,
                colors = SliderDefaults.colors(
                    activeTrackColor = AccentBlue,
                    thumbColor = AccentBlue
                )
            )
        }

        // Logs
        if (filteredTrips.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.CalendarMonth, contentDescription = "Empty", tint = TextSecondary, modifier = Modifier.size(48.dp))
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("No trip logs found", color = TextPrimary, fontSize = 14.sp)
                    Text("Try modifying search criteria", color = TextSecondary, fontSize = 11.sp)
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(filteredTrips) { trip ->
                    TripRowItem(trip = trip, onClick = { selectedTripForDetail = trip })
                }
            }
        }
    }

    selectedTripForDetail?.let { trip ->
        TripDetailModal(trip = trip, onDismiss = { selectedTripForDetail = null })
    }
}
