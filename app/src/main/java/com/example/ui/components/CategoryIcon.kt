package com.example.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Fastfood
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.TwoWheeler
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

data class CategoryMeta(
    val name: String,
    val icon: ImageVector,
    val color: Color,
    val isExpense: Boolean
)

object CategoryData {
    val expenseCategories = listOf(
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

    val incomeCategories = listOf(
        CategoryMeta("Lương", Icons.Default.Payments, Color(0xFF10B981), false),
        CategoryMeta("Thưởng", Icons.Default.Star, Color(0xFFF59E0B), false),
        CategoryMeta("Đầu tư", Icons.Default.TrendingUp, Color(0xFF3B82F6), false),
        CategoryMeta("Bán hàng", Icons.Default.Storefront, Color(0xFF8B5CF6), false),
        CategoryMeta("Thu nhập khác", Icons.Default.AccountBalanceWallet, Color(0xFF06B6D4), false)
    )

    private val categoryMap: Map<String, CategoryMeta> by lazy {
        (expenseCategories + incomeCategories).associateBy { it.name.lowercase() }
    }

    fun getCategoryMeta(name: String): CategoryMeta {
        return categoryMap[name.lowercase()]
            ?: CategoryMeta(name, Icons.Default.Category, Color(0xFF6B7280), true)
    }
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
