package com.example.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.model.CategoryBudgetEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CategoryBudgetDao {
    @Query("SELECT * FROM category_budgets")
    fun getAllBudgets(): Flow<List<CategoryBudgetEntity>>

    @Query("SELECT * FROM category_budgets WHERE categoryName = :categoryName LIMIT 1")
    fun getBudgetForCategory(categoryName: String): Flow<CategoryBudgetEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateBudget(budget: CategoryBudgetEntity)

    @Delete
    suspend fun deleteBudget(budget: CategoryBudgetEntity)

    @Query("DELETE FROM category_budgets")
    suspend fun clearAllBudgets()
}
