package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "savings_goals")
data class SavingsGoalEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val targetAmount: Double,
    val currentAmount: Double = 0.0,
    val targetDate: Long = System.currentTimeMillis() + (30L * 24 * 60 * 60 * 1000), // 30 days
    val colorHex: String = "#3B82F6"
)
