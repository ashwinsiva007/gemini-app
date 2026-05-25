package com.example.data.dao

import androidx.room.*
import com.example.data.entity.TripEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TripDao {
    @Query("SELECT * FROM trips WHERE userId = :userId ORDER BY startTime DESC")
    fun getAllTripsFlow(userId: String): Flow<List<TripEntity>>

    @Query("SELECT * FROM trips WHERE userId = :userId ORDER BY startTime DESC")
    suspend fun getAllTrips(userId: String): List<TripEntity>

    @Query("SELECT * FROM trips WHERE id = :tripId LIMIT 1")
    fun getTripByIdFlow(tripId: Long): Flow<TripEntity?>

    @Query("SELECT * FROM trips WHERE id = :tripId LIMIT 1")
    suspend fun getTripById(tripId: Long): TripEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTrip(trip: TripEntity): Long

    @Update
    suspend fun updateTrip(trip: TripEntity)

    @Query("DELETE FROM trips WHERE userId = :userId")
    suspend fun clearAllTrips(userId: String)
}
