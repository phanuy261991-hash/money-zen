package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.widget.Toast
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Android
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CleaningServices
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.ExpenseRed
import com.example.ui.viewmodel.AppThemeMode
import com.example.utils.AppStrings

import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Pin
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults

import androidx.compose.foundation.Image
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.HorizontalDivider
import androidx.compose.ui.res.painterResource
import com.example.R

@Composable
fun SettingsDataScreen(
    currentThemeMode: AppThemeMode,
    onSelectThemeMode: (AppThemeMode) -> Unit,
    totalTransactionsCount: Int,
    currentLanguage: String = "VI",
    onSelectLanguage: (String) -> Unit = {},
    currentCurrency: String = "VND",
    onSelectCurrency: (String) -> Unit = {},
    isBiometricEnabled: Boolean = false,
    onToggleBiometric: (Boolean) -> Unit = {},
    onLockAppNow: () -> Unit = {},
    onChangePin: (String) -> Unit = {},
    onTestBiometricAuth: () -> Unit = {},
    onOpenUserGuide: () -> Unit = {},
    onInsertSampleData: () -> Unit,
    onClearAllData: () -> Unit,
    onGetCsvData: () -> String
) {
    val context = LocalContext.current
    var showClearConfirmDialog by remember { mutableStateOf(false) }
    var showChangePinDialog by remember { mutableStateOf(false) }
    var showAboutOptionsDialog by remember { mutableStateOf(false) }
    var showAppInfoModal by remember { mutableStateOf(false) }
    var isDataExpanded by remember { mutableStateOf(false) }
    var newPinText by remember { mutableStateOf("") }

    // About Section Options Dialog (when clicking Giới Thiệu)
    if (showAboutOptionsDialog) {
        AlertDialog(
            onDismissRequest = { showAboutOptionsDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Info, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(AppStrings.aboutSectionTitle, fontWeight = FontWeight.Bold)
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = AppStrings.aboutSectionSubTitle,
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(4.dp))

                    // Option 1: Hướng dẫn sử dụng
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                showAboutOptionsDialog = false
                                onOpenUserGuide()
                            }
                            .testTag("dialog_option_user_guide")
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = Color(0xFF00A8FF))
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(AppStrings.optionUserGuide, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                Text(AppStrings.optionUserGuideDesc, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, modifier = Modifier.size(16.dp))
                        }
                    }

                    // Option 2: Thông tin ứng dụng
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                showAboutOptionsDialog = false
                                showAppInfoModal = true
                            }
                            .testTag("dialog_option_app_info")
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Info, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(AppStrings.optionAppInfo, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                Text(AppStrings.optionAppInfoDesc, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, modifier = Modifier.size(16.dp))
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showAboutOptionsDialog = false }) {
                    Text(AppStrings.cancel)
                }
            }
        )
    }

    // App Information Modal Dialog
    if (showAppInfoModal) {
        AlertDialog(
            onDismissRequest = { showAppInfoModal = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Info, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(AppStrings.appInfoDialogTitle, fontWeight = FontWeight.Bold)
                }
            },
            text = {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = Color(0xFF0F172A),
                            modifier = Modifier.size(52.dp)
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.app_logo),
                                contentDescription = null,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(AppStrings.appNameDesc, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                            Text(AppStrings.appInfoVersionLabel, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                    Text(AppStrings.appInfoDescText, fontSize = 12.sp, lineHeight = 18.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)

                    Spacer(modifier = Modifier.height(12.dp))
                    Text(AppStrings.appInfoKeyFeatures, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(AppStrings.appInfoFeature1, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(AppStrings.appInfoFeature2, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(AppStrings.appInfoFeature3, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(AppStrings.appInfoFeature4, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            },
            confirmButton = {
                Button(onClick = { showAppInfoModal = false }) {
                    Text(AppStrings.closeBtn)
                }
            }
        )
    }

    if (showChangePinDialog) {
        AlertDialog(
            onDismissRequest = { showChangePinDialog = false },
            title = { Text(AppStrings.changePinTitle) },
            text = {
                Column {
                    Text(AppStrings.changePinDesc)
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = newPinText,
                        onValueChange = { if (it.length <= 4 && it.all { c -> c.isDigit() }) newPinText = it },
                        label = { Text(AppStrings.newPinLabel) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newPinText.length == 4) {
                            onChangePin(newPinText)
                            Toast.makeText(context, if (currentLanguage == "EN") "PIN updated successfully!" else "Đã cập nhật mã PIN mới!", Toast.LENGTH_SHORT).show()
                            showChangePinDialog = false
                            newPinText = ""
                        }
                    },
                    enabled = newPinText.length == 4
                ) {
                    Text(AppStrings.savePin)
                }
            },
            dismissButton = {
                TextButton(onClick = { showChangePinDialog = false }) {
                    Text(AppStrings.cancel)
                }
            }
        )
    }

    if (showClearConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showClearConfirmDialog = false },
            title = { Text(AppStrings.clearDataTitle) },
            text = { Text(AppStrings.clearDataMsg) },
            confirmButton = {
                Button(
                    onClick = {
                        onClearAllData()
                        showClearConfirmDialog = false
                        Toast.makeText(context, if (currentLanguage == "EN") "All data cleared!" else "Đã xóa toàn bộ dữ liệu", Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ExpenseRed),
                    modifier = Modifier.testTag("confirm_clear_data_btn")
                ) {
                    Text(AppStrings.clearDataConfirm)
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearConfirmDialog = false }) {
                    Text(AppStrings.cancel)
                }
            }
        )
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item { Spacer(modifier = Modifier.height(8.dp)) }

        // Privacy Banner
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.Security,
                        contentDescription = AppStrings.privacyBannerTitle,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(36.dp)
                    )
                    Spacer(modifier = Modifier.width(14.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = AppStrings.privacyBannerTitle,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = AppStrings.privacyBannerDesc,
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.85f)
                        )
                    }
                }
            }
        }

        // Biometric Security & Lock Screen Card
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
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.Fingerprint,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = AppStrings.securityTitle,
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                                )
                                Text(
                                    text = if (isBiometricEnabled) AppStrings.securityEnabled else AppStrings.securityDisabled,
                                    fontSize = 12.sp,
                                    color = if (isBiometricEnabled) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Switch(
                            checked = isBiometricEnabled,
                            onCheckedChange = { enabled ->
                                onToggleBiometric(enabled)
                                Toast.makeText(
                                    context,
                                    if (enabled) "Đã bật bảo mật vân tay & khóa thiết bị!" else "Đã tắt bảo mật!",
                                    Toast.LENGTH_SHORT
                                ).show()
                            },
                            colors = SwitchDefaults.colors(checkedThumbColor = Color.White),
                            modifier = Modifier.testTag("biometric_switch")
                        )
                    }

                    if (isBiometricEnabled) {
                        Spacer(modifier = Modifier.height(14.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = onLockAppNow,
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("lock_app_now_btn")
                            ) {
                                Icon(Icons.Default.Lock, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(AppStrings.lockNow, fontSize = 11.sp)
                            }

                            OutlinedButton(
                                onClick = { showChangePinDialog = true },
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("change_pin_btn")
                            ) {
                                Icon(Icons.Default.Pin, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(AppStrings.changePin, fontSize = 11.sp)
                            }
                        }
                    }
                }
            }
        }

        // Theme Mode Selection Card
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.Palette,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = AppStrings.themeTitle,
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        val modes = listOf(
                            Triple(AppThemeMode.LIGHT, AppStrings.themeLight, Icons.Default.LightMode),
                            Triple(AppThemeMode.DARK, AppStrings.themeDark, Icons.Default.DarkMode),
                            Triple(AppThemeMode.SYSTEM, AppStrings.themeSystem, Icons.Default.Android)
                        )

                        modes.forEach { (mode, label, icon) ->
                            val isSelected = currentThemeMode == mode
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { onSelectThemeMode(mode) }
                                    .testTag("theme_mode_${mode.name.lowercase()}")
                            ) {
                                Column(
                                    modifier = Modifier.padding(vertical = 12.dp, horizontal = 8.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Icon(
                                        imageVector = icon,
                                        contentDescription = label,
                                        tint = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = label,
                                        fontSize = 12.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                        color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Language Selection Card
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.Language,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (currentLanguage == "EN") "Language Settings" else "Ngôn Ngữ Ứng Dụng",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        val languages = listOf(
                            Triple("VI", "Tiếng Việt", "🇻🇳"),
                            Triple("EN", "English", "🇺🇸")
                        )

                        languages.forEach { (code, label, flag) ->
                            val isSelected = currentLanguage == code
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { onSelectLanguage(code) }
                                    .testTag("lang_$code")
                            ) {
                                Row(
                                    modifier = Modifier.padding(vertical = 12.dp, horizontal = 12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    Text(text = flag, fontSize = 16.sp)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = label,
                                        fontSize = 13.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                        color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Currency Selection Card
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.AttachMoney,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (currentLanguage == "EN") "Currency Unit" else "Đơn Vị Tiền Tệ",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        val currencies = listOf(
                            Triple("VND", "VND (₫)", "VN"),
                            Triple("USD", "USD ($)", "US")
                        )

                        currencies.forEach { (code, label, symbol) ->
                            val isSelected = currentCurrency == code
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { onSelectCurrency(code) }
                                    .testTag("currency_$code")
                            ) {
                                Column(
                                    modifier = Modifier.padding(vertical = 12.dp, horizontal = 8.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = label,
                                        fontSize = 13.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                        color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Data Management Card (Expandable Accordion)
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("data_management_card")
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { isDataExpanded = !isDataExpanded },
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "${AppStrings.dataTitle} ($totalTransactionsCount)",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = if (isDataExpanded) AppStrings.collapseDataOptions else AppStrings.expandDataOptions,
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }

                        Icon(
                            imageVector = if (isDataExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    if (isDataExpanded) {
                        Spacer(modifier = Modifier.height(16.dp))

                        // 1. Export CSV Option
                        Button(
                            onClick = {
                                val csvData = onGetCsvData()
                                val sendIntent = Intent().apply {
                                    action = Intent.ACTION_SEND
                                    putExtra(Intent.EXTRA_TEXT, csvData)
                                    type = "text/csv"
                                }
                                val shareIntent = Intent.createChooser(sendIntent, AppStrings.exportCsv)
                                context.startActivity(shareIntent)
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("export_csv_btn"),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(AppStrings.exportCsv)
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // 2. Copy CSV to Clipboard
                        OutlinedButton(
                            onClick = {
                                val csvData = onGetCsvData()
                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                val clip = ClipData.newPlainText("Finance_CSV", csvData)
                                clipboard.setPrimaryClip(clip)
                                Toast.makeText(context, if (currentLanguage == "EN") "CSV copied to clipboard!" else "Đã sao chép dữ liệu CSV vào bộ nhớ tạm!", Toast.LENGTH_SHORT).show()
                            },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(AppStrings.copyCsv)
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // 3. Import Sample Data Button
                        OutlinedButton(
                            onClick = {
                                onInsertSampleData()
                                Toast.makeText(context, if (currentLanguage == "EN") "Sample data added!" else "Đã tạo thêm dữ liệu mẫu!", Toast.LENGTH_SHORT).show()
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("import_sample_data_btn"),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(AppStrings.insertSample)
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // 4. Clear All Data
                        Button(
                            onClick = { showClearConfirmDialog = true },
                            colors = ButtonDefaults.buttonColors(containerColor = ExpenseRed.copy(alpha = 0.1f), contentColor = ExpenseRed),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("clear_all_data_btn"),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.Default.CleaningServices, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(AppStrings.clearAll, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // Section: Giới thiệu (About App & User Guide) - Placed at the bottom
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showAboutOptionsDialog = true }
                    .testTag("about_section_card")
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = Color(0xFF00A8FF).copy(alpha = 0.15f),
                        modifier = Modifier.size(44.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                Icons.Default.Info,
                                contentDescription = null,
                                tint = Color(0xFF00A8FF),
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(14.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = AppStrings.aboutSectionTitle,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 16.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = AppStrings.aboutSectionSubTitle,
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Button(
                        onClick = { showAboutOptionsDialog = true },
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00A8FF))
                    ) {
                        Text(AppStrings.viewDetails, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        item { Spacer(modifier = Modifier.height(80.dp)) }
    }
}
