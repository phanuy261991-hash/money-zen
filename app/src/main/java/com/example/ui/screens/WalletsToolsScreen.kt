package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material.icons.filled.Smartphone
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.DebtEntity
import com.example.data.model.RecurringBillEntity
import com.example.data.model.SavingsGoalEntity
import com.example.data.model.WalletEntity
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.ExpenseRed
import com.example.ui.viewmodel.WalletBudgetProgress
import com.example.utils.AppStrings
import com.example.utils.FormatUtils

@Composable
fun WalletsToolsScreen(
    wallets: List<WalletEntity>,
    debts: List<DebtEntity>,
    savingsGoals: List<SavingsGoalEntity>,
    recurringBills: List<RecurringBillEntity>,
    walletBudgetProgresses: List<WalletBudgetProgress> = emptyList(),
    overallMonthlyLimit: Double = 0.0,
    overallMonthlySpent: Double = 0.0,
    isOverallOverLimit: Boolean = false,
    onAddWallet: (name: String, type: String, initialBalance: Double, monthlyLimit: Double) -> Unit,
    onSetWalletLimit: (walletId: Long, limit: Double) -> Unit = { _, _ -> },
    onSetOverallLimit: (limit: Double) -> Unit = {},
    onDeleteWallet: (WalletEntity) -> Unit,
    onTransferMoney: (fromWalletId: Long, toWalletId: Long, amount: Double, note: String) -> Unit,
    onAddDebt: (personName: String, amount: Double, type: String, note: String, walletId: Long) -> Unit,
    onSettleDebt: (DebtEntity) -> Unit,
    onDeleteDebt: (DebtEntity) -> Unit,
    onAddSavingsGoal: (title: String, targetAmount: Double) -> Unit,
    onDeleteSavingsGoal: (SavingsGoalEntity) -> Unit,
    onAddRecurringBill: (title: String, amount: Double, category: String, frequency: String, walletId: Long) -> Unit,
    onPayRecurringBill: (RecurringBillEntity) -> Unit,
    onDeleteRecurringBill: (RecurringBillEntity) -> Unit
) {
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val tabs = listOf(
        AppStrings.tabWalletsList,
        AppStrings.tabDebts,
        AppStrings.tabSavings,
        AppStrings.tabBills
    )

    var showAddWalletDialog by remember { mutableStateOf(false) }
    var showTransferDialog by remember { mutableStateOf(false) }
    var showAddDebtDialog by remember { mutableStateOf(false) }
    var showAddGoalDialog by remember { mutableStateOf(false) }
    var showAddBillDialog by remember { mutableStateOf(false) }
    var showSetOverallLimitDialog by remember { mutableStateOf(false) }
    var walletForLimitDialog by remember { mutableStateOf<WalletEntity?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Tab row header
        ScrollableTabRow(
            selectedTabIndex = selectedTabIndex,
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.primary,
            edgePadding = 16.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTabIndex == index,
                    onClick = { selectedTabIndex = index },
                    text = {
                        Text(
                            text = title,
                            fontWeight = if (selectedTabIndex == index) FontWeight.Bold else FontWeight.Normal,
                            fontSize = 14.sp
                        )
                    },
                    modifier = Modifier.testTag("tool_tab_$index")
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {
            when (selectedTabIndex) {
                0 -> WalletsTabContent(
                    wallets = wallets,
                    walletBudgetProgresses = walletBudgetProgresses,
                    overallMonthlyLimit = overallMonthlyLimit,
                    overallMonthlySpent = overallMonthlySpent,
                    isOverallOverLimit = isOverallOverLimit,
                    onOpenAddWallet = { showAddWalletDialog = true },
                    onOpenTransfer = { showTransferDialog = true },
                    onOpenSetOverallLimit = { showSetOverallLimitDialog = true },
                    onOpenSetWalletLimit = { walletForLimitDialog = it },
                    onDeleteWallet = onDeleteWallet
                )
                1 -> DebtsTabContent(
                    debts = debts,
                    onOpenAddDebt = { showAddDebtDialog = true },
                    onSettleDebt = onSettleDebt,
                    onDeleteDebt = onDeleteDebt
                )
                2 -> SavingsTabContent(
                    savingsGoals = savingsGoals,
                    onOpenAddGoal = { showAddGoalDialog = true },
                    onDeleteSavingsGoal = onDeleteSavingsGoal
                )
                3 -> BillsTabContent(
                    bills = recurringBills,
                    onOpenAddBill = { showAddBillDialog = true },
                    onPayBill = onPayRecurringBill,
                    onDeleteRecurringBill = onDeleteRecurringBill
                )
            }
        }
    }

    // Dialogs
    if (showAddWalletDialog) {
        AddWalletDialog(
            onDismiss = { showAddWalletDialog = false },
            onSave = { name, type, balance, monthlyLimit ->
                onAddWallet(name, type, balance, monthlyLimit)
                showAddWalletDialog = false
            }
        )
    }

    if (showTransferDialog && wallets.size >= 2) {
        TransferMoneyDialog(
            wallets = wallets,
            onDismiss = { showTransferDialog = false },
            onTransfer = { fromId, toId, amount, note ->
                onTransferMoney(fromId, toId, amount, note)
                showTransferDialog = false
            }
        )
    }

    if (showAddDebtDialog) {
        AddDebtDialog(
            wallets = wallets,
            onDismiss = { showAddDebtDialog = false },
            onSave = { person, amount, type, note, wId ->
                onAddDebt(person, amount, type, note, wId)
                showAddDebtDialog = false
            }
        )
    }

    if (showAddGoalDialog) {
        AddSavingsGoalDialog(
            onDismiss = { showAddGoalDialog = false },
            onSave = { title, target ->
                onAddSavingsGoal(title, target)
                showAddGoalDialog = false
            }
        )
    }

    if (showAddBillDialog) {
        AddRecurringBillDialog(
            wallets = wallets,
            onDismiss = { showAddBillDialog = false },
            onSave = { title, amount, category, frequency, walletId ->
                onAddRecurringBill(title, amount, category, frequency, walletId)
                showAddBillDialog = false
            }
        )
    }

    if (showSetOverallLimitDialog) {
        SetOverallLimitDialog(
            currentLimit = overallMonthlyLimit,
            onDismiss = { showSetOverallLimitDialog = false },
            onSaveLimit = { limit ->
                onSetOverallLimit(limit)
                showSetOverallLimitDialog = false
            }
        )
    }

    walletForLimitDialog?.let { wallet ->
        SetWalletLimitDialog(
            wallet = wallet,
            onDismiss = { walletForLimitDialog = null },
            onSaveLimit = { limit ->
                onSetWalletLimit(wallet.id, limit)
                walletForLimitDialog = null
            }
        )
    }
}

@Composable
private fun WalletsTabContent(
    wallets: List<WalletEntity>,
    walletBudgetProgresses: List<WalletBudgetProgress>,
    overallMonthlyLimit: Double,
    overallMonthlySpent: Double,
    isOverallOverLimit: Boolean,
    onOpenAddWallet: () -> Unit,
    onOpenTransfer: () -> Unit,
    onOpenSetOverallLimit: () -> Unit,
    onOpenSetWalletLimit: (WalletEntity) -> Unit,
    onDeleteWallet: (WalletEntity) -> Unit
) {
    var walletToDelete by remember { mutableStateOf<WalletEntity?>(null) }
    val totalBalance = wallets.sumOf { it.balance }

    if (walletToDelete != null) {
        AlertDialog(
            onDismissRequest = { walletToDelete = null },
            title = { Text(AppStrings.deleteConfirmTitle, fontWeight = FontWeight.Bold) },
            text = { Text(AppStrings.deleteWalletMsg) },
            confirmButton = {
                Button(
                    onClick = {
                        walletToDelete?.let { onDeleteWallet(it) }
                        walletToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ExpenseRed)
                ) {
                    Text(AppStrings.deleteBtn, color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { walletToDelete = null }) {
                    Text(AppStrings.cancel)
                }
            }
        )
    }

    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        // Total Balance Overview Card
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = AppStrings.totalWalletBalance,
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = FormatUtils.formatCurrency(totalBalance),
                        style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Button(
                            onClick = onOpenAddWallet,
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(text = AppStrings.addWallet, fontSize = 13.sp)
                        }

                        Button(
                            onClick = onOpenTransfer,
                            colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
                            shape = RoundedCornerShape(12.dp),
                            enabled = wallets.size >= 2,
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.SwapHoriz, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(text = AppStrings.transferMoney, fontSize = 13.sp)
                        }
                    }
                }
            }
        }

        // Overall Monthly Limit Banner / Card
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isOverallOverLimit) ExpenseRed.copy(alpha = 0.12f) else MaterialTheme.colorScheme.surfaceVariant
                ),
                border = if (isOverallOverLimit) androidx.compose.foundation.BorderStroke(1.5.dp, ExpenseRed) else null,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = AppStrings.overallLimitTitle,
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                color = if (isOverallOverLimit) ExpenseRed else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            if (isOverallOverLimit) {
                                Text(
                                    text = "⚠️ ${AppStrings.limitWarningOverall}",
                                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                                    color = ExpenseRed
                                )
                            }
                        }

                        TextButton(onClick = onOpenSetOverallLimit) {
                            Text(if (overallMonthlyLimit > 0) AppStrings.setOverallLimit else AppStrings.noLimitSet)
                        }
                    }

                    if (overallMonthlyLimit > 0) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "${AppStrings.expense}: ${FormatUtils.formatCurrency(overallMonthlySpent)}",
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.SemiBold,
                                color = if (isOverallOverLimit) ExpenseRed else MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Limit: ${FormatUtils.formatCurrency(overallMonthlyLimit)}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        val ratio = (overallMonthlySpent / overallMonthlyLimit).toFloat().coerceIn(0f, 1f)
                        LinearProgressIndicator(
                            progress = { ratio },
                            color = if (isOverallOverLimit) ExpenseRed else EmeraldPrimary,
                            trackColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(RoundedCornerShape(4.dp))
                        )
                    }
                }
            }
        }

        item {
            Text(
                text = "${AppStrings.tabWalletsList} (${wallets.size})",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                modifier = Modifier.padding(vertical = 4.dp)
            )
        }

        items(wallets, key = { it.id }) { wallet ->
            val wProgress = walletBudgetProgresses.find { it.wallet.id == wallet.id }
            val spent = wProgress?.spentAmount ?: 0.0
            val limit = wallet.monthlyLimit
            val isOver = wProgress?.isOverLimit ?: false

            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                border = if (isOver) androidx.compose.foundation.BorderStroke(1.5.dp, ExpenseRed) else null,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val (icon, color) = when (wallet.type) {
                            "NGAN_HANG" -> Icons.Default.AccountBalance to Color(0xFF2563EB)
                            "VI_DIEN_TU" -> Icons.Default.Smartphone to Color(0xFFEC4899)
                            "THE_TIN_DUNG" -> Icons.Default.CreditCard to Color(0xFFF59E0B)
                            else -> Icons.Default.Payments to Color(0xFF10B981)
                        }

                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(color.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(22.dp))
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = wallet.name,
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                            )
                            val typeLabel = when (wallet.type) {
                                "NGAN_HANG" -> AppStrings.typeBank
                                "VI_DIEN_TU" -> AppStrings.typeEWallet
                                "THE_TIN_DUNG" -> AppStrings.typeCredit
                                else -> AppStrings.typeCash
                            }
                            Text(
                                text = typeLabel,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = FormatUtils.formatCurrency(wallet.balance),
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = if (wallet.balance >= 0) MaterialTheme.colorScheme.onSurface else ExpenseRed
                            )
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                TextButton(
                                    onClick = { onOpenSetWalletLimit(wallet) },
                                    modifier = Modifier.height(30.dp)
                                ) {
                                    Text(
                                        text = if (limit > 0) "${AppStrings.walletMonthlyLimit}: ${FormatUtils.formatCurrency(limit)}" else "+ ${AppStrings.walletMonthlyLimit}",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                                IconButton(
                                    onClick = { walletToDelete = wallet },
                                    modifier = Modifier.size(28.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.DeleteOutline,
                                        contentDescription = AppStrings.deleteBtn,
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }
                    }

                    if (limit > 0) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "${AppStrings.expense}: ${FormatUtils.formatCurrency(spent)}",
                                style = MaterialTheme.typography.bodySmall,
                                color = if (isOver) ExpenseRed else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            if (isOver) {
                                Text(
                                    text = "⚠️ ${AppStrings.limitWarningWallet}",
                                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                                    color = ExpenseRed
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        val ratio = (spent / limit).toFloat().coerceIn(0f, 1f)
                        LinearProgressIndicator(
                            progress = { ratio },
                            color = if (isOver) ExpenseRed else EmeraldPrimary,
                            trackColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp))
                        )
                    }
                }
            }
        }

        item { Spacer(modifier = Modifier.height(80.dp)) }
    }
}

@Composable
private fun DebtsTabContent(
    debts: List<DebtEntity>,
    onOpenAddDebt: () -> Unit,
    onSettleDebt: (DebtEntity) -> Unit,
    onDeleteDebt: (DebtEntity) -> Unit
) {
    var debtToDelete by remember { mutableStateOf<DebtEntity?>(null) }
    val activeDebts = debts.filter { !it.isSettled }
    val totalLent = activeDebts.filter { it.type == "CHO_VAY" }.sumOf { it.amount }
    val totalBorrowed = activeDebts.filter { it.type == "DI_VAY" }.sumOf { it.amount }

    if (debtToDelete != null) {
        AlertDialog(
            onDismissRequest = { debtToDelete = null },
            title = { Text(AppStrings.deleteConfirmTitle, fontWeight = FontWeight.Bold) },
            text = { Text(AppStrings.deleteDebtMsg) },
            confirmButton = {
                Button(
                    onClick = {
                        debtToDelete?.let { onDeleteDebt(it) }
                        debtToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ExpenseRed)
                ) {
                    Text(AppStrings.deleteBtn, color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { debtToDelete = null }) {
                    Text(AppStrings.cancel)
                }
            }
        )
    }

    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        // Summary Card
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = AppStrings.debtsManagement,
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                        Button(
                            onClick = onOpenAddDebt,
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(AppStrings.addDebtTitle)
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = if (FormatUtils.currentLanguage == "EN") "Lent (To Collect)" else "Cho Vay (Cần thu)",
                                style = MaterialTheme.typography.bodySmall
                            )
                            Text(
                                FormatUtils.formatCurrency(totalLent),
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = EmeraldPrimary
                            )
                        }

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = if (FormatUtils.currentLanguage == "EN") "Borrowed (To Pay)" else "Đi Vay (Cần trả)",
                                style = MaterialTheme.typography.bodySmall
                            )
                            Text(
                                FormatUtils.formatCurrency(totalBorrowed),
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = ExpenseRed
                            )
                        }
                    }
                }
            }
        }

        items(debts, key = { it.id }) { debt ->
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (debt.isSettled) MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f) else MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val isLend = debt.type == "CHO_VAY"
                    val iconColor = if (isLend) EmeraldPrimary else ExpenseRed

                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(iconColor.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (isLend) Icons.Default.MonetizationOn else Icons.Default.CreditCard,
                            contentDescription = null,
                            tint = iconColor,
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = debt.personName,
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                        Text(
                            text = if (isLend) AppStrings.lendBtn else AppStrings.borrowBtn,
                            style = MaterialTheme.typography.bodySmall,
                            color = iconColor
                        )
                        if (debt.note.isNotBlank()) {
                            Text(
                                text = debt.note,
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = FormatUtils.formatCurrency(debt.amount),
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = iconColor
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            if (!debt.isSettled) {
                                TextButton(onClick = { onSettleDebt(debt) }) {
                                    Text(AppStrings.settleDebtBtn, fontSize = 12.sp)
                                }
                            } else {
                                Text(
                                    AppStrings.settledLabel,
                                    fontSize = 12.sp,
                                    color = EmeraldPrimary,
                                    modifier = Modifier.padding(end = 8.dp)
                                )
                            }

                            IconButton(
                                onClick = { debtToDelete = debt },
                                modifier = Modifier.size(28.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.DeleteOutline,
                                    contentDescription = AppStrings.deleteBtn,
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                                    modifier = Modifier.size(18.dp)
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
private fun SavingsTabContent(
    savingsGoals: List<SavingsGoalEntity>,
    onOpenAddGoal: () -> Unit,
    onDeleteSavingsGoal: (SavingsGoalEntity) -> Unit
) {
    var goalToDelete by remember { mutableStateOf<SavingsGoalEntity?>(null) }

    if (goalToDelete != null) {
        AlertDialog(
            onDismissRequest = { goalToDelete = null },
            title = { Text(AppStrings.deleteConfirmTitle, fontWeight = FontWeight.Bold) },
            text = { Text(AppStrings.deleteGoalMsg) },
            confirmButton = {
                Button(
                    onClick = {
                        goalToDelete?.let { onDeleteSavingsGoal(it) }
                        goalToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ExpenseRed)
                ) {
                    Text(AppStrings.deleteBtn, color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { goalToDelete = null }) {
                    Text(AppStrings.cancel)
                }
            }
        )
    }

    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = AppStrings.savingsGoals,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
                Button(
                    onClick = onOpenAddGoal,
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(AppStrings.addGoalTitle)
                }
            }
        }

        items(savingsGoals, key = { it.id }) { goal ->
            val ratio = if (goal.targetAmount > 0) (goal.currentAmount / goal.targetAmount).toFloat().coerceIn(0f, 1f) else 0f
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(EmeraldPrimary.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Savings, contentDescription = null, tint = EmeraldPrimary, modifier = Modifier.size(20.dp))
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = goal.title,
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                            )
                            Text(
                                text = "${FormatUtils.formatCurrency(goal.currentAmount)} / ${FormatUtils.formatCurrency(goal.targetAmount)}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        IconButton(
                            onClick = { goalToDelete = goal },
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.DeleteOutline,
                                contentDescription = AppStrings.deleteBtn,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    LinearProgressIndicator(
                        progress = { ratio },
                        color = EmeraldPrimary,
                        trackColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp))
                    )
                }
            }
        }

        item { Spacer(modifier = Modifier.height(80.dp)) }
    }
}

@Composable
private fun BillsTabContent(
    bills: List<RecurringBillEntity>,
    onOpenAddBill: () -> Unit,
    onPayBill: (RecurringBillEntity) -> Unit,
    onDeleteRecurringBill: (RecurringBillEntity) -> Unit
) {
    var billToDelete by remember { mutableStateOf<RecurringBillEntity?>(null) }

    if (billToDelete != null) {
        AlertDialog(
            onDismissRequest = { billToDelete = null },
            title = { Text(AppStrings.deleteConfirmTitle, fontWeight = FontWeight.Bold) },
            text = { Text(AppStrings.deleteBillMsg) },
            confirmButton = {
                Button(
                    onClick = {
                        billToDelete?.let { onDeleteRecurringBill(it) }
                        billToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ExpenseRed)
                ) {
                    Text(AppStrings.deleteBtn, color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { billToDelete = null }) {
                    Text(AppStrings.cancel)
                }
            }
        )
    }

    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = AppStrings.recurringBills,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
                Button(
                    onClick = onOpenAddBill,
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(AppStrings.addBillTitle)
                }
            }
        }

        items(bills, key = { it.id }) { bill ->
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(ExpenseRed.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Receipt, contentDescription = null, tint = ExpenseRed, modifier = Modifier.size(22.dp))
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = bill.title,
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                        Text(
                            text = "${bill.category} • ${bill.frequency}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = FormatUtils.formatCurrency(bill.amount),
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = ExpenseRed
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Button(
                                onClick = { onPayBill(bill) },
                                colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
                                contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                modifier = Modifier.height(28.dp)
                            ) {
                                Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(2.dp))
                                Text(AppStrings.payBillBtn, fontSize = 11.sp)
                            }

                            IconButton(
                                onClick = { billToDelete = bill },
                                modifier = Modifier.size(28.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.DeleteOutline,
                                    contentDescription = AppStrings.deleteBtn,
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                                    modifier = Modifier.size(18.dp)
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

// Localized Dialogs
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddRecurringBillDialog(
    wallets: List<WalletEntity>,
    onDismiss: () -> Unit,
    onSave: (title: String, amount: Double, category: String, frequency: String, walletId: Long) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var amountText by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf(if (FormatUtils.currentLanguage == "EN") "Electricity" else "Tiền điện") }
    var selectedFrequency by remember { mutableStateOf(if (FormatUtils.currentLanguage == "EN") "Monthly" else "Hàng tháng") }
    var selectedWalletId by remember { mutableStateOf(wallets.firstOrNull()?.id ?: 1L) }

    val categories = if (FormatUtils.currentLanguage == "EN") {
        listOf("Electricity", "Water", "Internet", "Rent", "Tuition", "Other")
    } else {
        listOf("Tiền điện", "Tiền nước", "Internet", "Tiền nhà", "Tiền học", "Khác")
    }

    val frequencies = if (FormatUtils.currentLanguage == "EN") {
        listOf("Monthly", "Weekly", "Yearly")
    } else {
        listOf("Hàng tháng", "Hàng tuần", "Hàng năm")
    }

    var catExpanded by remember { mutableStateOf(false) }
    var freqExpanded by remember { mutableStateOf(false) }
    var walletExpanded by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(AppStrings.addBillTitle, fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text(AppStrings.billTitleLabel) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = amountText,
                    onValueChange = { amountText = it },
                    label = { Text("${AppStrings.amountLabel}") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                ExposedDropdownMenuBox(
                    expanded = catExpanded,
                    onExpandedChange = { catExpanded = !catExpanded }
                ) {
                    OutlinedTextField(
                        value = selectedCategory,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text(AppStrings.categoryLabel) },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = catExpanded) },
                        modifier = Modifier.menuAnchor().fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = catExpanded,
                        onDismissRequest = { catExpanded = false }
                    ) {
                        categories.forEach { cat ->
                            DropdownMenuItem(
                                text = { Text(cat) },
                                onClick = {
                                    selectedCategory = cat
                                    catExpanded = false
                                }
                            )
                        }
                    }
                }

                ExposedDropdownMenuBox(
                    expanded = freqExpanded,
                    onExpandedChange = { freqExpanded = !freqExpanded }
                ) {
                    OutlinedTextField(
                        value = selectedFrequency,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text(AppStrings.frequencyLabel) },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = freqExpanded) },
                        modifier = Modifier.menuAnchor().fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = freqExpanded,
                        onDismissRequest = { freqExpanded = false }
                    ) {
                        frequencies.forEach { freq ->
                            DropdownMenuItem(
                                text = { Text(freq) },
                                onClick = {
                                    selectedFrequency = freq
                                    freqExpanded = false
                                }
                            )
                        }
                    }
                }

                if (wallets.isNotEmpty()) {
                    val currentWallet = wallets.find { it.id == selectedWalletId } ?: wallets.first()
                    ExposedDropdownMenuBox(
                        expanded = walletExpanded,
                        onExpandedChange = { walletExpanded = !walletExpanded }
                    ) {
                        OutlinedTextField(
                            value = currentWallet.name,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text(AppStrings.deductWalletLabel) },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = walletExpanded) },
                            modifier = Modifier.menuAnchor().fillMaxWidth()
                        )
                        ExposedDropdownMenu(
                            expanded = walletExpanded,
                            onDismissRequest = { walletExpanded = false }
                        ) {
                            wallets.forEach { wallet ->
                                DropdownMenuItem(
                                    text = { Text("${wallet.name} (${FormatUtils.formatCurrency(wallet.balance)})") },
                                    onClick = {
                                        selectedWalletId = wallet.id
                                        walletExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val amount = amountText.toDoubleOrNull() ?: 0.0
                    val finalTitle = title.ifBlank { selectedCategory }
                    if (amount > 0) {
                        onSave(finalTitle, amount, selectedCategory, selectedFrequency, selectedWalletId)
                    }
                },
                enabled = title.isNotBlank() || amountText.isNotBlank()
            ) {
                Text(AppStrings.saveBill)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(AppStrings.cancel)
            }
        }
    )
}

private data class WalletTypeOption(
    val code: String,
    val label: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val color: Color
)

@Composable
private fun AddWalletDialog(
    onDismiss: () -> Unit,
    onSave: (name: String, type: String, balance: Double, monthlyLimit: Double) -> Unit
) {
    var walletName by remember { mutableStateOf("") }
    var selectedType by remember { mutableStateOf("TIEN_MAT") }
    var balanceText by remember { mutableStateOf("") }
    var limitText by remember { mutableStateOf("") }

    val walletTypes = listOf(
        WalletTypeOption(
            code = "TIEN_MAT",
            label = AppStrings.typeCash,
            icon = Icons.Default.Payments,
            color = Color(0xFF10B981)
        ),
        WalletTypeOption(
            code = "NGAN_HANG",
            label = AppStrings.typeBank,
            icon = Icons.Default.AccountBalance,
            color = Color(0xFF2563EB)
        ),
        WalletTypeOption(
            code = "VI_DIEN_TU",
            label = AppStrings.typeEWallet,
            icon = Icons.Default.Smartphone,
            color = Color(0xFFEC4899)
        ),
        WalletTypeOption(
            code = "THE_TIN_DUNG",
            label = AppStrings.typeCredit,
            icon = Icons.Default.CreditCard,
            color = Color(0xFFF59E0B)
        )
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.AccountBalanceWallet,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = AppStrings.addWallet,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = walletName,
                    onValueChange = { walletName = it },
                    label = { Text(AppStrings.walletName) },
                    placeholder = { Text(if (FormatUtils.currentLanguage == "EN") "e.g. Cash, Chase, PayPal" else "Ví dụ: Ví Tiền Mặt, Vietcombank, MoMo") },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                Text(
                    text = AppStrings.walletType,
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    val rows = walletTypes.chunked(2)
                    rows.forEach { rowItems ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            rowItems.forEach { option ->
                                val isSel = selectedType == option.code
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = if (isSel) option.color.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                    border = androidx.compose.foundation.BorderStroke(
                                        width = if (isSel) 2.dp else 1.dp,
                                        color = if (isSel) option.color else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                                    ),
                                    modifier = Modifier
                                        .weight(1f)
                                        .clickable { selectedType = option.code }
                                ) {
                                    Row(
                                        modifier = Modifier.padding(vertical = 10.dp, horizontal = 8.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(28.dp)
                                                .clip(CircleShape)
                                                .background(if (isSel) option.color else MaterialTheme.colorScheme.surfaceVariant),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                imageVector = option.icon,
                                                contentDescription = null,
                                                tint = if (isSel) Color.White else option.color,
                                                modifier = Modifier.size(16.dp)
                                            )
                                        }
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = option.label,
                                            fontSize = 12.sp,
                                            fontWeight = if (isSel) FontWeight.Bold else FontWeight.Medium,
                                            color = if (isSel) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant,
                                            maxLines = 1
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                OutlinedTextField(
                    value = balanceText,
                    onValueChange = { balanceText = it },
                    label = { Text("${AppStrings.initialBalance} (${FormatUtils.currentCurrency})") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = limitText,
                    onValueChange = { limitText = it },
                    label = { Text(AppStrings.walletMonthlyLimitSetting) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val bal = balanceText.toDoubleOrNull() ?: 0.0
                    val limit = limitText.toDoubleOrNull() ?: 0.0
                    if (walletName.isNotBlank()) {
                        onSave(walletName, selectedType, bal, limit)
                    }
                },
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary)
            ) {
                Text(AppStrings.addWallet, color = Color.White)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text(AppStrings.cancel) }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TransferMoneyDialog(
    wallets: List<WalletEntity>,
    onDismiss: () -> Unit,
    onTransfer: (fromId: Long, toId: Long, amount: Double, note: String) -> Unit
) {
    var fromWallet by remember { mutableStateOf(wallets.first()) }
    var toWallet by remember { mutableStateOf(wallets.getOrElse(1) { wallets.first() }) }
    var amountText by remember { mutableStateOf("") }
    var noteText by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(AppStrings.transferTitle, fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text("${AppStrings.fromWalletLabel}: ${fromWallet.name}", fontWeight = FontWeight.Bold)
                Text("${AppStrings.toWalletLabel}: ${toWallet.name}", fontWeight = FontWeight.Bold)

                OutlinedTextField(
                    value = amountText,
                    onValueChange = { amountText = it },
                    label = { Text(AppStrings.transferAmountLabel) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = noteText,
                    onValueChange = { noteText = it },
                    label = { Text(AppStrings.noteLabel) },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val amt = amountText.toDoubleOrNull() ?: 0.0
                    if (amt > 0 && fromWallet.id != toWallet.id) {
                        onTransfer(fromWallet.id, toWallet.id, amt, noteText)
                    }
                }
            ) {
                Text(AppStrings.confirmTransfer)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text(AppStrings.cancel) }
        }
    )
}

@Composable
private fun AddDebtDialog(
    wallets: List<WalletEntity>,
    onDismiss: () -> Unit,
    onSave: (personName: String, amount: Double, type: String, note: String, walletId: Long) -> Unit
) {
    var personName by remember { mutableStateOf("") }
    var amountText by remember { mutableStateOf("") }
    var debtType by remember { mutableStateOf("CHO_VAY") }
    var note by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(AppStrings.addDebtTitle, fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = personName,
                    onValueChange = { personName = it },
                    label = { Text(AppStrings.personNameLabel) },
                    modifier = Modifier.fillMaxWidth()
                )

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                        onClick = { debtType = "CHO_VAY" },
                        colors = ButtonDefaults.buttonColors(containerColor = if (debtType == "CHO_VAY") EmeraldPrimary else Color.Gray),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(AppStrings.lendBtn)
                    }

                    Button(
                        onClick = { debtType = "DI_VAY" },
                        colors = ButtonDefaults.buttonColors(containerColor = if (debtType == "DI_VAY") ExpenseRed else Color.Gray),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(AppStrings.borrowBtn)
                    }
                }

                OutlinedTextField(
                    value = amountText,
                    onValueChange = { amountText = it },
                    label = { Text("${AppStrings.amountLabel}") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = note,
                    onValueChange = { note = it },
                    label = { Text(AppStrings.noteReasonLabel) },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val amt = amountText.toDoubleOrNull() ?: 0.0
                    if (personName.isNotBlank() && amt > 0) {
                        onSave(personName, amt, debtType, note, wallets.firstOrNull()?.id ?: 1L)
                    }
                }
            ) {
                Text(AppStrings.saveDebt)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text(AppStrings.cancel) }
        }
    )
}

@Composable
private fun AddSavingsGoalDialog(
    onDismiss: () -> Unit,
    onSave: (title: String, targetAmount: Double) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var targetText by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(AppStrings.addGoalTitle, fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text(AppStrings.goalTitleLabel) },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = targetText,
                    onValueChange = { targetText = it },
                    label = { Text(AppStrings.targetAmountLabel) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val target = targetText.toDoubleOrNull() ?: 0.0
                    if (title.isNotBlank() && target > 0) {
                        onSave(title, target)
                    }
                }
            ) {
                Text(AppStrings.createGoal)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text(AppStrings.cancel) }
        }
    )
}

@Composable
private fun SetWalletLimitDialog(
    wallet: WalletEntity,
    onDismiss: () -> Unit,
    onSaveLimit: (Double) -> Unit
) {
    var limitText by remember { mutableStateOf(if (wallet.monthlyLimit > 0) String.format("%.0f", wallet.monthlyLimit) else "") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("${AppStrings.walletMonthlyLimit}: ${wallet.name}", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = AppStrings.walletMonthlyLimitSetting,
                    style = MaterialTheme.typography.bodyMedium
                )
                OutlinedTextField(
                    value = limitText,
                    onValueChange = { limitText = it },
                    label = { Text("${AppStrings.amountLabel}") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val limit = limitText.toDoubleOrNull() ?: 0.0
                    onSaveLimit(limit)
                }
            ) {
                Text(AppStrings.saveTransaction)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text(AppStrings.cancel) }
        }
    )
}

@Composable
private fun SetOverallLimitDialog(
    currentLimit: Double,
    onDismiss: () -> Unit,
    onSaveLimit: (Double) -> Unit
) {
    var limitText by remember { mutableStateOf(if (currentLimit > 0) String.format("%.0f", currentLimit) else "") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(AppStrings.setOverallLimit, fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = AppStrings.overallLimitTitle,
                    style = MaterialTheme.typography.bodyMedium
                )
                OutlinedTextField(
                    value = limitText,
                    onValueChange = { limitText = it },
                    label = { Text(AppStrings.walletMonthlyLimitSetting) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val limit = limitText.toDoubleOrNull() ?: 0.0
                    onSaveLimit(limit)
                }
            ) {
                Text(AppStrings.saveTransaction)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text(AppStrings.cancel) }
        }
    )
}
