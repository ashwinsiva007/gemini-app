package com.example.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.data.model.EmergencyContact
import com.example.data.model.MedicalInfo
import com.example.data.model.VehicleInfo

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val email: String, // email is the primary identifier
    val name: String,
    val phone: String = "",
    val passwordHash: String = "",
    val isVerified: Boolean = false,
    val bloodGroup: String = "",
    val dob: String = "",
    val vehicle: VehicleInfo = VehicleInfo(),
    val emergencyContacts: List<EmergencyContact> = emptyList(),
    val medicalInfo: MedicalInfo = MedicalInfo(),
    val profileCompletion: Int = 0,
    val createdAt: Long = System.currentTimeMillis()
)
