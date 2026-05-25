package com.example.data.database

import androidx.room.TypeConverter
import com.example.data.model.EmergencyContact
import com.example.data.model.MedicalInfo
import com.example.data.model.RoutePoint
import com.example.data.model.VehicleInfo
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

class Converters {
    private val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()

    @TypeConverter
    fun fromStringList(value: String?): List<String>? {
        if (value == null) return null
        val type = Types.newParameterizedType(List::class.java, String::class.java)
        return moshi.adapter<List<String>>(type).fromJson(value)
    }

    @TypeConverter
    fun toStringList(list: List<String>?): String? {
        if (list == null) return null
        val type = Types.newParameterizedType(List::class.java, String::class.java)
        return moshi.adapter<List<String>>(type).toJson(list)
    }

    @TypeConverter
    fun fromContactList(value: String?): List<EmergencyContact>? {
        if (value == null) return null
        val type = Types.newParameterizedType(List::class.java, EmergencyContact::class.java)
        return moshi.adapter<List<EmergencyContact>>(type).fromJson(value)
    }

    @TypeConverter
    fun toContactList(list: List<EmergencyContact>?): String? {
        if (list == null) return null
        val type = Types.newParameterizedType(List::class.java, EmergencyContact::class.java)
        return moshi.adapter<List<EmergencyContact>>(type).toJson(list)
    }

    @TypeConverter
    fun fromVehicleInfo(value: String?): VehicleInfo? {
        if (value == null) return null
        return moshi.adapter(VehicleInfo::class.java).fromJson(value)
    }

    @TypeConverter
    fun toVehicleInfo(info: VehicleInfo?): String? {
        if (info == null) return null
        return moshi.adapter(VehicleInfo::class.java).toJson(info)
    }

    @TypeConverter
    fun fromMedicalInfo(value: String?): MedicalInfo? {
        if (value == null) return null
        return moshi.adapter(MedicalInfo::class.java).fromJson(value)
    }

    @TypeConverter
    fun toMedicalInfo(info: MedicalInfo?): String? {
        if (info == null) return null
        return moshi.adapter(MedicalInfo::class.java).toJson(info)
    }

    @TypeConverter
    fun fromRoutePoints(value: String?): List<RoutePoint>? {
        if (value == null) return null
        val type = Types.newParameterizedType(List::class.java, RoutePoint::class.java)
        return moshi.adapter<List<RoutePoint>>(type).fromJson(value)
    }

    @TypeConverter
    fun toRoutePoints(list: List<RoutePoint>?): String? {
        if (list == null) return null
        val type = Types.newParameterizedType(List::class.java, RoutePoint::class.java)
        return moshi.adapter<List<RoutePoint>>(type).toJson(list)
    }
}
