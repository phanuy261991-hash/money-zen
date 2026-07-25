package com.example.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.utils.AppStrings
import com.example.utils.FormatUtils

@Composable
fun BudgetDialog(
    categoryName: String,
    currentLimit: Double,
    onDismiss: () -> Unit,
    onSaveLimit: (categoryName: String, limit: Double) -> Unit
) {
    var limitText by remember {
        mutableStateOf(if (currentLimit > 0) String.format("%.0f", currentLimit) else "")
    }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    CategoryIcon(categoryName = categoryName, modifier = Modifier.padding(end = 8.dp))
                    Text(
                        text = "${AppStrings.budgetTitle}: $categoryName",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = limitText,
                    onValueChange = { input ->
                        if (input.all { it.isDigit() || it == '.' }) {
                            limitText = input
                        }
                    },
                    label = { Text(AppStrings.budgetLimitLabel) },
                    placeholder = { Text(if (FormatUtils.currentCurrency == "USD") "e.g. 500" else "Ví dụ: 5000000") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("budget_limit_input"),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )

                if (limitText.isNotBlank()) {
                    val parsed = limitText.toDoubleOrNull()
                    if (parsed != null && parsed > 0) {
                        Text(
                            text = "${FormatUtils.formatCurrency(parsed)} / ${AppStrings.periodMonth.lowercase()}",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier
                                .align(Alignment.Start)
                                .padding(top = 4.dp, start = 4.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text(AppStrings.cancel)
                    }
                    Button(
                        onClick = {
                            val limit = limitText.toDoubleOrNull() ?: 0.0
                            onSaveLimit(categoryName, limit)
                            onDismiss()
                        },
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .padding(start = 8.dp)
                            .testTag("save_budget_btn")
                    ) {
                        Text(AppStrings.budgetSave)
                    }
                }
            }
        }
    }
}
