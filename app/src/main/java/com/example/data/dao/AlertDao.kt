package com.example.data.dao

import androidx.room.*
import com.example.data.entity.AlertEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AlertDao {
    @Query("SELECT * FROM alerts WHERE userId = :userId ORDER BY timestamp DESC")
    fun getAllAlertsFlow(userId: String): Flow<List<AlertEntity>>

    @Query("SELECT * FROM alerts WHERE userId = :userId ORDER BY timestamp DESC")
    suspend fun getAllAlerts(userId: String): List<AlertEntity>

    @Query("SELECT * FROM alerts WHERE tripId = :tripId ORDER BY timestamp DESC")
    fun getAlertsForTripFlow(tripId: Long): Flow<List<AlertEntity>>

    @Query("SELECT * FROM alerts WHERE tripId = :tripId ORDER BY timestamp DESC")
    suspend fun getAlertsForTrip(tripId: Long): List<AlertEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAlert(alert: AlertEntity): Long

    @Query("UPDATE alerts SET resolved = 1, resolvedAt = :resolvedAt WHERE id = :alertId")
    suspend fun resolveAlert(alertId: Long, resolvedAt: Long)

    @Query("DELETE FROM alerts WHERE userId = :userId")
    suspend fun clearAllAlerts(userId: String)
}
