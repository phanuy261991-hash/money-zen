package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "category_budgets")
data class CategoryBudgetEntity(
    @PrimaryKey
    val categoryName: String,
    val monthlyLimit: Double,
    val isExpense: Boolean = true
)
