package com.example.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.data.model.RoutePoint

@Entity(tableName = "trips")
data class TripEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userId: String,
    val startTime: Long,
    val endTime: Long,
    val distanceKm: Double,
    val route: List<RoutePoint> = emptyList(),
    val alertCount: Int = 0,
    val drivingScore: Int = 100, // Starts at 100, decreases based on warnings
    val status: String = "Safe", // Safe, Warning, Incident
    val sessionVideoUrl: String = ""
)
