package com.example.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.data.dao.AlertDao
import com.example.data.dao.IncidentReportDao
import com.example.data.dao.TripDao
import com.example.data.dao.UserDao
import com.example.data.entity.AlertEntity
import com.example.data.entity.IncidentReportEntity
import com.example.data.entity.TripEntity
import com.example.data.entity.UserEntity

@Database(
    entities = [
        UserEntity::class,
        TripEntity::class,
        AlertEntity::class,
        IncidentReportEntity::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun tripDao(): TripDao
    abstract fun alertDao(): AlertDao
    abstract fun incidentReportDao(): IncidentReportDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "dashcam_pro_database"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
