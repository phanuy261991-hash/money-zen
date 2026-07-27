package com.example.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AddCard
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.CategoryData
import com.example.ui.components.CategoryIcon
import com.example.ui.theme.ExpenseRed
import com.example.ui.theme.IncomeGreen
import com.example.ui.viewmodel.BudgetProgress
import com.example.ui.viewmodel.CategorySummary
import com.example.ui.viewmodel.MonthComparisonData
import com.example.utils.FormatUtils

@Composable
fun AnalyticsBudgetScreen(
    totalIncome: Double,
    totalExpense: Double,
    categorySummaries: List<CategorySummary>,
    budgetProgresses: List<BudgetProgress>,
    monthComparisonData: MonthComparisonData = MonthComparisonData(),
    onSetBudgetClick: (categoryName: String, currentLimit: Double) -> Unit,
    onBackClick: (() -> Unit)? = null
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item { Spacer(modifier = Modifier.height(8.dp)) }

        if (onBackClick != null) {
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = onBackClick,
                        modifier = Modifier.testTag("back_to_home_btn")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = if (com.example.utils.AppStrings.isEn) "Back" else "Quay lại"
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = com.example.utils.AppStrings.detailedReportTitle,
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                    )
                }
            }
        }

        // Section Title: Thống kê Thu nhập & Chi tiêu
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = com.example.utils.AppStrings.incomeVsExpenseRatio,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    val grandTotal = totalIncome + totalExpense
                    val incomeRatio = if (grandTotal > 0) (totalIncome / grandTotal).toFloat() else 0.5f

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(com.example.utils.AppStrings.income, fontSize = 11.sp, color = IncomeGreen)
                            Text(
                                FormatUtils.formatCurrency(totalIncome),
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = IncomeGreen
                            )
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text(com.example.utils.AppStrings.expense, fontSize = 11.sp, color = ExpenseRed)
                            Text(
                                FormatUtils.formatCurrency(totalExpense),
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = ExpenseRed
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Dual progress indicator
                    LinearProgressIndicator(
                        progress = { incomeRatio.coerceIn(0.01f, 0.99f) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(10.dp)
                            .clip(RoundedCornerShape(5.dp)),
                        color = IncomeGreen,
                        trackColor = ExpenseRed
                    )
                }
            }
        }

        // Section: So Sánh Với Tháng Trước (Bar Chart & Comparison)
        item {
            MonthComparisonCard(data = monthComparisonData)
        }

        // Section: Category Breakdown with Visual Donut Chart
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Biểu Đồ Chi Tiêu Theo Danh Mục",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                        Icon(
                            Icons.Default.PieChart,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    if (categorySummaries.isEmpty()) {
                        Text(
                            text = "Chưa có dữ liệu chi tiêu trong khoảng thời gian này.",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(vertical = 12.dp)
                        )
                    } else {
                        // Donut Chart Centered
                        Box(
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        ) {
                            DonutPieChart(categorySummaries = categorySummaries, totalExpense = totalExpense)
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        categorySummaries.forEach { cat ->
                            val meta = CategoryData.getCategoryMeta(cat.categoryName)
                            Column(modifier = Modifier.padding(vertical = 6.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        CategoryIcon(categoryName = cat.categoryName, modifier = Modifier.size(20.dp))
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = CategoryData.getCategoryDisplayName(cat.categoryName),
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Medium
                                        )
                                        Text(
                                            text = " (${cat.transactionCount} giao dịch)",
                                            fontSize = 11.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                    Text(
                                        text = "${FormatUtils.formatCurrency(cat.totalAmount)} (${String.format("%.1f", cat.percentage)}%)",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                LinearProgressIndicator(
                                    progress = { (cat.percentage / 100f).coerceIn(0f, 1f) },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(6.dp)
                                        .clip(RoundedCornerShape(3.dp)),
                                    color = meta.color,
                                    trackColor = meta.color.copy(alpha = 0.15f)
                                )
                            }
                        }
                    }
                }
            }
        }

        // Section: Quản Lý Ngân Sách Hàng Tháng
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Ngân Sách Chi Tiêu Hàng Tháng",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
            }
        }

        // Budget Items
        items(CategoryData.getExpenseCategories(), key = { it.name }) { cat ->
            val bgProgress = budgetProgresses.find { it.categoryName == cat.name }
            val spent = bgProgress?.spentAmount ?: 0.0
            val limit = bgProgress?.budgetLimit ?: 0.0
            val ratio = if (limit > 0) (spent / limit).toFloat() else 0f
            val isOver = spent > limit && limit > 0

            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            CategoryIcon(categoryName = cat.name, modifier = Modifier.size(22.dp))
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = CategoryData.getCategoryDisplayName(cat.name),
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }

                        IconButton(
                            onClick = { onSetBudgetClick(cat.name, limit) },
                            modifier = Modifier.testTag("edit_budget_${cat.name}")
                        ) {
                            Icon(
                                Icons.Default.Edit,
                                contentDescription = "Sửa ngân sách",
                                modifier = Modifier.size(18.dp),
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    if (limit <= 0) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Đã chi: ${FormatUtils.formatCurrency(spent)} (Chưa đặt hạn mức)",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            TextButton(onClick = { onSetBudgetClick(cat.name, limit) }) {
                                Text("Đặt hạn mức", fontSize = 11.sp)
                            }
                        }
                    } else {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Đã chi: ${FormatUtils.formatCurrency(spent)}",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                color = if (isOver) ExpenseRed else MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Hạn mức: ${FormatUtils.formatCurrency(limit)}",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        val progressColor = when {
                            isOver -> ExpenseRed
                            ratio >= 0.8f -> Color(0xFFD97706)
                            else -> IncomeGreen
                        }

                        LinearProgressIndicator(
                            progress = { ratio.coerceIn(0f, 1f) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(RoundedCornerShape(4.dp)),
                            color = progressColor,
                            trackColor = progressColor.copy(alpha = 0.15f)
                        )

                        if (isOver) {
                            Row(
                                modifier = Modifier.padding(top = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    Icons.Default.Warning,
                                    contentDescription = null,
                                    tint = ExpenseRed,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Vượt hạn mức ${FormatUtils.formatCurrency(spent - limit)}",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = ExpenseRed
                                )
                            }
                        }
                    }
                }
            }
        }

        item { Spacer(modifier = Modifier.height(80.dp)) }
    }
}

@Composable
private fun DonutPieChart(
    categorySummaries: List<CategorySummary>,
    totalExpense: Double,
    modifier: Modifier = Modifier
) {
    if (categorySummaries.isEmpty() || totalExpense <= 0) return

    val angles = categorySummaries.map { (it.percentage / 100f) * 360f }
    val colors = categorySummaries.map { CategoryData.getCategoryMeta(it.categoryName).color }

    Box(
        modifier = modifier.size(180.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize().padding(12.dp)) {
            var startAngle = -90f
            angles.forEachIndexed { index, sweepAngle ->
                drawArc(
                    color = colors[index],
                    startAngle = startAngle,
                    sweepAngle = (sweepAngle - 2f).coerceAtLeast(1f),
                    useCenter = false,
                    style = Stroke(width = 24.dp.toPx())
                )
                startAngle += sweepAngle
            }
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "Tổng chi tiêu",
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = FormatUtils.formatCurrency(totalExpense),
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = ExpenseRed
            )
        }
    }
}

@Composable
private fun MonthComparisonCard(
    data: MonthComparisonData
) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "So Sánh Với Tháng Trước",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
                Icon(
                    imageVector = Icons.Default.BarChart,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Bar Chart Visual
            val maxAmount = maxOf(
                data.thisMonthIncome, data.thisMonthExpense,
                data.lastMonthIncome, data.lastMonthExpense,
                100000.0
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(110.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.Bottom
            ) {
                // Group 1: Tháng trước
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.weight(1f)
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.Bottom,
                        modifier = Modifier.height(80.dp)
                    ) {
                        // Last Month Income
                        val incRatio = (data.lastMonthIncome / maxAmount).toFloat().coerceIn(0.05f, 1f)
                        Box(
                            modifier = Modifier
                                .width(20.dp)
                                .fillMaxHeight(incRatio)
                                .clip(RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
                                .background(IncomeGreen.copy(alpha = 0.5f))
                        )
                        // Last Month Expense
                        val expRatio = (data.lastMonthExpense / maxAmount).toFloat().coerceIn(0.05f, 1f)
                        Box(
                            modifier = Modifier
                                .width(20.dp)
                                .fillMaxHeight(expRatio)
                                .clip(RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
                                .background(ExpenseRed.copy(alpha = 0.5f))
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(text = "Tháng trước", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }

                // Group 2: Tháng này
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.weight(1f)
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.Bottom,
                        modifier = Modifier.height(80.dp)
                    ) {
                        // This Month Income
                        val incRatio = (data.thisMonthIncome / maxAmount).toFloat().coerceIn(0.05f, 1f)
                        Box(
                            modifier = Modifier
                                .width(20.dp)
                                .fillMaxHeight(incRatio)
                                .clip(RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
                                .background(IncomeGreen)
                        )
                        // This Month Expense
                        val expRatio = (data.thisMonthExpense / maxAmount).toFloat().coerceIn(0.05f, 1f)
                        Box(
                            modifier = Modifier
                                .width(20.dp)
                                .fillMaxHeight(expRatio)
                                .clip(RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
                                .background(ExpenseRed)
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(text = "Tháng này", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Stat Cards
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Income Comparison Tile
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = IncomeGreen.copy(alpha = 0.12f),
                    modifier = Modifier.weight(1f)
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Text("Thu nhập", fontSize = 11.sp, color = IncomeGreen, fontWeight = FontWeight.Bold)
                        Text(FormatUtils.formatCurrency(data.thisMonthIncome), fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(2.dp))
                        val incDiffText = if (data.incomeDiff >= 0) "+${FormatUtils.formatCurrency(data.incomeDiff)}" else FormatUtils.formatCurrency(data.incomeDiff)
                        Text(
                            text = "$incDiffText (${String.format("%.1f", data.incomeChangePercent)}%)",
                            fontSize = 10.sp,
                            color = if (data.incomeDiff >= 0) IncomeGreen else ExpenseRed
                        )
                    }
                }

                // Expense Comparison Tile
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = ExpenseRed.copy(alpha = 0.12f),
                    modifier = Modifier.weight(1f)
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Text("Chi tiêu", fontSize = 11.sp, color = ExpenseRed, fontWeight = FontWeight.Bold)
                        Text(FormatUtils.formatCurrency(data.thisMonthExpense), fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(2.dp))
                        val expDiffText = if (data.expenseDiff >= 0) "+${FormatUtils.formatCurrency(data.expenseDiff)}" else FormatUtils.formatCurrency(data.expenseDiff)
                        Text(
                            text = "$expDiffText (${String.format("%.1f", data.expenseChangePercent)}%)",
                            fontSize = 10.sp,
                            color = if (data.expenseDiff <= 0) IncomeGreen else ExpenseRed
                        )
                    }
                }
            }
        }
    }
}
