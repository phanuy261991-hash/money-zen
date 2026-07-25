package com.example.data.repository

import com.example.data.dao.CategoryBudgetDao
import com.example.data.dao.DebtDao
import com.example.data.dao.RecurringBillDao
import com.example.data.dao.SavingsGoalDao
import com.example.data.dao.TransactionDao
import com.example.data.dao.WalletDao
import com.example.data.model.CategoryBudgetEntity
import com.example.data.model.DebtEntity
import com.example.data.model.RecurringBillEntity
import com.example.data.model.SavingsGoalEntity
import com.example.data.model.TransactionEntity
import com.example.data.model.TransactionType
import com.example.data.model.WalletEntity
import com.example.utils.FormatUtils
import kotlinx.coroutines.flow.Flow
import java.util.Calendar

class FinanceRepository(
    private val transactionDao: TransactionDao,
    private val categoryBudgetDao: CategoryBudgetDao,
    private val walletDao: WalletDao,
    private val debtDao: DebtDao,
    private val savingsGoalDao: SavingsGoalDao,
    private val recurringBillDao: RecurringBillDao
) {
    val allTransactions: Flow<List<TransactionEntity>> = transactionDao.getAllTransactions()
    val allBudgets: Flow<List<CategoryBudgetEntity>> = categoryBudgetDao.getAllBudgets()
    val allWallets: Flow<List<WalletEntity>> = walletDao.getAllWallets()
    val allDebts: Flow<List<DebtEntity>> = debtDao.getAllDebts()
    val allSavingsGoals: Flow<List<SavingsGoalEntity>> = savingsGoalDao.getAllSavingsGoals()
    val allRecurringBills: Flow<List<RecurringBillEntity>> = recurringBillDao.getAllRecurringBills()

    suspend fun insertTransaction(transaction: TransactionEntity): Long {
        val id = transactionDao.insertTransaction(transaction)
        
        // Update wallet balances according to transaction type
        val wallet = walletDao.getWalletById(transaction.walletId)
        if (wallet != null) {
            when (transaction.type) {
                TransactionType.INCOME.name -> {
                    walletDao.updateWallet(wallet.copy(balance = wallet.balance + transaction.amount))
                }
                TransactionType.EXPENSE.name -> {
                    walletDao.updateWallet(wallet.copy(balance = wallet.balance - transaction.amount))
                }
                TransactionType.TRANSFER.name -> {
                    // Deduct from source wallet
                    walletDao.updateWallet(wallet.copy(balance = wallet.balance - transaction.amount))
                    // Add to destination wallet if exists
                    transaction.transferToWalletId?.let { destId ->
                        val destWallet = walletDao.getWalletById(destId)
                        if (destWallet != null) {
                            walletDao.updateWallet(destWallet.copy(balance = destWallet.balance + transaction.amount))
                        }
                    }
                }
            }
        }
        return id
    }

    suspend fun updateTransaction(transaction: TransactionEntity) {
        transactionDao.updateTransaction(transaction)
    }

    suspend fun deleteTransaction(transaction: TransactionEntity) {
        transactionDao.deleteTransaction(transaction)
    }

    suspend fun setCategoryBudget(categoryName: String, monthlyLimit: Double) {
        categoryBudgetDao.insertOrUpdateBudget(
            CategoryBudgetEntity(categoryName = categoryName, monthlyLimit = monthlyLimit)
        )
    }

    // Wallet operations
    suspend fun insertWallet(wallet: WalletEntity): Long = walletDao.insertWallet(wallet)
    suspend fun updateWallet(wallet: WalletEntity) = walletDao.updateWallet(wallet)
    suspend fun deleteWallet(wallet: WalletEntity) = walletDao.deleteWallet(wallet)

    suspend fun transferBetweenWallets(fromWalletId: Long, toWalletId: Long, amount: Double, note: String) {
        val fromWallet = walletDao.getWalletById(fromWalletId) ?: return
        val toWallet = walletDao.getWalletById(toWalletId) ?: return

        val transferTx = TransactionEntity(
            title = "Chuyển tiền sang ${toWallet.name}",
            amount = amount,
            type = TransactionType.TRANSFER.name,
            category = "Chuyển khoản",
            date = System.currentTimeMillis(),
            note = note.ifEmpty { "Chuyển từ ${fromWallet.name} sang ${toWallet.name}" },
            paymentMethod = fromWallet.name,
            walletId = fromWalletId,
            transferToWalletId = toWalletId
        )

        insertTransaction(transferTx)
    }

    // Debt operations
    suspend fun insertDebt(debt: DebtEntity): Long = debtDao.insertDebt(debt)
    suspend fun updateDebt(debt: DebtEntity) = debtDao.updateDebt(debt)
    suspend fun deleteDebt(debt: DebtEntity) = debtDao.deleteDebt(debt)

    suspend fun settleDebt(debt: DebtEntity) {
        // Mark debt settled
        debtDao.updateDebt(debt.copy(isSettled = true))

        // Auto create repayment transaction
        val isLent = debt.type == "CHO_VAY"
        val txType = if (isLent) TransactionType.INCOME.name else TransactionType.EXPENSE.name
        val title = if (isLent) "Thu nợ từ ${debt.personName}" else "Trả nợ cho ${debt.personName}"
        val cat = if (isLent) "Thu nợ" else "Trả nợ"

        insertTransaction(
            TransactionEntity(
                title = title,
                amount = debt.amount,
                type = txType,
                category = cat,
                date = System.currentTimeMillis(),
                note = "Tất toán khoản ${if (isLent) "cho vay" else "đi vay"}: ${debt.personName}",
                walletId = debt.walletId
            )
        )
    }

    // Savings Goals operations
    suspend fun insertSavingsGoal(goal: SavingsGoalEntity): Long = savingsGoalDao.insertGoal(goal)
    suspend fun updateSavingsGoal(goal: SavingsGoalEntity) = savingsGoalDao.updateGoal(goal)
    suspend fun deleteSavingsGoal(goal: SavingsGoalEntity) = savingsGoalDao.deleteGoal(goal)

    suspend fun depositToSavingsGoal(goalId: Long, amount: Double, walletId: Long) {
        val goalList = savingsGoalDao.getAllSavingsGoals()
        // Simple update
        val wallet = walletDao.getWalletById(walletId)
        if (wallet != null) {
            insertTransaction(
                TransactionEntity(
                    title = "Gửi tiết kiệm",
                    amount = amount,
                    type = TransactionType.EXPENSE.name,
                    category = "Tiết kiệm",
                    date = System.currentTimeMillis(),
                    note = "Tích lũy vào mục tiêu tiết kiệm",
                    walletId = walletId
                )
            )
        }
    }

    // Recurring Bill operations
    suspend fun insertRecurringBill(bill: RecurringBillEntity): Long = recurringBillDao.insertBill(bill)
    suspend fun updateRecurringBill(bill: RecurringBillEntity) = recurringBillDao.updateBill(bill)
    suspend fun deleteRecurringBill(bill: RecurringBillEntity) = recurringBillDao.deleteBill(bill)

    suspend fun payRecurringBill(bill: RecurringBillEntity) {
        recurringBillDao.updateBill(bill.copy(isPaid = true))
        insertTransaction(
            TransactionEntity(
                title = bill.title,
                amount = bill.amount,
                type = TransactionType.EXPENSE.name,
                category = bill.category,
                date = System.currentTimeMillis(),
                note = "Thanh toán hóa đơn định kỳ: ${bill.title}",
                walletId = bill.walletId
            )
        )
    }

    suspend fun clearAllData() {
        transactionDao.clearAllTransactions()
        categoryBudgetDao.clearAllBudgets()
        walletDao.clearAllWallets()
        debtDao.clearAllDebts()
        savingsGoalDao.clearAllGoals()
        recurringBillDao.clearAllBills()
    }

    suspend fun insertSampleData() {
        clearAllData()

        val now = System.currentTimeMillis()

        // 1. Wallets
        val w1 = WalletEntity(id = 1L, name = "Ví Tiền mặt", type = "TIEN_MAT", balance = 3500000.0, iconName = "Payments", colorHex = "#10B981")
        val w2 = WalletEntity(id = 2L, name = "Vietcombank", type = "NGAN_HANG", balance = 24500000.0, iconName = "AccountBalance", colorHex = "#2563EB")
        val w3 = WalletEntity(id = 3L, name = "Ví MoMo", type = "VI_DIEN_TU", balance = 1200000.0, iconName = "Smartphone", colorHex = "#EC4899")
        val w4 = WalletEntity(id = 4L, name = "Thẻ Tín Dụng VPBank", type = "THE_TIN_DUNG", balance = -2500000.0, iconName = "CreditCard", colorHex = "#F59E0B")

        walletDao.insertWallets(listOf(w1, w2, w3, w4))

        // 2. Transactions
        val samples = listOf(
            TransactionEntity(
                title = "Lương tháng này",
                amount = 22000000.0,
                type = TransactionType.INCOME.name,
                category = "Lương",
                date = now - 2 * 24 * 3600 * 1000L,
                note = "Chuyển khoản công ty",
                paymentMethod = "Vietcombank",
                walletId = 2L
            ),
            TransactionEntity(
                title = "Đi siêu thị WinMart",
                amount = 850000.0,
                type = TransactionType.EXPENSE.name,
                category = "Ăn uống",
                date = now - 1 * 24 * 3600 * 1000L,
                note = "Thức ăn gia đình 1 tuần",
                paymentMethod = "Ví MoMo",
                walletId = 3L
            ),
            TransactionEntity(
                title = "Cà phê gặp đối tác",
                amount = 120000.0,
                type = TransactionType.EXPENSE.name,
                category = "Ăn uống",
                date = now - 12 * 3600 * 1000L,
                note = "Highlands Coffee",
                paymentMethod = "Ví Tiền mặt",
                walletId = 1L
            ),
            TransactionEntity(
                title = "Đổ xăng xe máy",
                amount = 100000.0,
                type = TransactionType.EXPENSE.name,
                category = "Di chuyển",
                date = now - 3 * 24 * 3600 * 1000L,
                note = "Cây xăng Petrolimex",
                paymentMethod = "Ví Tiền mặt",
                walletId = 1L
            ),
            TransactionEntity(
                title = "Tiền điện nước internet",
                amount = 1450000.0,
                type = TransactionType.EXPENSE.name,
                category = "Hóa đơn",
                date = now - 5 * 24 * 3600 * 1000L,
                note = "Hóa đơn tháng",
                paymentMethod = "Vietcombank",
                walletId = 2L
            ),
            TransactionEntity(
                title = "Mua áo sơ mi công sở",
                amount = 650000.0,
                type = TransactionType.EXPENSE.name,
                category = "Mua sắm",
                date = now - 4 * 24 * 3600 * 1000L,
                note = "Uniqlo Mall",
                paymentMethod = "Thẻ Tín Dụng VPBank",
                walletId = 4L
            ),
            TransactionEntity(
                title = "Thưởng dự án Q2",
                amount = 3500000.0,
                type = TransactionType.INCOME.name,
                category = "Thưởng",
                date = now - 8 * 24 * 3600 * 1000L,
                note = "Thưởng KPI quý",
                paymentMethod = "Vietcombank",
                walletId = 2L
            ),
            TransactionEntity(
                title = "Xem phim CGV Cinema",
                amount = 320000.0,
                type = TransactionType.EXPENSE.name,
                category = "Giải trí",
                date = now - 6 * 24 * 3600 * 1000L,
                note = "Vé xem phim cuối tuần",
                paymentMethod = "Ví MoMo",
                walletId = 3L
            )
        )

        for (tx in samples) {
            transactionDao.insertTransaction(tx)
        }

        // 3. Category Budgets
        categoryBudgetDao.insertOrUpdateBudget(CategoryBudgetEntity("Ăn uống", 6000000.0))
        categoryBudgetDao.insertOrUpdateBudget(CategoryBudgetEntity("Mua sắm", 3000000.0))
        categoryBudgetDao.insertOrUpdateBudget(CategoryBudgetEntity("Di chuyển", 1500000.0))
        categoryBudgetDao.insertOrUpdateBudget(CategoryBudgetEntity("Giải trí", 2000000.0))
        categoryBudgetDao.insertOrUpdateBudget(CategoryBudgetEntity("Hóa đơn", 2500000.0))

        // 4. Sample Debts & Loans
        debtDao.insertDebts(
            listOf(
                DebtEntity(personName = "Anh Tuấn (Cho vay)", amount = 2000000.0, type = "CHO_VAY", note = "Mượn tiền làm dự án", walletId = 1L),
                DebtEntity(personName = "Bạn Minh (Đi vay)", amount = 500000.0, type = "DI_VAY", note = "Vay mua đồ gia dụng", walletId = 1L)
            )
        )

        // 5. Savings Goals
        savingsGoalDao.insertGoals(
            listOf(
                SavingsGoalEntity(title = "Mua iPhone 16 Pro", targetAmount = 30000000.0, currentAmount = 18500000.0, colorHex = "#10B981"),
                SavingsGoalEntity(title = "Du lịch Nhật Bản cuối năm", targetAmount = 45000000.0, currentAmount = 12000000.0, colorHex = "#3B82F6")
            )
        )

        // 6. Recurring Bills
        recurringBillDao.insertBills(
            listOf(
                RecurringBillEntity(title = "Hóa đơn Tiền Điện EVN", amount = 850000.0, category = "Hóa đơn", walletId = 2L),
                RecurringBillEntity(title = "Cước Internet FPT", amount = 285000.0, category = "Hóa đơn", walletId = 2L),
                RecurringBillEntity(title = "Gói Netflix Ultra HD", amount = 260000.0, category = "Giải trí", walletId = 3L)
            )
        )
    }

    fun exportTransactionsToCsv(transactions: List<TransactionEntity>): String {
        val sb = StringBuilder()
        sb.append("ID,Tiêu đề,Số tiền (VND),Loại,Danh mục,Ngày,Ghi chú,Phương thức\n")
        for (tx in transactions) {
            val dateStr = FormatUtils.formatDate(tx.date)
            val typeStr = when (tx.type) {
                TransactionType.INCOME.name -> "Thu nhập"
                TransactionType.EXPENSE.name -> "Chi tiêu"
                TransactionType.TRANSFER.name -> "Chuyển tiền"
                else -> tx.type
            }
            val cleanTitle = tx.title.replace(",", " ")
            val cleanNote = tx.note.replace(",", " ")
            sb.append("${tx.id},\"$cleanTitle\",${tx.amount},\"$typeStr\",\"${tx.category}\",\"$dateStr\",\"$cleanNote\",\"${tx.paymentMethod}\"\n")
        }
        return sb.toString()
    }
}
