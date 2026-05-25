package com.example.data.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class EmergencyContact(
    val id: String = java.util.UUID.randomUUID().toString(),
    val name: String = "",
    val relationship: String = "",
    val phone: String = "",
    val email: String = "",
    val whatsapp: String = "",
    val isPrimary: Boolean = false
)

@JsonClass(generateAdapter = true)
data class VehicleInfo(
    val make: String = "",
    val model: String = "",
    val year: String = "",
    val registrationNumber: String = "",
    val colorHex: String = "#3B82F6",
    val vehicleType: String = "Car", // Car, Truck, Bus, Auto
    val insurancePolicy: String = "",
    val insuranceExpiry: String = ""
)

@JsonClass(generateAdapter = true)
data class MedicalInfo(
    val knownConditions: List<String> = emptyList(), // Diabetes, Epilepsy, Heart Condition, Vision Issues, None
    val medications: String = "",
    val doctorName: String = "",
    val doctorContact: String = ""
)

@JsonClass(generateAdapter = true)
data class RoutePoint(
    val latitude: Double,
    val longitude: Double,
    val timestamp: Long
)
