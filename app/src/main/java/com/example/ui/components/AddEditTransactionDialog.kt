package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.TransactionEntity
import com.example.data.model.TransactionType
import com.example.data.model.WalletEntity
import com.example.ui.theme.ExpenseRed
import com.example.ui.theme.IncomeGreen
import com.example.utils.AppStrings
import com.example.utils.FormatUtils

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun AddEditTransactionDialog(
    transactionToEdit: TransactionEntity? = null,
    wallets: List<WalletEntity> = emptyList(),
    onDismiss: () -> Unit,
    onSave: (
        title: String,
        amount: Double,
        type: String,
        category: String,
        date: Long,
        note: String,
        paymentMethod: String,
        walletId: Long
    ) -> Unit
) {
    var type by remember {
        mutableStateOf(transactionToEdit?.type ?: TransactionType.EXPENSE.name)
    }
    var amountText by remember {
        mutableStateOf(transactionToEdit?.let { String.format("%.0f", it.amount) } ?: "")
    }
    var title by remember {
        mutableStateOf(transactionToEdit?.title ?: "")
    }
    var selectedCategory by remember {
        mutableStateOf(
            transactionToEdit?.category ?: if (type == TransactionType.EXPENSE.name) "Ăn uống" else "Lương"
        )
    }
    var dateMs by remember {
        mutableStateOf(transactionToEdit?.date ?: System.currentTimeMillis())
    }
    var note by remember {
        mutableStateOf(transactionToEdit?.note ?: "")
    }
    var selectedWalletId by remember {
        mutableStateOf(transactionToEdit?.walletId ?: wallets.firstOrNull()?.id ?: 1L)
    }

    var showDatePicker by remember { mutableStateOf(false) }
    var amountError by remember { mutableStateOf(false) }

    val categories = if (type == TransactionType.EXPENSE.name) {
        CategoryData.expenseCategories
    } else {
        CategoryData.incomeCategories
    }

    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(initialSelectedDateMillis = dateMs)
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { dateMs = it }
                    showDatePicker = false
                }) {
                    Text("Chọn")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Hủy")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .padding(vertical = 16.dp),
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (transactionToEdit == null) AppStrings.newTransactionTitle else AppStrings.editTransactionTitle,
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    IconButton(onClick = onDismiss, modifier = Modifier.testTag("close_dialog_btn")) {
                        Icon(Icons.Default.Close, contentDescription = AppStrings.close)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Type Toggle (Thu nhập / Chi tiêu)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(
                                if (type == TransactionType.EXPENSE.name) ExpenseRed else Color.Transparent
                            )
                            .clickable {
                                type = TransactionType.EXPENSE.name
                                if (CategoryData.expenseCategories.none { it.name == selectedCategory }) {
                                    selectedCategory = "Ăn uống"
                                }
                            }
                            .padding(vertical = 12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = AppStrings.expenseToggle,
                            fontWeight = FontWeight.Bold,
                            color = if (type == TransactionType.EXPENSE.name) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(
                                if (type == TransactionType.INCOME.name) IncomeGreen else Color.Transparent
                            )
                            .clickable {
                                type = TransactionType.INCOME.name
                                if (CategoryData.incomeCategories.none { it.name == selectedCategory }) {
                                    selectedCategory = "Lương"
                                }
                            }
                            .padding(vertical = 12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = AppStrings.incomeToggle,
                            fontWeight = FontWeight.Bold,
                            color = if (type == TransactionType.INCOME.name) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Amount Input
                OutlinedTextField(
                    value = amountText,
                    onValueChange = { input ->
                        if (input.all { it.isDigit() || it == '.' }) {
                            amountText = input
                            amountError = false
                        }
                    },
                    label = { Text(AppStrings.amountLabel) },
                    isError = amountError,
                    supportingText = { if (amountError) Text(AppStrings.invalidAmount) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("amount_input"),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = if (type == TransactionType.EXPENSE.name) ExpenseRed else IncomeGreen
                    )
                )

                // Quick Amount Increments
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val isUsd = FormatUtils.currentCurrency == "USD"
                    val increments = if (isUsd) listOf(5, 10, 50, 100) else listOf(50000, 100000, 500000, 1000000)
                    increments.forEach { inc ->
                        val label = if (isUsd) "+$$inc" else when(inc) {
                            50000 -> "+50k"
                            100000 -> "+100k"
                            500000 -> "+500k"
                            else -> "+1M"
                        }
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier
                                .weight(1f)
                                .clickable {
                                    val current = amountText.toDoubleOrNull() ?: 0.0
                                    amountText = if (isUsd) String.format("%.2f", current + inc) else String.format("%.0f", current + inc)
                                    amountError = false
                                }
                        ) {
                            Text(
                                text = label,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(vertical = 6.dp),
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Wallet Selection for Money Lover
                if (wallets.isNotEmpty()) {
                    Text(
                        text = AppStrings.selectWalletLabel,
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(bottom = 6.dp)
                    )

                    var walletExpanded by remember { mutableStateOf(false) }
                    val currentWalletName = wallets.find { it.id == selectedWalletId }?.name ?: "Ví Tiền mặt"

                    ExposedDropdownMenuBox(
                        expanded = walletExpanded,
                        onExpandedChange = { walletExpanded = !walletExpanded },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        OutlinedTextField(
                            value = currentWalletName,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text(AppStrings.walletLabel) },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = walletExpanded) },
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .menuAnchor()
                                .fillMaxWidth()
                        )
                        ExposedDropdownMenu(
                            expanded = walletExpanded,
                            onDismissRequest = { walletExpanded = false }
                        ) {
                            wallets.forEach { w ->
                                DropdownMenuItem(
                                    text = { Text("${w.name} (${FormatUtils.formatCurrency(w.balance)})") },
                                    onClick = {
                                        selectedWalletId = w.id
                                        walletExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                }

                // Category Selection Grid
                Text(
                    text = AppStrings.categoryLabel,
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    categories.forEach { cat ->
                        val isSelected = selectedCategory == cat.name
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (isSelected) cat.color.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                            border = if (isSelected) androidx.compose.foundation.BorderStroke(2.dp, cat.color) else null,
                            modifier = Modifier.clickable { selectedCategory = cat.name }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                CategoryIcon(categoryName = cat.name, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = cat.name,
                                    fontSize = 13.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSelected) cat.color else MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Title Input
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text(AppStrings.titleLabel) },
                    placeholder = { Text(AppStrings.titlePlaceholder) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("title_input"),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Date Selector
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                        .clickable { showDatePicker = true }
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.CalendarToday,
                            contentDescription = "Ngày",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(AppStrings.transactionDate, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(
                                FormatUtils.formatDateFull(dateMs),
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Note Input
                OutlinedTextField(
                    value = note,
                    onValueChange = { note = it },
                    label = { Text(AppStrings.noteLabel) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    maxLines = 2
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Save Button
                Button(
                    onClick = {
                        val parsedAmount = amountText.toDoubleOrNull()
                        if (parsedAmount == null || parsedAmount <= 0) {
                            amountError = true
                        } else {
                            val activeWalletName = wallets.find { it.id == selectedWalletId }?.name ?: "Tiền mặt"
                            onSave(
                                title,
                                parsedAmount,
                                type,
                                selectedCategory,
                                dateMs,
                                note,
                                activeWalletName,
                                selectedWalletId
                            )
                            onDismiss()
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("save_transaction_btn"),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (type == TransactionType.EXPENSE.name) ExpenseRed else IncomeGreen
                    )
                ) {
                    Text(
                        text = if (transactionToEdit == null) AppStrings.saveTransaction else AppStrings.updateTransaction,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }
    }
}
