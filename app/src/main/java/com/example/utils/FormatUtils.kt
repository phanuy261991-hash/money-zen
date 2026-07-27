package com.example.utils

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object FormatUtils {
    var currentCurrency: String by mutableStateOf("VND")
    var currentLanguage: String by mutableStateOf("EN")

    @Synchronized
    fun formatCurrency(amount: Double, currency: String = currentCurrency): String {
        return try {
            if (currency == "USD") {
                val fmt = NumberFormat.getCurrencyInstance(Locale.US)
                fmt.format(amount)
            } else {
                val fmt = NumberFormat.getCurrencyInstance(Locale("vi", "VN"))
                fmt.format(amount)
            }
        } catch (e: Exception) {
            if (currency == "USD") "$${String.format("%.2f", amount)}"
            else "${String.format("%.0f", amount)} ₫"
        }
    }

    @Synchronized
    fun formatDate(timestamp: Long): String {
        return try {
            val locale = if (currentLanguage == "EN") Locale.ENGLISH else Locale("vi", "VN")
            SimpleDateFormat("dd/MM/yyyy", locale).format(Date(timestamp))
        } catch (e: Exception) {
            ""
        }
    }

    @Synchronized
    fun formatDateMonthYear(timestamp: Long): String {
        return try {
            val locale = if (currentLanguage == "EN") Locale.ENGLISH else Locale("vi", "VN")
            SimpleDateFormat("MM/yyyy", locale).format(Date(timestamp))
        } catch (e: Exception) {
            ""
        }
    }

    @Synchronized
    fun formatDateFull(timestamp: Long): String {
        return try {
            val locale = if (currentLanguage == "EN") Locale.ENGLISH else Locale("vi", "VN")
            SimpleDateFormat("EEEE, dd MMMM yyyy", locale).format(Date(timestamp))
        } catch (e: Exception) {
            ""
        }
    }
}


