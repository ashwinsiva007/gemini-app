package com.example.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "incidents")
data class IncidentReportEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userId: String,
    val alertId: Long,
    val tripId: Long,
    val generatedAt: Long = System.currentTimeMillis(),
    val pdfPath: String = "",
    val sharedWith: List<String> = emptyList(), // shared contacts list
    val status: String = "Pending" // Pending, Shared, Resolved
)
