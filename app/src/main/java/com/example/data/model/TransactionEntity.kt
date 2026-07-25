package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class TransactionType {
    INCOME,   // Thu nhập
    EXPENSE,  // Chi tiêu
    TRANSFER  // Chuyển tiền giữa các ví
}

@Entity(tableName = "transactions")
data class TransactionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val amount: Double,
    val type: String, // TransactionType.INCOME.name, EXPENSE.name, or TRANSFER.name
    val category: String, // e.g., "Ăn uống", "Di chuyển", "Chuyển khoản ví", ...
    val date: Long = System.currentTimeMillis(),
    val note: String = "",
    val paymentMethod: String = "Tiền mặt",
    val walletId: Long = 1L,
    val transferToWalletId: Long? = null
)
