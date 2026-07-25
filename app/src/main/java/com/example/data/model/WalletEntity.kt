package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "wallets")
data class WalletEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val type: String, // "TIEN_MAT", "NGAN_HANG", "VI_DIEN_TU", "THE_TIN_DUNG"
    val balance: Double,
    val iconName: String = "AccountBalanceWallet",
    val colorHex: String = "#10B981"
)
