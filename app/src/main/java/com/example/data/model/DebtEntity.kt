package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "debts")
data class DebtEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val personName: String,
    val amount: Double,
    val type: String, // "CHO_VAY" (I lent money), "DI_VAY" (I borrowed money)
    val dueDate: Long = System.currentTimeMillis() + (7L * 24 * 60 * 60 * 1000), // default 7 days
    val note: String = "",
    val isSettled: Boolean = false,
    val walletId: Long = 1L
)
