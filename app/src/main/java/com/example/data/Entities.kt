package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "reflections")
data class Reflection(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val mood: String, // GREAT, GOOD, OKAY, ANXIOUS, BAD
    val note: String,
    val tags: String, // Comma-separated list of tags
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "wellness_goals")
data class WellnessGoal(
    @PrimaryKey val id: String, // "meditation", "sleep", "hydration"
    val title: String,
    val currentValue: Int,
    val targetValue: Int,
    val unit: String
)
