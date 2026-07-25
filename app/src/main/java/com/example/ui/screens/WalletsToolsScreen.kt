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
import com.example.utils.AppStrings
import com.example.utils.FormatUtils

@Composable
fun WalletsToolsScreen(
    wallets: List<WalletEntity>,
    debts: List<DebtEntity>,
    savingsGoals: List<SavingsGoalEntity>,
    recurringBills: List<RecurringBillEntity>,
    onAddWallet: (name: String, type: String, initialBalance: Double) -> Unit,
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
                    onOpenAddWallet = { showAddWalletDialog = true },
                    onOpenTransfer = { showTransferDialog = true },
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
            onSave = { name, type, balance ->
                onAddWallet(name, type, balance)
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
}

@Composable
private fun WalletsTabContent(
    wallets: List<WalletEntity>,
    onOpenAddWallet: () -> Unit,
    onOpenTransfer: () -> Unit,
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

        item {
            Text(
                text = "${AppStrings.tabWalletsList} (${wallets.size})",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                modifier = Modifier.padding(vertical = 4.dp)
            )
        }

        items(wallets, key = { it.id }) { wallet ->
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
                    val (icon, color) = when (wallet.type) {
                        "NGAN_HANG" -> Icons.Default.AccountBalance to Color(0xFF2563EB)
                        "VI_DIEN_TU" -> Icons.Default.Smartphone to Color(0xFFEC4899)
                        "THE_TIN_DUNG" -> Icons.Default.CreditCard to Color(0xFFF59E0B)
                        else -> Icons.Default.Payments to Color(0xFF10B981)
                    }

                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(color.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(24.dp))
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = wallet.name,
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                        Spacer(modifier = Modifier.height(2.dp))
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

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = FormatUtils.formatCurrency(wallet.balance),
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = if (wallet.balance >= 0) MaterialTheme.colorScheme.onSurface else ExpenseRed
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        IconButton(
                            onClick = { walletToDelete = wallet },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.DeleteOutline,
                                contentDescription = AppStrings.deleteBtn,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                                modifier = Modifier.size(20.dp)
                            )
                        }
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
                            Text(if (FormatUtils.currentLanguage == "EN") "Add Debt" else "Ghi Nợ Mới")
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(if (FormatUtils.currentLanguage == "EN") "Lent (To Collect)" else "Cho Vay (Cần thu)", style = MaterialTheme.typography.bodySmall)
                            Text(
                                FormatUtils.formatCurrency(totalLent),
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = EmeraldPrimary
                            )
                        }

                        Column(modifier = Modifier.weight(1f)) {
                            Text(if (FormatUtils.currentLanguage == "EN") "Borrowed (To Pay)" else "Đi Vay (Cần trả)", style = MaterialTheme.typography.bodySmall)
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
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val isLent = debt.type == "CHO_VAY"
                    val tagColor = if (isLent) EmeraldPrimary else ExpenseRed

                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(tagColor.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (isLent) Icons.Default.MonetizationOn else Icons.Default.Receipt,
                            contentDescription = null,
                            tint = tagColor,
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = debt.personName,
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                            )
                            if (debt.isSettled) {
                                Spacer(modifier = Modifier.width(6.dp))
                                Surface(
                                    color = EmeraldPrimary.copy(alpha = 0.2f),
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Text(
                                        if (FormatUtils.currentLanguage == "EN") "Settled" else "Đã xong",
                                        color = EmeraldPrimary,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }
                        val debtTypeStr = if (isLent) {
                            if (FormatUtils.currentLanguage == "EN") "Lent" else "Cho vay"
                        } else {
                            if (FormatUtils.currentLanguage == "EN") "Borrowed" else "Đi vay"
                        }
                        Text(
                            text = "$debtTypeStr • ${debt.note}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = FormatUtils.formatCurrency(debt.amount),
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                color = tagColor
                            )
                            Spacer(modifier = Modifier.width(4.dp))
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

                        if (!debt.isSettled) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Button(
                                onClick = { onSettleDebt(debt) },
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = tagColor),
                                modifier = Modifier.height(30.dp)
                            ) {
                                val settleBtnLabel = if (isLent) {
                                    if (FormatUtils.currentLanguage == "EN") "Collect" else "Thu Nợ"
                                } else {
                                    if (FormatUtils.currentLanguage == "EN") "Pay Off" else "Trả Nợ"
                                }
                                Text(settleBtnLabel, fontSize = 11.sp)
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
                    text = AppStrings.tabSavings,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
                Button(
                    onClick = onOpenAddGoal,
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(if (FormatUtils.currentLanguage == "EN") "New Goal" else "Mục Tiêu Mới")
                }
            }
        }

        items(savingsGoals, key = { it.id }) { goal ->
            val ratio = if (goal.targetAmount > 0) (goal.currentAmount / goal.targetAmount).toFloat().coerceIn(0f, 1f) else 0f
            val percentInt = (ratio * 100).toInt()

            Card(
                shape = RoundedCornerShape(16.dp),
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
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.Savings,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = goal.title,
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                            )
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "$percentInt%",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.width(4.dp))
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
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    LinearProgressIndicator(
                        progress = { ratio },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(CircleShape),
                        color = EmeraldPrimary,
                        trackColor = MaterialTheme.colorScheme.surfaceVariant
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "${if (FormatUtils.currentLanguage == "EN") "Saved" else "Đã tiết kiệm"}: ${FormatUtils.formatCurrency(goal.currentAmount)}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "${if (FormatUtils.currentLanguage == "EN") "Target" else "Mục tiêu"}: ${FormatUtils.formatCurrency(goal.targetAmount)}",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Bold
                        )
                    }
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
                    text = AppStrings.recurringBillsManagement,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )

                Button(
                    onClick = onOpenAddBill,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
                    modifier = Modifier.testTag("add_recurring_bill_btn")
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(if (FormatUtils.currentLanguage == "EN") "Add Bill" else "Thêm Hóa Đơn", fontSize = 12.sp)
                }
            }
        }

        if (bills.isEmpty()) {
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.Receipt,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(40.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = if (FormatUtils.currentLanguage == "EN") "No recurring bills yet" else "Chưa có hóa đơn định kỳ nào",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = if (FormatUtils.currentLanguage == "EN") "Add bills like electricity, water, internet to get payment reminders." else "Thêm hóa đơn như điện, nước, internet để nhận nhắc nhở thanh toán.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(top = 4.dp, bottom = 12.dp)
                        )
                        Button(
                            onClick = onOpenAddBill,
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text(if (FormatUtils.currentLanguage == "EN") "Add New Bill" else "Thêm Hóa Đơn Mới")
                        }
                    }
                }
            }
        } else {
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
                        Icon(
                            Icons.Default.Receipt,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(28.dp)
                        )

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = bill.title,
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                            )
                            Text(
                                text = "${bill.frequency} • ${bill.category}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = FormatUtils.formatCurrency(bill.amount),
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
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

                            Spacer(modifier = Modifier.height(4.dp))

                            if (bill.isPaid) {
                                Text(AppStrings.paid, color = EmeraldPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            } else {
                                Button(
                                    onClick = { onPayBill(bill) },
                                    shape = RoundedCornerShape(8.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
                                    modifier = Modifier.height(30.dp)
                                ) {
                                    Text(if (FormatUtils.currentLanguage == "EN") "Pay Now" else "Trả Ngay", fontSize = 11.sp)
                                }
                            }
                        }
                    }
                }
            }
        }

        item { Spacer(modifier = Modifier.height(80.dp)) }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddRecurringBillDialog(
    wallets: List<WalletEntity>,
    onDismiss: () -> Unit,
    onSave: (title: String, amount: Double, category: String, frequency: String, walletId: Long) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var amountText by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("Tiền điện") }
    var selectedFrequency by remember { mutableStateOf("Hàng tháng") }
    var selectedWalletId by remember { mutableStateOf(wallets.firstOrNull()?.id ?: 1L) }

    val categories = listOf("Tiền điện", "Tiền nước", "Internet", "Tiền nhà", "Tiền học", "Khác")
    val frequencies = listOf("Hàng tháng", "Hàng tuần", "Hàng năm")

    var catExpanded by remember { mutableStateOf(false) }
    var freqExpanded by remember { mutableStateOf(false) }
    var walletExpanded by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Thêm Hóa Đơn Định Kỳ", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Tên hóa đơn (VD: Điện T8)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = amountText,
                    onValueChange = { amountText = it },
                    label = { Text("Số tiền (₫)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                // Category Dropdown
                ExposedDropdownMenuBox(
                    expanded = catExpanded,
                    onExpandedChange = { catExpanded = !catExpanded }
                ) {
                    OutlinedTextField(
                        value = selectedCategory,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Danh mục") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = catExpanded) },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
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

                // Frequency Dropdown
                ExposedDropdownMenuBox(
                    expanded = freqExpanded,
                    onExpandedChange = { freqExpanded = !freqExpanded }
                ) {
                    OutlinedTextField(
                        value = selectedFrequency,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Tần suất thanh toán") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = freqExpanded) },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
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

                // Wallet Selection
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
                            label = { Text("Trích từ ví") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = walletExpanded) },
                            modifier = Modifier
                                .menuAnchor()
                                .fillMaxWidth()
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
                Text("Lưu Hóa Đơn")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Hủy")
            }
        }
    )
}

// Dialog Composable Helpers
private data class WalletTypeOption(
    val code: String,
    val label: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val color: Color
)

@Composable
private fun AddWalletDialog(
    onDismiss: () -> Unit,
    onSave: (name: String, type: String, balance: Double) -> Unit
) {
    var walletName by remember { mutableStateOf("") }
    var selectedType by remember { mutableStateOf("TIEN_MAT") }
    var balanceText by remember { mutableStateOf("") }

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
            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
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

                // 2x2 Grid of prominent Wallet Type Cards
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
                                        modifier = Modifier.padding(vertical = 12.dp, horizontal = 10.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(32.dp)
                                                .clip(CircleShape)
                                                .background(if (isSel) option.color else MaterialTheme.colorScheme.surfaceVariant),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                imageVector = option.icon,
                                                contentDescription = null,
                                                tint = if (isSel) Color.White else option.color,
                                                modifier = Modifier.size(18.dp)
                                            )
                                        }
                                        Spacer(modifier = Modifier.width(8.dp))
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
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val bal = balanceText.toDoubleOrNull() ?: 0.0
                    if (walletName.isNotBlank()) {
                        onSave(walletName, selectedType, bal)
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
        title = { Text("Chuyển Tiền Giữa Các Ví") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text("Ví gửi (Trừ tiền): ${fromWallet.name}", fontWeight = FontWeight.Bold)
                Text("Ví nhận (Cộng tiền): ${toWallet.name}", fontWeight = FontWeight.Bold)

                OutlinedTextField(
                    value = amountText,
                    onValueChange = { amountText = it },
                    label = { Text("Số tiền chuyển (VND)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = noteText,
                    onValueChange = { noteText = it },
                    label = { Text("Ghi chú") },
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
                Text("Xác Nhận Chuyển")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Hủy") }
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
        title = { Text("Thêm Ghi Nợ Mới") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = personName,
                    onValueChange = { personName = it },
                    label = { Text("Tên người vay / cho vay") },
                    modifier = Modifier.fillMaxWidth()
                )

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                        onClick = { debtType = "CHO_VAY" },
                        colors = ButtonDefaults.buttonColors(containerColor = if (debtType == "CHO_VAY") EmeraldPrimary else Color.Gray),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Cho Vay")
                    }

                    Button(
                        onClick = { debtType = "DI_VAY" },
                        colors = ButtonDefaults.buttonColors(containerColor = if (debtType == "DI_VAY") Color.Red else Color.Gray),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Đi Vay")
                    }
                }

                OutlinedTextField(
                    value = amountText,
                    onValueChange = { amountText = it },
                    label = { Text("Số tiền (VND)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = note,
                    onValueChange = { note = it },
                    label = { Text("Ghi chú lý do") },
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
                Text("Lưu Sổ Nợ")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Hủy") }
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
        title = { Text("Tạo Mục Tiêu Tiết Kiệm Mới") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Tên mục tiêu (Ví dụ: Mua iPhone, Du lịch)") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = targetText,
                    onValueChange = { targetText = it },
                    label = { Text("Số tiền mục tiêu cần đạt (VND)") },
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
                Text("Tạo Mục Tiêu")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Hủy") }
        }
    )
}
