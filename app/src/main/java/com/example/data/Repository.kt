package com.example.data

import kotlinx.coroutines.flow.Flow

class SerenityRepository(
    private val reflectionDao: ReflectionDao,
    private val wellnessGoalDao: WellnessGoalDao
) {
    val allReflections: Flow<List<Reflection>> = reflectionDao.getAllReflections()
    val allGoals: Flow<List<WellnessGoal>> = wellnessGoalDao.getAllGoals()

    suspend fun insertReflection(reflection: Reflection) {
        reflectionDao.insertReflection(reflection)
    }

    suspend fun deleteReflection(id: Int) {
        reflectionDao.deleteReflectionById(id)
    }

    suspend fun insertGoal(goal: WellnessGoal) {
        wellnessGoalDao.insertGoal(goal)
    }

    suspend fun updateGoalValue(id: String, value: Int) {
        wellnessGoalDao.updateGoalValue(id, value)
    }
}
