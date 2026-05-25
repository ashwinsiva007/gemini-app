package com.example.data.dao

import androidx.room.*
import com.example.data.entity.IncidentReportEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface IncidentReportDao {
    @Query("SELECT * FROM incidents WHERE userId = :userId ORDER BY generatedAt DESC")
    fun getAllIncidentReportsFlow(userId: String): Flow<List<IncidentReportEntity>>

    @Query("SELECT * FROM incidents WHERE userId = :userId ORDER BY generatedAt DESC")
    suspend fun getAllIncidentReports(userId: String): List<IncidentReportEntity>

    @Query("SELECT * FROM incidents WHERE id = :id LIMIT 1")
    fun getIncidentReportByIdFlow(id: Long): Flow<IncidentReportEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertIncidentReport(report: IncidentReportEntity): Long

    @Update
    suspend fun updateIncidentReport(report: IncidentReportEntity)

    @Query("DELETE FROM incidents WHERE userId = :userId")
    suspend fun clearAllIncidentReports(userId: String)
}
