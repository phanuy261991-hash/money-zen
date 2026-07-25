package com.example.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.RecurringBillEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RecurringBillDao {
    @Query("SELECT * FROM recurring_bills ORDER BY isPaid ASC, nextDueDate ASC")
    fun getAllRecurringBills(): Flow<List<RecurringBillEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBill(bill: RecurringBillEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBills(bills: List<RecurringBillEntity>)

    @Update
    suspend fun updateBill(bill: RecurringBillEntity)

    @Delete
    suspend fun deleteBill(bill: RecurringBillEntity)

    @Query("DELETE FROM recurring_bills")
    suspend fun clearAllBills()
}
