package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.DashcamApplication
import com.example.data.entity.AlertEntity
import com.example.data.entity.IncidentReportEntity
import com.example.data.entity.TripEntity
import com.example.data.entity.UserEntity
import com.example.data.model.EmergencyContact
import com.example.data.model.MedicalInfo
import com.example.data.model.RoutePoint
import com.example.data.model.VehicleInfo
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

enum class AuthScreen {
    SignIn, SignUp, ForgotPassword, OTPVerification
}

enum class NavigationScreen {
    Dashboard, LiveFeed, Trips, Alerts, Incidents, Profile, Settings
}

data class LiveTripState(
    val isRecording: Boolean = false,
    val currentTripId: Long = 0L,
    val speedKmh: Double = 0.0,
    val durationSeconds: Long = 0,
    val distanceKm: Double = 0.0,
    val currentLatitude: Double = 12.9716, // Default Bangalore
    val currentLongitude: Double = 77.5946,
    val simulatedSpeedLimit: Double = 60.0,
    val activeRoadSign: String? = null,
    val drivingScore: Int = 100,
    val alertsTriggered: Int = 0,
    val isNightMode: Boolean = false,
    val isPiPEnabled: Boolean = false
)

data class AiSimulatorState(
    val ear: Double = 0.35, // Eye Aspect Ratio
    val mar: Double = 0.15, // Mouth Aspect Ratio
    val headPitch: Double = 5.0, // Degrees
    val drowsinessTimerActive: Boolean = false,
    val alertLevel: Int = 0 // 0 = None, 1 = Mild, 2 = Warning, 3 = Danger (SOS Countdown)
)

class DashcamViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = (application as DashcamApplication).repository

    // Auth States
    private val _currentUserEmail = MutableStateFlow<String?>(null)
    val currentUserEmail: StateFlow<String?> = _currentUserEmail.asStateFlow()

    private val _authScreen = MutableStateFlow(AuthScreen.SignIn)
    val authScreen: StateFlow<AuthScreen> = _authScreen.asStateFlow()

    private val _currentScreen = MutableStateFlow(NavigationScreen.Dashboard)
    val currentScreen: StateFlow<NavigationScreen> = _currentScreen.asStateFlow()

    private val _userEntity = MutableStateFlow<UserEntity?>(null)
    val userEntity: StateFlow<UserEntity?> = _userEntity.asStateFlow()

    // Auth Form Bindings
    val registerName = MutableStateFlow("")
    val registerEmail = MutableStateFlow("")
    val registerPhone = MutableStateFlow("")
    val registerPassword = MutableStateFlow("")
    val registerConfirmPassword = MutableStateFlow("")
    val loginEmail = MutableStateFlow("")
    val loginPassword = MutableStateFlow("")
    val loginRememberMe = MutableStateFlow(true)
    
    // OTP / Password Reset states
    val otpCode = MutableStateFlow("")
    val forgotEmail = MutableStateFlow("")
    val newPassword = MutableStateFlow("")
    val authError = MutableStateFlow("")
    val authSuccessMessage = MutableStateFlow("")

    // Database Flows
    val trips: StateFlow<List<TripEntity>> = _currentUserEmail
        .flatMapLatest { email ->
            if (email != null) repository.getTripsFlow(email) else flowOf(emptyList())
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val alerts: StateFlow<List<AlertEntity>> = _currentUserEmail
        .flatMapLatest { email ->
            if (email != null) repository.getAlertsFlow(email) else flowOf(emptyList())
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val incidents: StateFlow<List<IncidentReportEntity>> = _currentUserEmail
        .flatMapLatest { email ->
            if (email != null) repository.getIncidentReportsFlow(email) else flowOf(emptyList())
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Live Recording and Simulator States
    private val _liveTrip = MutableStateFlow(LiveTripState())
    val liveTrip: StateFlow<LiveTripState> = _liveTrip.asStateFlow()

    private val _aiState = MutableStateFlow(AiSimulatorState())
    val aiState: StateFlow<AiSimulatorState> = _aiState.asStateFlow()

    // SOS State
    private val _sosCountdown = MutableStateFlow(30)
    val sosCountdown: StateFlow<Int> = _sosCountdown.asStateFlow()

    private val _sosActive = MutableStateFlow(false)
    val sosActive: StateFlow<Boolean> = _sosActive.asStateFlow()

    private var tripTickerJob: Job? = null
    private var aiSimulationJob: Job? = null
    private var sosCountdownJob: Job? = null

    init {
        // Pre-create demo login credentials to make review fully complete immediately
        viewModelScope.launch {
            val demoEmail = "demo@dashcampro.com"
            val existing = repository.getUser(demoEmail)
            if (existing == null) {
                // Prepopulate user details including dummy vehicles, emergency contacts, medical records
                val demoUser = UserEntity(
                    email = demoEmail,
                    name = "Captain Ashwin",
                    phone = "+91 98765 43210",
                    passwordHash = "password123", // bcrypt logic mocked
                    isVerified = true,
                    bloodGroup = "O+",
                    dob = "1994-08-15",
                    vehicle = VehicleInfo(
                        make = "Tesla",
                        model = "Model S Plaid",
                        year = "2024",
                        registrationNumber = "KA-03-MS-9999",
                        colorHex = "#3B82F6",
                        vehicleType = "Car",
                        insurancePolicy = "POL-1002931-D",
                        insuranceExpiry = "2027-12-31"
                    ),
                    emergencyContacts = listOf(
                        EmergencyContact(name = "Sarah Siva", relationship = "Spouse", phone = "+91 99001 12233", email = "sarah@example.com", whatsapp = "+91 99001 12233", isPrimary = true),
                        EmergencyContact(name = "Dr. Siva Rama", relationship = "Father", phone = "+91 99001 44556", email = "sivarama@doctor.com", isPrimary = false)
                    ),
                    medicalInfo = MedicalInfo(
                        knownConditions = listOf("Vision Issues"),
                        medications = "OpticDrops twice daily",
                        doctorName = "Dr. Siva Rama",
                        doctorContact = "+91 99001 44556"
                    ),
                    profileCompletion = 100,
                    createdAt = System.currentTimeMillis()
                )
                repository.saveUser(demoUser)
                prepopulateMockData(demoEmail)
            }
        }
    }

    private suspend fun prepopulateMockData(email: String) {
        val now = System.currentTimeMillis()
        val oneDay = 24 * 60 * 60 * 1000L

        // Trip 1
        val t1 = TripEntity(
            userId = email,
            startTime = now - oneDay * 3,
            endTime = now - oneDay * 3 + 1800 * 1000L,
            distanceKm = 24.5,
            drivingScore = 96,
            alertCount = 1,
            status = "Safe"
        )
        val tId1 = repository.saveTrip(t1)
        repository.saveAlert(AlertEntity(userId = email, tripId = tId1, type = "Speed Limit Exceeded", severity = "Level 1 (Mild)", latitude = 12.9716, longitude = 77.5946, timestamp = t1.startTime + 600000))

        // Trip 2
        val t2 = TripEntity(
            userId = email,
            startTime = now - oneDay * 2,
            endTime = now - oneDay * 2 + 3600 * 1000L,
            distanceKm = 48.0,
            drivingScore = 52,
            alertCount = 5,
            status = "Warning"
        )
        val tId2 = repository.saveTrip(t2)
        repository.saveAlert(AlertEntity(userId = email, tripId = tId2, type = "Drowsiness Warning", severity = "Level 2 (Moderate)", latitude = 12.9250, longitude = 77.5100, timestamp = t2.startTime + 1200000))
        repository.saveAlert(AlertEntity(userId = email, tripId = tId2, type = "Yawn Detection", severity = "Level 1 (Mild)", latitude = 12.9300, longitude = 77.5250, timestamp = t2.startTime + 1800000))
        repository.saveAlert(AlertEntity(userId = email, tripId = tId2, type = "Drowsiness Warning", severity = "Level 2 (Moderate)", latitude = 12.9400, longitude = 77.5300, timestamp = t2.startTime + 2400000))

        // Trip 3
        val t3 = TripEntity(
            userId = email,
            startTime = now - oneDay * 1,
            endTime = now - oneDay * 1 + 1200 * 1000L,
            distanceKm = 14.2,
            drivingScore = 35,
            alertCount = 4,
            status = "Incident"
        )
        val tId3 = repository.saveTrip(t3)
        val dangerAlertId = repository.saveAlert(AlertEntity(userId = email, tripId = tId3, type = "Critical Drowsiness (EAR < 0.25)", severity = "Level 3 (Critical)", latitude = 12.9600, longitude = 77.5800, timestamp = t3.startTime + 600000))
        
        // Incident Report for Trip 3
        repository.saveIncidentReport(
            IncidentReportEntity(
                userId = email,
                alertId = dangerAlertId,
                tripId = tId3,
                generatedAt = t3.startTime + 605000,
                status = "Pending",
                sharedWith = listOf("Sarah Siva")
            )
        )
    }

    // AUTH API actions
    fun setAuthScreen(screen: AuthScreen) {
        _authScreen.value = screen
        authError.value = ""
        authSuccessMessage.value = ""
    }

    fun handleSignIn() {
        authError.value = ""
        val email = loginEmail.value.trim()
        val password = loginPassword.value

        if (email.isEmpty() || password.isEmpty()) {
            authError.value = "All fields are required"
            return
        }

        viewModelScope.launch {
            val user = repository.getUser(email)
            if (user != null && user.passwordHash == password) {
                _currentUserEmail.value = email
                _userEntity.value = user
                _currentScreen.value = NavigationScreen.Dashboard
                authSuccessMessage.value = "Logged in successfully!"
            } else {
                authError.value = "Incorrect credentials. Try: demo@dashcampro.com / password123"
            }
        }
    }

    fun handleSignUp() {
        authError.value = ""
        val name = registerName.value.trim()
        val email = registerEmail.value.trim()
        val phone = registerPhone.value.trim()
        val password = registerPassword.value
        val confirm = registerConfirmPassword.value

        if (name.isEmpty() || email.isEmpty() || phone.isEmpty() || password.isEmpty()) {
            authError.value = "All fields are required"
            return
        }
        if (password != confirm) {
            authError.value = "Passwords do not match"
            return
        }
        if (password.length < 6) {
            authError.value = "Password must be at least 6 characters"
            return
        }

        viewModelScope.launch {
            val existing = repository.getUser(email)
            if (existing != null) {
                authError.value = "Email is already registered"
                return@launch
            }

            // Create user
            val newUser = UserEntity(
                email = email,
                name = name,
                phone = phone,
                passwordHash = password,
                isVerified = false, // Require OTP
                profileCompletion = 25 // base registration
            )
            repository.saveUser(newUser)
            _authScreen.value = AuthScreen.OTPVerification
            authSuccessMessage.value = "OTP Code sent to your phone!"
        }
    }

    fun verifyOTP() {
        authError.value = ""
        val code = otpCode.value.trim()
        if (code != "1234" && code != "123456") {
            authError.value = "Invalid OTP code. Enter 1234 to verify."
            return
        }

        val targetEmail = if (authScreen.value == AuthScreen.ForgotPassword) forgotEmail.value.trim() else registerEmail.value.trim()

        viewModelScope.launch {
            val user = repository.getUser(targetEmail)
            if (user != null) {
                val updated = user.copy(isVerified = true)
                repository.saveUser(updated)
                _currentUserEmail.value = targetEmail
                _userEntity.value = updated
                _currentScreen.value = NavigationScreen.Dashboard
                authSuccessMessage.value = "Account setup verified successfully!"
            } else {
                authError.value = "User not found"
            }
        }
    }

    fun sendForgotPasswordOTP() {
        authError.value = ""
        val email = forgotEmail.value.trim()
        if (email.isEmpty()) {
            authError.value = "Please enter your email"
            return
        }

        viewModelScope.launch {
            val user = repository.getUser(email)
            if (user == null) {
                authError.value = "No account found with this email"
            } else {
                _authScreen.value = AuthScreen.OTPVerification
                authSuccessMessage.value = "OTP sent to your email to reset password"
            }
        }
    }

    fun handleSignOut() {
        _currentUserEmail.value = null
        _userEntity.value = null
        _authScreen.value = AuthScreen.SignIn
        _currentScreen.value = NavigationScreen.Dashboard
        // Clear forms
        loginEmail.value = ""
        loginPassword.value = ""
        registerEmail.value = ""
        registerPassword.value = ""
    }

    fun navigateTo(screen: NavigationScreen) {
        _currentScreen.value = screen
    }

    // PROFILE UPDATE LOGIC
    fun updatePersonalProfile(name: String, dob: String, bloodGroup: String, phone: String) {
        val current = _userEntity.value ?: return
        viewModelScope.launch {
            val updated = current.copy(
                name = name,
                dob = dob,
                bloodGroup = bloodGroup,
                phone = phone
            )
            val finalUser = recalculateCompletion(updated)
            repository.saveUser(finalUser)
            _userEntity.value = finalUser
        }
    }

    fun updateVehicleProfile(vehicle: VehicleInfo) {
        val current = _userEntity.value ?: return
        viewModelScope.launch {
            val updated = current.copy(vehicle = vehicle)
            val finalUser = recalculateCompletion(updated)
            repository.saveUser(finalUser)
            _userEntity.value = finalUser
        }
    }

    fun updateMedicalProfile(medical: MedicalInfo) {
        val current = _userEntity.value ?: return
        viewModelScope.launch {
            val updated = current.copy(medicalInfo = medical)
            val finalUser = recalculateCompletion(updated)
            repository.saveUser(finalUser)
            _userEntity.value = finalUser
        }
    }

    fun addEmergencyContact(contact: EmergencyContact) {
        val current = _userEntity.value ?: return
        viewModelScope.launch {
            val updatedList = current.emergencyContacts.toMutableList()
            if (contact.isPrimary) {
                // unset other primaries
                updatedList.forEachIndexed { i, ec ->
                    updatedList[i] = ec.copy(isPrimary = false)
                }
            }
            updatedList.add(contact)
            val updated = current.copy(emergencyContacts = updatedList)
            val finalUser = recalculateCompletion(updated)
            repository.saveUser(finalUser)
            _userEntity.value = finalUser
        }
    }

    fun deleteEmergencyContact(contactId: String) {
        val current = _userEntity.value ?: return
        viewModelScope.launch {
            val updatedList = current.emergencyContacts.filter { it.id != contactId }
            val updated = current.copy(emergencyContacts = updatedList)
            val finalUser = recalculateCompletion(updated)
            repository.saveUser(finalUser)
            _userEntity.value = finalUser
        }
    }

    private fun recalculateCompletion(user: UserEntity): UserEntity {
        var score = 25 // base registration
        if (user.dob.isNotEmpty() && user.bloodGroup.isNotEmpty()) score += 25
        if (user.vehicle.registrationNumber.isNotEmpty()) score += 25
        if (user.emergencyContacts.isNotEmpty()) score += 15
        if (user.medicalInfo.knownConditions.isNotEmpty() || user.medicalInfo.medications.isNotEmpty()) score += 10
        return user.copy(profileCompletion = score.coerceIn(0, 100))
    }

    // LIVE FEED AND AI SIMULATION CONTROLS
    fun startRecording() {
        val email = _currentUserEmail.value ?: return
        if (_liveTrip.value.isRecording) return

        viewModelScope.launch {
            // Write a new trip in Room
            val newTrip = TripEntity(
                userId = email,
                startTime = System.currentTimeMillis(),
                endTime = System.currentTimeMillis(),
                distanceKm = 0.0,
                drivingScore = 100,
                status = "Safe"
            )
            val tripId = repository.saveTrip(newTrip)

            _liveTrip.value = LiveTripState(
                isRecording = true,
                currentTripId = tripId,
                speedKmh = 15.0,
                durationSeconds = 0,
                distanceKm = 0.0,
                drivingScore = 100,
                activeRoadSign = null
            )

            startTripTickers()
        }
    }

    fun stopRecording() {
        val live = _liveTrip.value
        if (!live.isRecording) return

        tripTickerJob?.cancel()
        aiSimulationJob?.cancel()

        viewModelScope.launch {
            val currentTrip = repository.getTrip(live.currentTripId)
            if (currentTrip != null) {
                val updated = currentTrip.copy(
                    endTime = System.currentTimeMillis(),
                    distanceKm = live.distanceKm,
                    drivingScore = live.drivingScore,
                    alertCount = live.alertsTriggered,
                    status = when {
                        live.drivingScore < 50 -> "Incident"
                        live.drivingScore < 80 -> "Warning"
                        else -> "Safe"
                    }
                )
                repository.updateTrip(updated)
            }

            _liveTrip.value = live.copy(isRecording = false)
            _aiState.value = AiSimulatorState() // Reset UI
        }
    }

    // Simulate different EAR and MAR values or manually trigger Alerts
    fun updateSimulatedAiValues(ear: Double, mar: Double, pitch: Double) {
        _aiState.value = _aiState.value.copy(
            ear = ear,
            mar = mar,
            headPitch = pitch
        )

        // Run automatic rules checking
        var level = 0
        if (ear < 0.25) {
            level = 2
            // If very low ear for extended period, make Level 3 Critical
            if (ear < 0.20) {
                level = 3
            }
        } else if (mar > 0.60) {
            level = 1 // Yawn
        } else if (pitch > 25.0 || pitch < -25.0) {
            level = 1 // Head drop
        }

        if (level != _aiState.value.alertLevel) {
            _aiState.value = _aiState.value.copy(alertLevel = level)
            if (level > 0) {
                triggerSimulationAlert(level)
            }
        }
    }

    fun triggerSimulationAlert(level: Int) {
        val live = _liveTrip.value
        if (!live.isRecording) return

        val email = _currentUserEmail.value ?: return

        viewModelScope.launch {
            val alertType = when (level) {
                1 -> "Yawn Detection / Head Drop"
                2 -> "Moderate Drowsiness Warning"
                else -> "Critical Drowsiness (EAR < 0.20)"
            }
            val severity = when (level) {
                1 -> "Level 1 (Mild)"
                2 -> "Level 2 (Moderate)"
                else -> "Level 3 (Critical)"
            }

            // Decrement driving score
            val deduction = when (level) {
                1 -> 5
                2 -> 15
                else -> 30
            }

            val newScore = (live.drivingScore - deduction).coerceIn(0, 100)
            _liveTrip.value = live.copy(
                drivingScore = newScore,
                alertsTriggered = live.alertsTriggered + 1
            )

            val alertId = repository.saveAlert(
                AlertEntity(
                    userId = email,
                    tripId = live.currentTripId,
                    type = alertType,
                    severity = severity,
                    latitude = live.currentLatitude,
                    longitude = live.currentLongitude
                )
            )

            if (level == 3) {
                // Level 3 triggers SOS Countdown
                startSosCountdown()
                // Save Incident Report
                repository.saveIncidentReport(
                    IncidentReportEntity(
                        userId = email,
                        alertId = alertId,
                        tripId = live.currentTripId,
                        status = "Pending"
                    )
                )
            }
        }
    }

    private fun startTripTickers() {
        tripTickerJob?.cancel()
        tripTickerJob = viewModelScope.launch {
            while (true) {
                delay(1000)
                val live = _liveTrip.value
                val newDuration = live.durationSeconds + 1
                // Add distance slowly based on speed
                val addedDistance = (live.speedKmh / 3600.0)
                val newDistance = live.distanceKm + addedDistance

                // Jitter speed, lat/long slightly for realistic feed
                val newSpeed = (live.speedKmh + (Math.random() * 4 - 2)).coerceIn(10.0, 110.0)
                val newLat = live.currentLatitude + (Math.random() * 0.0001 - 0.00005)
                val newLng = live.currentLongitude + (Math.random() * 0.0001 - 0.00005)

                _liveTrip.value = live.copy(
                    durationSeconds = newDuration,
                    distanceKm = newDistance,
                    speedKmh = newSpeed,
                    currentLatitude = newLat,
                    currentLongitude = newLng
                )
            }
        }

        aiSimulationJob?.cancel()
        aiSimulationJob = viewModelScope.launch {
            // Periodically cycle road signs or simulate alerts if the user leaves it in autoplay mode
            val signs = listOf("STOP", "Speed Limit 80", "Speed Limit 40", "Traffic Light: Red", "Traffic Light: Green", null)
            var idx = 0
            while (true) {
                delay(12000)
                val live = _liveTrip.value
                if (live.isRecording) {
                    val nextSign = signs[idx % signs.size]
                    _liveTrip.value = live.copy(activeRoadSign = nextSign)
                    idx++

                    // If STOP sign visible and speed > 5 km/h, prompt alert
                    if (nextSign == "STOP" && live.speedKmh > 5.0) {
                        triggerSimulatedIncident("Sign Trespass: Ran STOP Sign", "Level 2 (Moderate)")
                    }
                    // If speed exceeds limit
                    if (nextSign == "Speed Limit 40" && live.speedKmh > 40.0) {
                        triggerSimulatedIncident("Speed Limit Exceeded (Speeding)", "Level 1 (Mild)")
                    }
                }
            }
        }
    }

    fun triggerSimulatedIncident(type: String, severity: String) {
        val live = _liveTrip.value
        val email = _currentUserEmail.value ?: return
        if (!live.isRecording) return

        viewModelScope.launch {
            val deduction = when {
                severity.contains("3") -> 30
                severity.contains("2") -> 15
                else -> 5
            }
            _liveTrip.value = live.copy(
                drivingScore = (live.drivingScore - deduction).coerceIn(0, 100),
                alertsTriggered = live.alertsTriggered + 1
            )
            repository.saveAlert(
                AlertEntity(
                    userId = email,
                    tripId = live.currentTripId,
                    type = type,
                    severity = severity,
                    latitude = live.currentLatitude,
                    longitude = live.currentLongitude
                )
            )
        }
    }

    // Toggle Camera features
    fun toggleNightMode() {
        _liveTrip.value = _liveTrip.value.copy(
            isNightMode = !_liveTrip.value.isNightMode
        )
    }

    fun togglePiP() {
        _liveTrip.value = _liveTrip.value.copy(
            isPiPEnabled = !_liveTrip.value.isPiPEnabled
        )
    }

    // SOS countdown routines
    private fun startSosCountdown() {
        _sosCountdown.value = 30
        _sosActive.value = true
        sosCountdownJob?.cancel()
        sosCountdownJob = viewModelScope.launch {
            while (_sosCountdown.value > 0) {
                delay(1000)
                _sosCountdown.value = _sosCountdown.value - 1
            }
            // Trigger actual complete SOS
            triggerEmergencySOSNow()
        }
    }

    fun triggerEmergencySOSNow() {
        sosCountdownJob?.cancel()
        _sosCountdown.value = 0
        _sosActive.value = true
        
        // Log manual/auto SOS Alert in database
        val live = _liveTrip.value
        val email = _currentUserEmail.value ?: return
        viewModelScope.launch {
            repository.saveAlert(
                AlertEntity(
                    userId = email,
                    tripId = live.currentTripId,
                    type = "EMERGENCY: SOS Triggered",
                    severity = "Level 3 (Critical)",
                    latitude = live.currentLatitude,
                    longitude = live.currentLongitude
                )
            )
        }
    }

    fun cancelSOS() {
        sosCountdownJob?.cancel()
        _sosActive.value = false
        _sosCountdown.value = 30
        // Clean up alert state
        _aiState.value = _aiState.value.copy(alertLevel = 0)
    }

    // RESOLVE ALERTS & SETTINGS CLK
    fun resolveAlert(alertId: Long) {
        viewModelScope.launch {
            repository.resolveAlert(alertId)
        }
    }

    fun clearAllUserData() {
        val email = _currentUserEmail.value ?: return
        viewModelScope.launch {
            repository.clearTrips(email)
            repository.clearAlerts(email)
            repository.clearIncidentReports(email)
        }
    }

    // Clean up
    override fun onCleared() {
        super.onCleared()
        tripTickerJob?.cancel()
        aiSimulationJob?.cancel()
        sosCountdownJob?.cancel()
    }
}
