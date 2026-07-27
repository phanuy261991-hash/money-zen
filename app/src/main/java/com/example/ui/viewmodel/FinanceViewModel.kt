package com.example.ui.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.db.AppDatabase
import com.example.data.model.CategoryBudgetEntity
import com.example.data.model.DebtEntity
import com.example.data.model.RecurringBillEntity
import com.example.data.model.SavingsGoalEntity
import com.example.data.model.TransactionEntity
import com.example.data.model.TransactionType
import com.example.data.model.WalletEntity
import com.example.data.repository.FinanceRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Calendar

import com.example.utils.FormatUtils

enum class AppThemeMode {
    SYSTEM,
    LIGHT,
    DARK;

    val label: String
        get() = when (this) {
            SYSTEM -> if (FormatUtils.currentLanguage == "EN") "System Default" else "Theo hệ thống"
            LIGHT -> if (FormatUtils.currentLanguage == "EN") "Light Mode" else "Giao diện Sáng"
            DARK -> if (FormatUtils.currentLanguage == "EN") "Dark Mode" else "Giao diện Tối"
        }
}

enum class TimePeriodFilter {
    THIS_MONTH,
    LAST_MONTH,
    ALL_TIME;

    val label: String
        get() = when (this) {
            THIS_MONTH -> if (FormatUtils.currentLanguage == "EN") "This Month" else "Tháng này"
            LAST_MONTH -> if (FormatUtils.currentLanguage == "EN") "Last Month" else "Tháng trước"
            ALL_TIME -> if (FormatUtils.currentLanguage == "EN") "All Time" else "Tất cả thời gian"
        }
}

data class CategorySummary(
    val categoryName: String,
    val totalAmount: Double,
    val percentage: Float,
    val transactionCount: Int
)

data class BudgetProgress(
    val categoryName: String,
    val spentAmount: Double,
    val budgetLimit: Double,
    val ratio: Float,
    val isOverBudget: Boolean
)

data class WalletBudgetProgress(
    val wallet: WalletEntity,
    val spentAmount: Double,
    val budgetLimit: Double,
    val ratio: Float,
    val isOverLimit: Boolean
)

data class MonthComparisonData(
    val thisMonthIncome: Double = 0.0,
    val thisMonthExpense: Double = 0.0,
    val lastMonthIncome: Double = 0.0,
    val lastMonthExpense: Double = 0.0,
    val incomeChangePercent: Double = 0.0,
    val expenseChangePercent: Double = 0.0,
    val incomeDiff: Double = 0.0,
    val expenseDiff: Double = 0.0
)

class FinanceViewModel(application: Application) : AndroidViewModel(application) {
    private val database = AppDatabase.getDatabase(application)
    private val repository = FinanceRepository(
        database.transactionDao(),
        database.categoryBudgetDao(),
        database.walletDao(),
        database.debtDao(),
        database.savingsGoalDao(),
        database.recurringBillDao()
    )

    private val prefs = application.getSharedPreferences("app_settings", Context.MODE_PRIVATE)

    val themeMode = MutableStateFlow(
        try {
            AppThemeMode.valueOf(prefs.getString("theme_mode", AppThemeMode.SYSTEM.name) ?: AppThemeMode.SYSTEM.name)
        } catch (e: Exception) {
            AppThemeMode.SYSTEM
        }
    )

    fun setThemeMode(mode: AppThemeMode) {
        themeMode.value = mode
        prefs.edit().putString("theme_mode", mode.name).apply()
    }

    // Language & Currency settings
    val appLanguage = MutableStateFlow(prefs.getString("app_language", "VI") ?: "VI")
    val appCurrency = MutableStateFlow(prefs.getString("app_currency", "VND") ?: "VND")

    init {
        com.example.utils.FormatUtils.currentLanguage = appLanguage.value
        com.example.utils.FormatUtils.currentCurrency = appCurrency.value
    }

    fun setAppLanguage(lang: String) {
        appLanguage.value = lang
        com.example.utils.FormatUtils.currentLanguage = lang
        prefs.edit().putString("app_language", lang).apply()
    }

    fun setAppCurrency(currency: String) {
        appCurrency.value = currency
        com.example.utils.FormatUtils.currentCurrency = currency
        prefs.edit().putString("app_currency", currency).apply()
    }

    // Biometric / Device Security Lock State
    val isBiometricEnabled = MutableStateFlow(prefs.getBoolean("biometric_enabled", false))
    val isAppLocked = MutableStateFlow(prefs.getBoolean("biometric_enabled", false))
    val pinCode = MutableStateFlow(prefs.getString("security_pin", "1234") ?: "1234")

    fun setBiometricEnabled(enabled: Boolean) {
        isBiometricEnabled.value = enabled
        prefs.edit().putBoolean("biometric_enabled", enabled).apply()
        if (enabled) {
            isAppLocked.value = true
        } else {
            isAppLocked.value = false
        }
    }

    fun setAppLocked(locked: Boolean) {
        isAppLocked.value = locked
    }

    fun setPinCode(pin: String) {
        pinCode.value = pin
        prefs.edit().putString("security_pin", pin).apply()
    }

    fun verifyPin(inputPin: String): Boolean {
        val matches = (inputPin == pinCode.value)
        if (matches) {
            isAppLocked.value = false
        }
        return matches
    }

    val searchQuery = MutableStateFlow("")
    val selectedFilterType = MutableStateFlow("ALL") // "ALL", "INCOME", "EXPENSE", "TRANSFER"
    val selectedCategoryFilter = MutableStateFlow<String?>(null)
    val selectedPeriod = MutableStateFlow(TimePeriodFilter.THIS_MONTH)
    val selectedWalletId = MutableStateFlow<Long?>(null) // null = Tất cả các ví

    val rawTransactions: StateFlow<List<TransactionEntity>> = repository.allTransactions
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val budgets: StateFlow<List<CategoryBudgetEntity>> = repository.allBudgets
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val wallets: StateFlow<List<WalletEntity>> = repository.allWallets
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val debts: StateFlow<List<DebtEntity>> = repository.allDebts
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val savingsGoals: StateFlow<List<SavingsGoalEntity>> = repository.allSavingsGoals
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val recurringBills: StateFlow<List<RecurringBillEntity>> = repository.allRecurringBills
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Auto-populate sample data ONLY on first launch if database is empty
    init {
        viewModelScope.launch {
            val hasInitialized = prefs.getBoolean("has_initialized_data", false)
            if (!hasInitialized) {
                val list = rawTransactions.first()
                if (list.isEmpty()) {
                    repository.insertSampleData()
                }
                prefs.edit().putBoolean("has_initialized_data", true).apply()
            }
        }
    }

    private val subFilterFlow = combine(
        selectedCategoryFilter,
        selectedPeriod,
        selectedWalletId
    ) { cat, period, walletId ->
        Triple(cat, period, walletId)
    }

    // Filtered Transactions based on period, search query, type, category, and wallet
    val filteredTransactions: StateFlow<List<TransactionEntity>> = combine(
        rawTransactions,
        searchQuery,
        selectedFilterType,
        subFilterFlow
    ) { txs, query, type, subFilter ->
        val (catFilter, period, walletId) = subFilter
        val now = System.currentTimeMillis()
        val cal = Calendar.getInstance()
        cal.timeInMillis = now
        val currentMonth = cal.get(Calendar.MONTH)
        val currentYear = cal.get(Calendar.YEAR)

        cal.add(Calendar.MONTH, -1)
        val lastMonth = cal.get(Calendar.MONTH)
        val lastMonthYear = cal.get(Calendar.YEAR)

        txs.filter { tx ->
            // Wallet filter
            val matchesWallet = walletId == null || tx.walletId == walletId || tx.transferToWalletId == walletId

            // Period filter
            val matchesPeriod = when (period) {
                TimePeriodFilter.THIS_MONTH -> {
                    cal.timeInMillis = tx.date
                    cal.get(Calendar.MONTH) == currentMonth && cal.get(Calendar.YEAR) == currentYear
                }
                TimePeriodFilter.LAST_MONTH -> {
                    cal.timeInMillis = tx.date
                    cal.get(Calendar.MONTH) == lastMonth && cal.get(Calendar.YEAR) == lastMonthYear
                }
                TimePeriodFilter.ALL_TIME -> true
            }

            // Type filter
            val matchesType = when (type) {
                "INCOME" -> tx.type == TransactionType.INCOME.name
                "EXPENSE" -> tx.type == TransactionType.EXPENSE.name
                "TRANSFER" -> tx.type == TransactionType.TRANSFER.name
                else -> true
            }

            // Category filter
            val matchesCategory = catFilter == null || tx.category.equals(catFilter, ignoreCase = true)

            // Search query
            val matchesQuery = query.isBlank() ||
                    tx.title.contains(query, ignoreCase = true) ||
                    tx.category.contains(query, ignoreCase = true) ||
                    tx.note.contains(query, ignoreCase = true)

            matchesWallet && matchesPeriod && matchesType && matchesCategory && matchesQuery
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Total Wallet Balance across selected wallet or all
    val totalWalletBalance: StateFlow<Double> = combine(wallets, selectedWalletId) { walletList, walletId ->
        if (walletId == null) {
            walletList.sumOf { it.balance }
        } else {
            walletList.find { it.id == walletId }?.balance ?: 0.0
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    // Financial Totals for current selected period
    val totalIncome: StateFlow<Double> = filteredTransactions
        .map { list ->
            list.filter { it.type == TransactionType.INCOME.name }.sumOf { it.amount }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val totalExpense: StateFlow<Double> = filteredTransactions
        .map { list ->
            list.filter { it.type == TransactionType.EXPENSE.name }.sumOf { it.amount }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val netBalance: StateFlow<Double> = combine(totalIncome, totalExpense) { inc, exp ->
        inc - exp
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    // Category Expense Summary
    val categorySummaries: StateFlow<List<CategorySummary>> = filteredTransactions
        .map { list ->
            val expenses = list.filter { it.type == TransactionType.EXPENSE.name }
            val totalExp = expenses.sumOf { it.amount }

            if (totalExp == 0.0) {
                emptyList()
            } else {
                expenses.groupBy { it.category }
                    .map { (cat, catList) ->
                        val sum = catList.sumOf { it.amount }
                        CategorySummary(
                            categoryName = cat,
                            totalAmount = sum,
                            percentage = ((sum / totalExp) * 100).toFloat(),
                            transactionCount = catList.size
                        )
                    }
                    .sortedByDescending { it.totalAmount }
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Month comparison data (This Month vs Last Month)
    val monthComparisonData: StateFlow<MonthComparisonData> = rawTransactions.map { txs ->
        val cal = Calendar.getInstance()
        val now = System.currentTimeMillis()
        cal.timeInMillis = now
        val thisMonth = cal.get(Calendar.MONTH)
        val thisYear = cal.get(Calendar.YEAR)

        cal.add(Calendar.MONTH, -1)
        val lastMonth = cal.get(Calendar.MONTH)
        val lastYear = cal.get(Calendar.YEAR)

        var thisInc = 0.0
        var thisExp = 0.0
        var lastInc = 0.0
        var lastExp = 0.0

        txs.forEach { tx ->
            cal.timeInMillis = tx.date
            val m = cal.get(Calendar.MONTH)
            val y = cal.get(Calendar.YEAR)

            if (m == thisMonth && y == thisYear) {
                if (tx.type == TransactionType.INCOME.name) thisInc += tx.amount
                else if (tx.type == TransactionType.EXPENSE.name) thisExp += tx.amount
            } else if (m == lastMonth && y == lastYear) {
                if (tx.type == TransactionType.INCOME.name) lastInc += tx.amount
                else if (tx.type == TransactionType.EXPENSE.name) lastExp += tx.amount
            }
        }

        val incDiff = thisInc - lastInc
        val expDiff = thisExp - lastExp

        val incPct = if (lastInc > 0) (incDiff / lastInc) * 100.0 else if (thisInc > 0) 100.0 else 0.0
        val expPct = if (lastExp > 0) (expDiff / lastExp) * 100.0 else if (thisExp > 0) 100.0 else 0.0

        MonthComparisonData(
            thisMonthIncome = thisInc,
            thisMonthExpense = thisExp,
            lastMonthIncome = lastInc,
            lastMonthExpense = lastExp,
            incomeChangePercent = incPct,
            expenseChangePercent = expPct,
            incomeDiff = incDiff,
            expenseDiff = expDiff
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), MonthComparisonData())

    // Budget Progress Tracker
    val budgetProgresses: StateFlow<List<BudgetProgress>> = combine(rawTransactions, budgets, selectedPeriod) { txs, bgList, period ->
        val now = System.currentTimeMillis()
        val cal = Calendar.getInstance()
        cal.timeInMillis = now
        val currentMonth = cal.get(Calendar.MONTH)
        val currentYear = cal.get(Calendar.YEAR)

        cal.add(Calendar.MONTH, -1)
        val lastMonth = cal.get(Calendar.MONTH)
        val lastMonthYear = cal.get(Calendar.YEAR)

        val periodExpenses = txs.filter { tx ->
            tx.type == TransactionType.EXPENSE.name && when (period) {
                TimePeriodFilter.THIS_MONTH -> {
                    cal.timeInMillis = tx.date
                    cal.get(Calendar.MONTH) == currentMonth && cal.get(Calendar.YEAR) == currentYear
                }
                TimePeriodFilter.LAST_MONTH -> {
                    cal.timeInMillis = tx.date
                    cal.get(Calendar.MONTH) == lastMonth && cal.get(Calendar.YEAR) == lastMonthYear
                }
                TimePeriodFilter.ALL_TIME -> true
            }
        }

        val spentByCategory = periodExpenses.groupBy { it.category }
            .mapValues { entry -> entry.value.sumOf { it.amount } }

        bgList.map { bg ->
            val spent = spentByCategory[bg.categoryName] ?: 0.0
            val ratio = if (bg.monthlyLimit > 0) (spent / bg.monthlyLimit).toFloat() else 0f
            BudgetProgress(
                categoryName = bg.categoryName,
                spentAmount = spent,
                budgetLimit = bg.monthlyLimit,
                ratio = ratio,
                isOverBudget = spent > bg.monthlyLimit
            )
        }.sortedByDescending { it.ratio }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Overall Monthly Spending Limit (0 = disabled)
    val overallMonthlyLimit = MutableStateFlow(
        prefs.getFloat("overall_monthly_limit", 0f).toDouble()
    )

    fun setOverallMonthlyLimit(limit: Double) {
        overallMonthlyLimit.value = limit
        prefs.edit().putFloat("overall_monthly_limit", limit.toFloat()).apply()
    }

    // Current month expenses grouped by walletId
    val walletMonthlySpent: StateFlow<Map<Long, Double>> = rawTransactions.map { txs ->
        val now = System.currentTimeMillis()
        val cal = Calendar.getInstance()
        cal.timeInMillis = now
        val currentMonth = cal.get(Calendar.MONTH)
        val currentYear = cal.get(Calendar.YEAR)

        txs.filter { tx ->
            tx.type == TransactionType.EXPENSE.name && run {
                cal.timeInMillis = tx.date
                cal.get(Calendar.MONTH) == currentMonth && cal.get(Calendar.YEAR) == currentYear
            }
        }.groupBy { it.walletId }
         .mapValues { entry -> entry.value.sumOf { it.amount } }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyMap())

    // Wallet Budget Progress Tracker
    val walletBudgetProgresses: StateFlow<List<WalletBudgetProgress>> = combine(wallets, walletMonthlySpent) { wList, spentMap ->
        wList.map { w ->
            val spent = spentMap[w.id] ?: 0.0
            val limit = w.monthlyLimit
            val ratio = if (limit > 0) (spent / limit).toFloat() else 0f
            WalletBudgetProgress(
                wallet = w,
                spentAmount = spent,
                budgetLimit = limit,
                ratio = ratio,
                isOverLimit = limit > 0 && spent > limit
            )
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Overall Monthly Spending Limit Alert State
    val isOverallOverLimit: StateFlow<Boolean> = combine(totalExpense, overallMonthlyLimit) { spent, limit ->
        limit > 0 && spent > limit
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    // Actions
    fun addTransaction(
        title: String,
        amount: Double,
        type: String,
        category: String,
        date: Long,
        note: String,
        paymentMethod: String,
        walletId: Long = 1L,
        transferToWalletId: Long? = null
    ) {
        viewModelScope.launch {
            repository.insertTransaction(
                TransactionEntity(
                    title = title.ifBlank { category },
                    amount = amount,
                    type = type,
                    category = category,
                    date = date,
                    note = note,
                    paymentMethod = paymentMethod,
                    walletId = walletId,
                    transferToWalletId = transferToWalletId
                )
            )
        }
    }

    fun updateTransaction(transaction: TransactionEntity) {
        viewModelScope.launch {
            repository.updateTransaction(transaction)
        }
    }

    fun deleteTransaction(transaction: TransactionEntity) {
        viewModelScope.launch {
            repository.deleteTransaction(transaction)
        }
    }

    fun setBudget(categoryName: String, limit: Double) {
        viewModelScope.launch {
            repository.setCategoryBudget(categoryName, limit)
        }
    }

    // Money Lover Wallet Actions
    fun addWallet(name: String, type: String, initialBalance: Double, monthlyLimit: Double = 0.0) {
        viewModelScope.launch {
            repository.insertWallet(
                WalletEntity(
                    name = name,
                    type = type,
                    balance = initialBalance,
                    monthlyLimit = monthlyLimit,
                    colorHex = when(type) {
                        "NGAN_HANG" -> "#2563EB"
                        "VI_DIEN_TU" -> "#EC4899"
                        "THE_TIN_DUNG" -> "#F59E0B"
                        else -> "#10B981"
                    }
                )
            )
        }
    }

    fun setWalletMonthlyLimit(walletId: Long, monthlyLimit: Double) {
        viewModelScope.launch {
            val existing = wallets.value.find { it.id == walletId }
            if (existing != null) {
                repository.insertWallet(existing.copy(monthlyLimit = monthlyLimit))
            }
        }
    }

    fun transferMoney(fromWalletId: Long, toWalletId: Long, amount: Double, note: String) {
        viewModelScope.launch {
            repository.transferBetweenWallets(fromWalletId, toWalletId, amount, note)
        }
    }

    fun deleteWallet(wallet: WalletEntity) {
        viewModelScope.launch {
            repository.deleteWallet(wallet)
        }
    }

    // Debt Actions
    fun addDebt(personName: String, amount: Double, type: String, note: String, walletId: Long) {
        viewModelScope.launch {
            repository.insertDebt(
                DebtEntity(
                    personName = personName,
                    amount = amount,
                    type = type,
                    note = note,
                    walletId = walletId
                )
            )
        }
    }

    fun settleDebt(debt: DebtEntity) {
        viewModelScope.launch {
            repository.settleDebt(debt)
        }
    }

    fun deleteDebt(debt: DebtEntity) {
        viewModelScope.launch {
            repository.deleteDebt(debt)
        }
    }

    // Savings Goals Actions
    fun addSavingsGoal(title: String, targetAmount: Double) {
        viewModelScope.launch {
            repository.insertSavingsGoal(SavingsGoalEntity(title = title, targetAmount = targetAmount))
        }
    }

    fun depositToSavingsGoal(goalId: Long, amount: Double, walletId: Long) {
        viewModelScope.launch {
            repository.depositToSavingsGoal(goalId, amount, walletId)
        }
    }

    fun deleteSavingsGoal(goal: SavingsGoalEntity) {
        viewModelScope.launch {
            repository.deleteSavingsGoal(goal)
        }
    }

    // Recurring Bills Actions
    fun addRecurringBill(title: String, amount: Double, category: String, frequency: String, walletId: Long) {
        viewModelScope.launch {
            repository.insertRecurringBill(
                RecurringBillEntity(
                    title = title,
                    amount = amount,
                    category = category,
                    frequency = frequency,
                    walletId = walletId
                )
            )
        }
    }

    fun payRecurringBill(bill: RecurringBillEntity) {
        viewModelScope.launch {
            repository.payRecurringBill(bill)
        }
    }

    fun deleteRecurringBill(bill: RecurringBillEntity) {
        viewModelScope.launch {
            repository.deleteRecurringBill(bill)
        }
    }

    fun clearAllData() {
        viewModelScope.launch {
            repository.clearAllData()
            prefs.edit().putBoolean("has_initialized_data", true).apply()
        }
    }

    fun insertSampleData() {
        viewModelScope.launch {
            repository.insertSampleData()
        }
    }

    fun exportCsvData(): String {
        return repository.exportTransactionsToCsv(rawTransactions.value)
    }
}
