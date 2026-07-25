package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "recurring_bills")
data class RecurringBillEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val amount: Double,
    val category: String,
    val frequency: String = "Hàng tháng", // "Hàng tháng", "Hàng tuần", "Hàng năm"
    val nextDueDate: Long = System.currentTimeMillis() + (5L * 24 * 60 * 60 * 1000),
    val walletId: Long = 1L,
    val isPaid: Boolean = false
)
