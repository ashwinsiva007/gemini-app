package com.example

import android.app.Application
import com.example.data.database.AppDatabase
import com.example.data.repository.DashcamRepository

class DashcamApplication : Application() {
    val database by lazy { AppDatabase.getDatabase(this) }
    val repository by lazy {
        DashcamRepository(
            database.userDao(),
            database.tripDao(),
            database.alertDao(),
            database.incidentReportDao()
        )
    }
}
