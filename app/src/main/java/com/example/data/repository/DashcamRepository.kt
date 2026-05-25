package com.example.data.repository

import com.example.data.dao.AlertDao
import com.example.data.dao.IncidentReportDao
import com.example.data.dao.TripDao
import com.example.data.dao.UserDao
import com.example.data.entity.AlertEntity
import com.example.data.entity.IncidentReportEntity
import com.example.data.entity.TripEntity
import com.example.data.entity.UserEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class DashcamRepository(
    private val userDao: UserDao,
    private val tripDao: TripDao,
    private val alertDao: AlertDao,
    private val incidentReportDao: IncidentReportDao
) {
    // User Operations
    fun getUserFlow(email: String): Flow<UserEntity?> = userDao.getUserByEmailFlow(email)

    suspend fun getUser(email: String): UserEntity? = withContext(Dispatchers.IO) {
        userDao.getUserByEmail(email)
    }

    suspend fun saveUser(user: UserEntity) = withContext(Dispatchers.IO) {
        userDao.insertUser(user)
    }

    suspend fun deleteUser(email: String) = withContext(Dispatchers.IO) {
        userDao.deleteUser(email)
    }

    // Trip Operations
    fun getTripsFlow(userId: String): Flow<List<TripEntity>> = tripDao.getAllTripsFlow(userId)

    suspend fun getTrips(userId: String): List<TripEntity> = withContext(Dispatchers.IO) {
        tripDao.getAllTrips(userId)
    }

    suspend fun getTrip(tripId: Long): TripEntity? = withContext(Dispatchers.IO) {
        tripDao.getTripById(tripId)
    }

    suspend fun saveTrip(trip: TripEntity): Long = withContext(Dispatchers.IO) {
        tripDao.insertTrip(trip)
    }

    suspend fun updateTrip(trip: TripEntity) = withContext(Dispatchers.IO) {
        tripDao.updateTrip(trip)
    }

    suspend fun clearTrips(userId: String) = withContext(Dispatchers.IO) {
        tripDao.clearAllTrips(userId)
    }

    // Alert Operations
    fun getAlertsFlow(userId: String): Flow<List<AlertEntity>> = alertDao.getAllAlertsFlow(userId)

    suspend fun getAlerts(userId: String): List<AlertEntity> = withContext(Dispatchers.IO) {
        alertDao.getAllAlerts(userId)
    }

    fun getAlertsForTripFlow(tripId: Long): Flow<List<AlertEntity>> = alertDao.getAlertsForTripFlow(tripId)

    suspend fun getAlertsForTrip(tripId: Long): List<AlertEntity> = withContext(Dispatchers.IO) {
        alertDao.getAlertsForTrip(tripId)
    }

    suspend fun saveAlert(alert: AlertEntity): Long = withContext(Dispatchers.IO) {
        alertDao.insertAlert(alert)
    }

    suspend fun resolveAlert(alertId: Long, resolvedAt: Long = System.currentTimeMillis()) = withContext(Dispatchers.IO) {
        alertDao.resolveAlert(alertId, resolvedAt)
    }

    suspend fun clearAlerts(userId: String) = withContext(Dispatchers.IO) {
        alertDao.clearAllAlerts(userId)
    }

    // Incident Report Operations
    fun getIncidentReportsFlow(userId: String): Flow<List<IncidentReportEntity>> = incidentReportDao.getAllIncidentReportsFlow(userId)

    suspend fun getIncidentReports(userId: String): List<IncidentReportEntity> = withContext(Dispatchers.IO) {
        incidentReportDao.getAllIncidentReports(userId)
    }

    suspend fun saveIncidentReport(report: IncidentReportEntity): Long = withContext(Dispatchers.IO) {
        incidentReportDao.insertIncidentReport(report)
    }

    suspend fun updateIncidentReport(report: IncidentReportEntity) = withContext(Dispatchers.IO) {
        incidentReportDao.updateIncidentReport(report)
    }

    suspend fun clearIncidentReports(userId: String) = withContext(Dispatchers.IO) {
        incidentReportDao.clearAllIncidentReports(userId)
    }
}
