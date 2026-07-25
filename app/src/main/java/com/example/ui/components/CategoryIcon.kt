package com.example.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.ChildCare
import androidx.compose.material.icons.filled.Coffee
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Fastfood
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Flight
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocalGasStation
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Smartphone
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.TwoWheeler
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.utils.FormatUtils

data class CategoryMeta(
    val name: String,
    val icon: ImageVector,
    val color: Color,
    val isExpense: Boolean
)

object CategoryData {
    val defaultExpenseCategories = listOf(
        CategoryMeta("Ăn uống", Icons.Default.Fastfood, Color(0xFFEF4444), true),
        CategoryMeta("Di chuyển", Icons.Default.TwoWheeler, Color(0xFFF59E0B), true),
        CategoryMeta("Mua sắm", Icons.Default.ShoppingBag, Color(0xFFEC4899), true),
        CategoryMeta("Hóa đơn", Icons.Default.ReceiptLong, Color(0xFF3B82F6), true),
        CategoryMeta("Giải trí", Icons.Default.SportsEsports, Color(0xFF8B5CF6), true),
        CategoryMeta("Sức khỏe", Icons.Default.MedicalServices, Color(0xFF10B981), true),
        CategoryMeta("Giáo dục", Icons.Default.Book, Color(0xFF6366F1), true),
        CategoryMeta("Nhà cửa", Icons.Default.Home, Color(0xFF14B8A6), true),
        CategoryMeta("Khác", Icons.Default.Category, Color(0xFF6B7280), true)
    )

    val defaultIncomeCategories = listOf(
        CategoryMeta("Lương", Icons.Default.Payments, Color(0xFF10B981), false),
        CategoryMeta("Thưởng", Icons.Default.Star, Color(0xFFF59E0B), false),
        CategoryMeta("Đầu tư", Icons.Default.TrendingUp, Color(0xFF3B82F6), false),
        CategoryMeta("Bán hàng", Icons.Default.Storefront, Color(0xFF8B5CF6), false),
        CategoryMeta("Thu nhập khác", Icons.Default.AccountBalanceWallet, Color(0xFF06B6D4), false)
    )

    // Reactive list of user added custom categories
    val customCategories = mutableStateListOf<CategoryMeta>()

    fun getExpenseCategories(): List<CategoryMeta> {
        return defaultExpenseCategories + customCategories.filter { it.isExpense }
    }

    fun getIncomeCategories(): List<CategoryMeta> {
        return defaultIncomeCategories + customCategories.filter { !it.isExpense }
    }

    fun addCustomCategory(name: String, icon: ImageVector, color: Color, isExpense: Boolean) {
        val trimmed = name.trim()
        if (trimmed.isNotEmpty()) {
            val all = getExpenseCategories() + getIncomeCategories()
            if (all.none { it.name.equals(trimmed, ignoreCase = true) }) {
                customCategories.add(CategoryMeta(trimmed, icon, color, isExpense))
            }
        }
    }

    private val translationsEn = mapOf(
        "Ăn uống" to "Food & Dining",
        "Di chuyển" to "Transportation",
        "Mua sắm" to "Shopping",
        "Hóa đơn" to "Bills & Utilities",
        "Giải trí" to "Entertainment",
        "Sức khỏe" to "Health & Medical",
        "Giáo dục" to "Education",
        "Nhà cửa" to "Housing & Rent",
        "Khác" to "Other Expense",
        "Lương" to "Salary",
        "Thưởng" to "Bonus",
        "Đầu tư" to "Investment",
        "Bán hàng" to "Sales",
        "Thu nhập khác" to "Other Income"
    )

    fun getCategoryDisplayName(categoryName: String): String {
        if (FormatUtils.currentLanguage == "EN") {
            return translationsEn[categoryName] ?: categoryName
        }
        return categoryName
    }

    fun getCategoryMeta(name: String): CategoryMeta {
        val all = getExpenseCategories() + getIncomeCategories()
        val found = all.find {
            it.name.equals(name, ignoreCase = true) ||
            translationsEn[it.name]?.equals(name, ignoreCase = true) == true
        }
        return found ?: CategoryMeta(name, Icons.Default.Category, Color(0xFF6B7280), true)
    }

    val availableIcons = listOf(
        Icons.Default.Fastfood,
        Icons.Default.Coffee,
        Icons.Default.ShoppingBag,
        Icons.Default.TwoWheeler,
        Icons.Default.DirectionsCar,
        Icons.Default.LocalGasStation,
        Icons.Default.Home,
        Icons.Default.ReceiptLong,
        Icons.Default.SportsEsports,
        Icons.Default.Movie,
        Icons.Default.MedicalServices,
        Icons.Default.FitnessCenter,
        Icons.Default.Pets,
        Icons.Default.Flight,
        Icons.Default.Book,
        Icons.Default.ChildCare,
        Icons.Default.Payments,
        Icons.Default.Star,
        Icons.Default.TrendingUp,
        Icons.Default.Storefront,
        Icons.Default.AccountBalanceWallet,
        Icons.Default.Category
    )

    val availableColors = listOf(
        Color(0xFFEF4444), // Red
        Color(0xFFF59E0B), // Amber
        Color(0xFF10B981), // Emerald
        Color(0xFF14B8A6), // Teal
        Color(0xFF3B82F6), // Blue
        Color(0xFF6366F1), // Indigo
        Color(0xFF8B5CF6), // Purple
        Color(0xFFEC4899), // Pink
        Color(0xFF06B6D4), // Cyan
        Color(0xFF84CC16)  // Lime
    )
}

@Composable
fun CategoryIcon(
    categoryName: String,
    modifier: Modifier = Modifier,
    tint: Color = Color.Unspecified
) {
    val meta = CategoryData.getCategoryMeta(categoryName)
    val iconColor = if (tint == Color.Unspecified) meta.color else tint
    Icon(
        imageVector = meta.icon,
        contentDescription = categoryName,
        modifier = modifier,
        tint = iconColor
    )
}

