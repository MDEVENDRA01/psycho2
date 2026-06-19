package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(entities = [Reflection::class, WellnessGoal::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun reflectionDao(): ReflectionDao
    abstract fun wellnessGoalDao(): WellnessGoalDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "serenity_path_database"
                )
                .addCallback(AppDatabaseCallback(scope))
                .build()
                INSTANCE = instance
                instance
            }
        }
    }

    private class AppDatabaseCallback(
        private val scope: CoroutineScope
    ) : RoomDatabase.Callback() {

        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                scope.launch(Dispatchers.IO) {
                    populateDb(database.reflectionDao(), database.wellnessGoalDao())
                }
            }
        }

        suspend fun populateDb(reflectionDao: ReflectionDao, wellnessGoalDao: WellnessGoalDao) {
            // Prepopulate wellness goals
            val initialGoals = listOf(
                WellnessGoal("meditation", "Meditation", 3, 5, "5 days"),
                WellnessGoal("sleep", "Sleep (8h)", 5, 7, "7 days"),
                WellnessGoal("hydration", "Hydration", 1, 1, "Daily")
            )
            wellnessGoalDao.insertGoals(initialGoals)

            // Prepopulate reflections
            val now = System.currentTimeMillis()
            val dayInMillis = 24 * 60 * 60 * 1000L

            reflectionDao.insertReflection(
                Reflection(
                    mood = "GOOD",
                    note = "Felt a bit overwhelmed after class today, but taking a 10-minute walk really helped clear my head. Need to remember to breathe when things pile up.",
                    tags = "Anxiety,Coping",
                    timestamp = now - 20 * 60 * 60 * 1000L // 20 hours ago
                )
            )

            reflectionDao.insertReflection(
                Reflection(
                    mood = "GREAT",
                    note = "Slept much better last night. Woke up feeling somewhat rested. The meditation before bed seems to be making a small difference in how quickly I fall asleep.",
                    tags = "Sleep",
                    timestamp = now - 2 * dayInMillis + 5 * 60 * 60 * 1000L // Tuesday morning approx context
                )
            )
        }
    }
}
