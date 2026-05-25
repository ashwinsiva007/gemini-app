package com.example.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "alerts")
data class AlertEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userId: String,
    val tripId: Long,
    val type: String, // Drowsiness, Speed Limit, Sign Trespass, Lane Departure, G-sensor, Manual SOS, Low Battery
    val severity: String, // Level 1 (Mild), Level 2 (Moderate), Level 3 (Critical)
    val timestamp: Long = System.currentTimeMillis(),
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val videoClipUrl: String = "",
    val resolved: Boolean = false,
    val resolvedAt: Long = 0
)
