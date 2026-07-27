package com.example.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.dao.CategoryBudgetDao
import com.example.data.dao.DebtDao
import com.example.data.dao.RecurringBillDao
import com.example.data.dao.SavingsGoalDao
import com.example.data.dao.TransactionDao
import com.example.data.dao.WalletDao
import com.example.data.model.CategoryBudgetEntity
import com.example.data.model.DebtEntity
import com.example.data.model.RecurringBillEntity
import com.example.data.model.SavingsGoalEntity
import com.example.data.model.TransactionEntity
import com.example.data.model.WalletEntity

@Database(
    entities = [
        TransactionEntity::class,
        CategoryBudgetEntity::class,
        WalletEntity::class,
        DebtEntity::class,
        SavingsGoalEntity::class,
        RecurringBillEntity::class
    ],
    version = 3,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun transactionDao(): TransactionDao
    abstract fun categoryBudgetDao(): CategoryBudgetDao
    abstract fun walletDao(): WalletDao
    abstract fun debtDao(): DebtDao
    abstract fun savingsGoalDao(): SavingsGoalDao
    abstract fun recurringBillDao(): RecurringBillDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "personal_finance_local.db"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
