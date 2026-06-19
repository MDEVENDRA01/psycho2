package com.example

import android.app.Application
import com.example.data.AppDatabase
import com.example.data.SerenityRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

class SerenityApplication : Application() {
    // No need to cancel this scope, it'll live as long as the application process
    val applicationScope = CoroutineScope(SupervisorJob())

    // Using by lazy so the database and the repository are only created when they're needed
    // rather than when the application starts
    val database by lazy { AppDatabase.getDatabase(this, applicationScope) }
    val repository by lazy { SerenityRepository(database.reflectionDao(), database.wellnessGoalDao()) }
}
