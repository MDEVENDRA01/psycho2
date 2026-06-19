package com.example.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ReflectionDao {
    @Query("SELECT * FROM reflections ORDER BY timestamp DESC")
    fun getAllReflections(): Flow<List<Reflection>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReflection(reflection: Reflection)

    @Query("DELETE FROM reflections WHERE id = :id")
    suspend fun deleteReflectionById(id: Int)
}

@Dao
interface WellnessGoalDao {
    @Query("SELECT * FROM wellness_goals")
    fun getAllGoals(): Flow<List<WellnessGoal>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGoal(goal: WellnessGoal)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGoals(goals: List<WellnessGoal>)

    @Query("UPDATE wellness_goals SET currentValue = :value WHERE id = :id")
    suspend fun updateGoalValue(id: String, value: Int)
}
