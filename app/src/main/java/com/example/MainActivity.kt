package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.AccountBalanceWallet
import androidx.compose.material.icons.outlined.Analytics
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Receipt
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.model.TransactionEntity
import com.example.ui.components.AddEditTransactionDialog
import com.example.ui.components.BudgetDialog
import android.widget.Toast
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.compose.ui.platform.LocalContext
import com.example.ui.screens.BiometricLockScreen
import com.example.ui.screens.AnalyticsBudgetScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.SettingsDataScreen
import com.example.ui.screens.TransactionsScreen
import com.example.ui.screens.WalletsToolsScreen
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.FinanceTheme
import com.example.ui.viewmodel.AppThemeMode
import com.example.ui.viewmodel.FinanceViewModel
import com.example.utils.AppStrings

sealed class NavigationTab(
    val route: String,
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
) {
    object Home : NavigationTab("home", "Tổng quan", Icons.Filled.Home, Icons.Outlined.Home)
    object Transactions : NavigationTab("transactions", "Sổ giao dịch", Icons.Filled.Receipt, Icons.Outlined.Receipt)
    object Tools : NavigationTab("tools", "Ví & Sổ nợ", Icons.Filled.AccountBalanceWallet, Icons.Outlined.AccountBalanceWallet)
    object Analytics : NavigationTab("analytics", "Báo cáo", Icons.Filled.Analytics, Icons.Outlined.Analytics)
    object Settings : NavigationTab("settings", "Cài đặt", Icons.Filled.Settings, Icons.Outlined.Settings)
}

class MainActivity : FragmentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MainAppScreen()
        }
    }
}

fun triggerBiometricAuth(
    activity: FragmentActivity,
    onSuccess: () -> Unit,
    onError: (String) -> Unit
) {
    val executor = ContextCompat.getMainExecutor(activity)
    val biometricPrompt = BiometricPrompt(
        activity,
        executor,
        object : BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                super.onAuthenticationSucceeded(result)
                onSuccess()
            }

            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                super.onAuthenticationError(errorCode, errString)
                onError(errString.toString())
            }

            override fun onAuthenticationFailed() {
                super.onAuthenticationFailed()
                onError("Xác thực thất bại. Vui lòng thử lại.")
            }
        }
    )

    val promptInfo = BiometricPrompt.PromptInfo.Builder()
        .setTitle("Xác thực bảo mật")
        .setSubtitle("Sử dụng vân tay, khuôn mặt hoặc khóa màn hình thiết bị")
        .setAllowedAuthenticators(
            BiometricManager.Authenticators.BIOMETRIC_STRONG or
            BiometricManager.Authenticators.DEVICE_CREDENTIAL
        )
        .build()

    try {
        biometricPrompt.authenticate(promptInfo)
    } catch (e: Exception) {
        onError(e.localizedMessage ?: "Thiết bị chưa thiết lập khóa màn hình")
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainAppScreen(viewModel: FinanceViewModel = viewModel()) {
    val themeMode by viewModel.themeMode.collectAsStateWithLifecycle()
    val isBiometricEnabled by viewModel.isBiometricEnabled.collectAsStateWithLifecycle()
    val isAppLocked by viewModel.isAppLocked.collectAsStateWithLifecycle()

    val context = LocalContext.current
    val activity = context as? FragmentActivity

    val systemInDark = isSystemInDarkTheme()
    val isDarkTheme = when (themeMode) {
        AppThemeMode.SYSTEM -> systemInDark
        AppThemeMode.LIGHT -> false
        AppThemeMode.DARK -> true
    }

    FinanceTheme(darkTheme = isDarkTheme) {
        if (isAppLocked) {
            BiometricLockScreen(
                onTriggerBiometric = {
                    if (activity != null) {
                        triggerBiometricAuth(
                            activity = activity,
                            onSuccess = { viewModel.setAppLocked(false) },
                            onError = { err ->
                                Toast.makeText(context, err, Toast.LENGTH_SHORT).show()
                            }
                        )
                    } else {
                        Toast.makeText(context, "Thiết bị không hỗ trợ FragmentActivity", Toast.LENGTH_SHORT).show()
                    }
                },
                onVerifyPin = { pin -> viewModel.verifyPin(pin) }
            )
        } else {
            var selectedTab by remember { mutableIntStateOf(0) }

            // Observe ViewModel States
            val rawTransactions by viewModel.rawTransactions.collectAsStateWithLifecycle()
            val filteredTransactions by viewModel.filteredTransactions.collectAsStateWithLifecycle()
            val totalIncome by viewModel.totalIncome.collectAsStateWithLifecycle()
            val totalExpense by viewModel.totalExpense.collectAsStateWithLifecycle()
            val netBalance by viewModel.netBalance.collectAsStateWithLifecycle()
            val categorySummaries by viewModel.categorySummaries.collectAsStateWithLifecycle()
            val budgetProgresses by viewModel.budgetProgresses.collectAsStateWithLifecycle()
            val monthComparisonData by viewModel.monthComparisonData.collectAsStateWithLifecycle()
            val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
            val selectedFilterType by viewModel.selectedFilterType.collectAsStateWithLifecycle()
            val selectedCategoryFilter by viewModel.selectedCategoryFilter.collectAsStateWithLifecycle()
            val selectedPeriod by viewModel.selectedPeriod.collectAsStateWithLifecycle()

            // Money Lover Money & Wallet States
            val wallets by viewModel.wallets.collectAsStateWithLifecycle()
            val selectedWalletId by viewModel.selectedWalletId.collectAsStateWithLifecycle()
            val totalWalletBalance by viewModel.totalWalletBalance.collectAsStateWithLifecycle()
            val debts by viewModel.debts.collectAsStateWithLifecycle()
            val savingsGoals by viewModel.savingsGoals.collectAsStateWithLifecycle()
            val recurringBills by viewModel.recurringBills.collectAsStateWithLifecycle()

            val appLanguage by viewModel.appLanguage.collectAsStateWithLifecycle()
            val appCurrency by viewModel.appCurrency.collectAsStateWithLifecycle()

            val walletBudgetProgresses by viewModel.walletBudgetProgresses.collectAsStateWithLifecycle()
            val overallMonthlyLimit by viewModel.overallMonthlyLimit.collectAsStateWithLifecycle()
            val isOverallOverLimit by viewModel.isOverallOverLimit.collectAsStateWithLifecycle()

        // Dialog state
        var showAddDialog by remember { mutableStateOf(false) }
        var transactionToEdit by remember { mutableStateOf<TransactionEntity?>(null) }
        var categoryForBudgetDialog by remember { mutableStateOf<Pair<String, Double>?>(null) }

        // Dialogs rendering
        if (showAddDialog || transactionToEdit != null) {
            AddEditTransactionDialog(
                transactionToEdit = transactionToEdit,
                wallets = wallets,
                onDismiss = {
                    showAddDialog = false
                    transactionToEdit = null
                },
                onSave = { title, amount, type, category, date, note, paymentMethod, walletId ->
                    if (transactionToEdit != null) {
                        viewModel.updateTransaction(
                            transactionToEdit!!.copy(
                                title = title,
                                amount = amount,
                                type = type,
                                category = category,
                                date = date,
                                note = note,
                                paymentMethod = paymentMethod,
                                walletId = walletId
                            )
                        )
                    } else {
                        viewModel.addTransaction(
                            title = title,
                            amount = amount,
                            type = type,
                            category = category,
                            date = date,
                            note = note,
                            paymentMethod = paymentMethod,
                            walletId = walletId
                        )
                    }
                }
            )
        }

        categoryForBudgetDialog?.let { (catName, currentLimit) ->
            BudgetDialog(
                categoryName = catName,
                currentLimit = currentLimit,
                onDismiss = { categoryForBudgetDialog = null },
                onSaveLimit = { name, limit ->
                    viewModel.setBudget(name, limit)
                }
            )
        }

        Scaffold(
            bottomBar = {
                BottomAppBar(
                    windowInsets = WindowInsets.navigationBars,
                    containerColor = MaterialTheme.colorScheme.surface,
                    tonalElevation = 8.dp
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Left Tab 0: Home
                        BottomTabItem(
                            tab = NavigationTab.Home,
                            isSelected = selectedTab == 0,
                            onClick = { selectedTab = 0 }
                        )

                        // Left Tab 1: Transactions
                        BottomTabItem(
                            tab = NavigationTab.Transactions,
                            isSelected = selectedTab == 1,
                            onClick = { selectedTab = 1 }
                        )

                        // Center: Prominent Circular Add FAB Button
                        FloatingActionButton(
                            onClick = { showAddDialog = true },
                            containerColor = EmeraldPrimary,
                            contentColor = Color.White,
                            shape = CircleShape,
                            elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 6.dp),
                            modifier = Modifier
                                .size(52.dp)
                                .testTag("add_transaction_center_fab")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Thêm Giao Dịch",
                                modifier = Modifier.size(28.dp)
                            )
                        }

                        // Right Tab 2: Wallets & Tools
                        BottomTabItem(
                            tab = NavigationTab.Tools,
                            isSelected = selectedTab == 2,
                            onClick = { selectedTab = 2 }
                        )

                        // Right Tab 3: Settings
                        BottomTabItem(
                            tab = NavigationTab.Settings,
                            isSelected = selectedTab == 3,
                            onClick = { selectedTab = 3 }
                        )
                    }
                }
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
                    .padding(innerPadding)
            ) {
                when (selectedTab) {
                    0 -> HomeScreen(
                        totalWalletBalance = totalWalletBalance,
                        wallets = wallets,
                        selectedWalletId = selectedWalletId,
                        onSelectWallet = { viewModel.selectedWalletId.value = it },
                        totalIncome = totalIncome,
                        totalExpense = totalExpense,
                        netBalance = netBalance,
                        selectedPeriod = selectedPeriod,
                        onSelectPeriod = { viewModel.selectedPeriod.value = it },
                        recentTransactions = rawTransactions,
                        categorySummaries = categorySummaries,
                        budgetProgresses = budgetProgresses,
                        savingsGoals = savingsGoals,
                        recurringBills = recurringBills,
                        isOverallOverLimit = isOverallOverLimit,
                        onAddClick = { showAddDialog = true },
                        onViewAllTransactions = { selectedTab = 1 },
                        onTransactionClick = { tx -> transactionToEdit = tx },
                        onViewAnalytics = { selectedTab = 4 }
                    )

                    1 -> TransactionsScreen(
                        transactions = filteredTransactions,
                        searchQuery = searchQuery,
                        onSearchChange = { viewModel.searchQuery.value = it },
                        selectedFilterType = selectedFilterType,
                        onSelectFilterType = { viewModel.selectedFilterType.value = it },
                        selectedCategoryFilter = selectedCategoryFilter,
                        onSelectCategoryFilter = { viewModel.selectedCategoryFilter.value = it },
                        onTransactionClick = { tx -> transactionToEdit = tx },
                        onAddClick = { showAddDialog = true }
                    )

                    2 -> WalletsToolsScreen(
                        wallets = wallets,
                        debts = debts,
                        savingsGoals = savingsGoals,
                        recurringBills = recurringBills,
                        walletBudgetProgresses = walletBudgetProgresses,
                        overallMonthlyLimit = overallMonthlyLimit,
                        overallMonthlySpent = totalExpense,
                        isOverallOverLimit = isOverallOverLimit,
                        onAddWallet = { name, type, bal, limit -> viewModel.addWallet(name, type, bal, limit) },
                        onSetWalletLimit = { walletId, limit -> viewModel.setWalletMonthlyLimit(walletId, limit) },
                        onSetOverallLimit = { limit -> viewModel.setOverallMonthlyLimit(limit) },
                        onDeleteWallet = { wallet -> viewModel.deleteWallet(wallet) },
                        onTransferMoney = { fromId, toId, amount, note -> viewModel.transferMoney(fromId, toId, amount, note) },
                        onAddDebt = { person, amt, type, note, wId -> viewModel.addDebt(person, amt, type, note, wId) },
                        onSettleDebt = { debt -> viewModel.settleDebt(debt) },
                        onDeleteDebt = { debt -> viewModel.deleteDebt(debt) },
                        onAddSavingsGoal = { title, target -> viewModel.addSavingsGoal(title, target) },
                        onDeleteSavingsGoal = { goal -> viewModel.deleteSavingsGoal(goal) },
                        onAddRecurringBill = { title, amt, cat, freq, wId -> viewModel.addRecurringBill(title, amt, cat, freq, wId) },
                        onPayRecurringBill = { bill -> viewModel.payRecurringBill(bill) },
                        onDeleteRecurringBill = { bill -> viewModel.deleteRecurringBill(bill) }
                    )

                    3 -> SettingsDataScreen(
                        currentThemeMode = themeMode,
                        onSelectThemeMode = { viewModel.setThemeMode(it) },
                        totalTransactionsCount = rawTransactions.size,
                        currentLanguage = appLanguage,
                        onSelectLanguage = { viewModel.setAppLanguage(it) },
                        currentCurrency = appCurrency,
                        onSelectCurrency = { viewModel.setAppCurrency(it) },
                        isBiometricEnabled = isBiometricEnabled,
                        onToggleBiometric = { viewModel.setBiometricEnabled(it) },
                        onLockAppNow = { viewModel.setAppLocked(true) },
                        onChangePin = { viewModel.setPinCode(it) },
                        onTestBiometricAuth = {
                            if (activity != null) {
                                triggerBiometricAuth(
                                    activity = activity,
                                    onSuccess = { Toast.makeText(context, "Xác thực thành công!", Toast.LENGTH_SHORT).show() },
                                    onError = { err -> Toast.makeText(context, err, Toast.LENGTH_SHORT).show() }
                                )
                            }
                        },
                        onInsertSampleData = { viewModel.insertSampleData() },
                        onClearAllData = { viewModel.clearAllData() },
                        onGetCsvData = { viewModel.exportCsvData() }
                    )

                    4 -> AnalyticsBudgetScreen(
                        totalIncome = totalIncome,
                        totalExpense = totalExpense,
                        categorySummaries = categorySummaries,
                        budgetProgresses = budgetProgresses,
                        monthComparisonData = monthComparisonData,
                        onSetBudgetClick = { catName, limit ->
                            categoryForBudgetDialog = catName to limit
                        },
                        onBackClick = { selectedTab = 0 }
                    )
                }
            }
        }
    }
}
}

@Composable
private fun BottomTabItem(
    tab: NavigationTab,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val title = when (tab) {
        NavigationTab.Home -> AppStrings.tabHome
        NavigationTab.Transactions -> AppStrings.tabTransactions
        NavigationTab.Tools -> AppStrings.tabWallets
        NavigationTab.Analytics -> AppStrings.tabAnalytics
        NavigationTab.Settings -> AppStrings.tabSettings
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .clickable(onClick = onClick)
            .padding(horizontal = 4.dp, vertical = 2.dp)
            .testTag("nav_tab_${tab.route}")
    ) {
        Icon(
            imageVector = if (isSelected) tab.selectedIcon else tab.unselectedIcon,
            contentDescription = title,
            tint = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = title,
            fontSize = 10.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
