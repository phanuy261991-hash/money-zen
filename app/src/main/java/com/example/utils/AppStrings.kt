package com.example.utils

object AppStrings {
    private val isEn: Boolean
        get() = FormatUtils.currentLanguage == "EN"

    val currentCurrency: String
        get() = FormatUtils.currentCurrency

    // Navigation Tabs
    val tabHome get() = if (isEn) "Home" else "Trang chủ"
    val tabTransactions get() = if (isEn) "Transactions" else "Giao dịch"
    val tabAnalytics get() = if (isEn) "Analytics & Budget" else "Báo cáo & Ngân sách"
    val tabWallets get() = if (isEn) "Wallets & Tools" else "Ví & Tiện ích"
    val tabSettings get() = if (isEn) "Settings" else "Cài đặt"

    // Home Screen
    val selectWallet get() = if (isEn) "Select Wallet" else "Chọn Ví Tiền"
    val totalWalletBalance get() = if (isEn) "Total Wallet Balance" else "Tổng Số Dư Các Ví"
    val allWallets get() = if (isEn) "All Wallets" else "Tất cả ví"
    val income get() = if (isEn) "Income" else "Thu nhập"
    val expense get() = if (isEn) "Expense" else "Chi tiêu"
    val netBalance get() = if (isEn) "Net Surplus" else "Thặng dư net"
    val recentTransactions get() = if (isEn) "Recent Transactions" else "Giao dịch gần đây"
    val viewAll get() = if (isEn) "View All" else "Xem tất cả"
    val noTransactions get() = if (isEn) "No transactions found" else "Chưa có giao dịch nào"
    val categoryBreakdown get() = if (isEn) "Category Breakdown" else "Chi tiêu theo danh mục"
    val budgetStatus get() = if (isEn) "Budget Status" else "Trạng thái ngân sách"
    val savingsGoals get() = if (isEn) "Savings Goals" else "Mục tiêu tiết kiệm"
    val upcomingBills get() = if (isEn) "Upcoming Bills" else "Hóa đơn sắp tới"
    val overBudget get() = if (isEn) "Over budget" else "Vượt ngân sách"
    val safeBudget get() = if (isEn) "Within budget" else "Trong mức an toàn"

    // Time Periods
    val periodToday get() = if (isEn) "Today" else "Hôm nay"
    val periodWeek get() = if (isEn) "This Week" else "Tuần này"
    val periodMonth get() = if (isEn) "This Month" else "Tháng này"
    val periodYear get() = if (isEn) "This Year" else "Năm nay"
    val periodAll get() = if (isEn) "All Time" else "Tất cả"

    // Transactions Screen
    val searchPlaceholder get() = if (isEn) "Search transactions..." else "Tìm kiếm giao dịch..."
    val filterAll get() = if (isEn) "All" else "Tất cả"
    val filterExpense get() = if (isEn) "Expense" else "Chi tiêu"
    val filterIncome get() = if (isEn) "Income" else "Thu nhập"
    val totalCount get() = if (isEn) "Total Transactions" else "Tổng số giao dịch"

    // Add / Edit Transaction
    val newTransactionTitle get() = if (isEn) "Add New Transaction" else "Thêm Giao Dịch Mới"
    val editTransactionTitle get() = if (isEn) "Edit Transaction" else "Sửa Giao Dịch"
    val expenseToggle get() = if (isEn) "Expense (-)" else "Chi tiêu (-)"
    val incomeToggle get() = if (isEn) "Income (+)" else "Thu nhập (+)"
    val amountLabel get() = if (isEn) "Amount ($currentCurrency) *" else "Số tiền ($currentCurrency) *"
    val selectWalletLabel get() = if (isEn) "Select Wallet" else "Chọn Ví Tiền Thực Hiện"
    val walletLabel get() = if (isEn) "Wallet" else "Ví Tiền"
    val categoryLabel get() = if (isEn) "Category" else "Danh mục"
    val titleLabel get() = if (isEn) "Title / Detail Name" else "Tiêu đề / Tên chi tiết"
    val titlePlaceholder get() = if (isEn) "e.g. Lunch, Monthly Salary..." else "Ví dụ: Ăn trưa bún chả, Tiền lương..."
    val transactionDate get() = if (isEn) "Transaction Date" else "Ngày giao dịch"
    val noteLabel get() = if (isEn) "Note (Optional)" else "Ghi chú (Không bắt buộc)"
    val saveTransaction get() = if (isEn) "Save Transaction" else "Lưu Giao Dịch"
    val updateTransaction get() = if (isEn) "Update Transaction" else "Cập Nhật Giao Dịch"
    val invalidAmount get() = if (isEn) "Please enter a valid amount" else "Vui lòng nhập số tiền hợp lệ"
    val close get() = if (isEn) "Close" else "Đóng"
    val select get() = if (isEn) "Select" else "Chọn"
    val cancel get() = if (isEn) "Cancel" else "Hủy"

    // Budget Dialog
    val budgetTitle get() = if (isEn) "Set Budget Limit" else "Thiết lập ngân sách"
    val budgetLimitLabel get() = if (isEn) "Monthly Spending Limit ($currentCurrency)" else "Hạn mức chi tiêu tháng ($currentCurrency)"
    val budgetSave get() = if (isEn) "Save Limit" else "Lưu Hạn Mức"

    // Wallets & Tools Tabs
    val tabWalletsList get() = if (isEn) "My Wallets" else "Ví Của Tôi"
    val tabDebts get() = if (isEn) "Debts & Loans" else "Sổ Nợ & Vay"
    val tabSavings get() = if (isEn) "Savings Goals" else "Hũ Tiết Kiệm"
    val tabBills get() = if (isEn) "Recurring Bills" else "Hóa Đơn Định Kỳ"

    // Wallets & Tools Details
    val walletsManagement get() = if (isEn) "Wallets Management" else "Quản lý ví tiền"
    val addWallet get() = if (isEn) "Add New Wallet" else "Thêm ví mới"
    val walletName get() = if (isEn) "Wallet Name" else "Tên ví"
    val initialBalance get() = if (isEn) "Initial Balance" else "Số dư ban đầu"
    val walletType get() = if (isEn) "Wallet Category" else "Loại ví tiền"
    val typeCash get() = if (isEn) "Cash" else "Tiền mặt"
    val typeBank get() = if (isEn) "Bank Account" else "Ngân hàng"
    val typeEWallet get() = if (isEn) "E-Wallet" else "Ví điện tử"
    val typeCredit get() = if (isEn) "Credit Card" else "Thẻ tín dụng"
    val transferMoney get() = if (isEn) "Transfer Money" else "Chuyển Tiền"
    val debtsManagement get() = if (isEn) "Debts & Loans" else "Sổ nợ & Cho vay"
    val recurringBillsManagement get() = if (isEn) "Recurring Bills" else "Hóa đơn định kỳ"
    val paid get() = if (isEn) "Paid" else "Đã thanh toán"
    val unpaid get() = if (isEn) "Unpaid" else "Chưa thanh toán"

    // Deletion Confirmations
    val deleteConfirmTitle get() = if (isEn) "Confirm Deletion" else "Xác Nhận Xóa"
    val deleteWalletMsg get() = if (isEn) "Are you sure you want to delete this wallet?" else "Bạn có chắc chắn muốn xóa ví tiền này không?"
    val deleteDebtMsg get() = if (isEn) "Are you sure you want to delete this debt entry?" else "Bạn có chắc chắn muốn xóa khoản nợ này không?"
    val deleteGoalMsg get() = if (isEn) "Are you sure you want to delete this savings goal?" else "Bạn có chắc chắn muốn xóa mục tiêu tiết kiệm này không?"
    val deleteBillMsg get() = if (isEn) "Are you sure you want to delete this recurring bill?" else "Bạn có chắc chắn muốn xóa hóa đơn định kỳ này không?"
    val deleteBtn get() = if (isEn) "Delete" else "Xóa"

    // Settings Screen
    val securityTitle get() = if (isEn) "Fingerprint & Device Lock" else "Xác Thực Vân Tay & Khóa Thiết Bị"
    val securityEnabled get() = if (isEn) "Biometric login protection ON" else "Đang bật bảo mật đăng nhập"
    val securityDisabled get() = if (isEn) "Protection disabled" else "Chưa bật bảo mật"
    val lockNow get() = if (isEn) "Lock Screen Now" else "Khóa Màn Hình Ngay"
    val changePin get() = if (isEn) "Change Backup PIN" else "Đổi Mã PIN Backup"
    val themeTitle get() = if (isEn) "Appearance Theme" else "Chế Độ Giao Diện"
    val themeSystem get() = if (isEn) "System Default" else "Theo Hệ Thống"
    val themeLight get() = if (isEn) "Light Mode" else "Giao Diện Sáng"
    val themeDark get() = if (isEn) "Dark Mode" else "Giao Diện Tối"
    val languageTitle get() = if (isEn) "App Language" else "Ngôn Ngữ Ứng Dụng"
    val currencyTitle get() = if (isEn) "Currency Unit" else "Đơn Vị Tiền Tệ"
    val dataTitle get() = if (isEn) "Data Management" else "Quản Lý Dữ Liệu"
    val insertSample get() = if (isEn) "Insert Sample Data" else "Thêm Dữ Liệu Mẫu"
    val clearAll get() = if (isEn) "Clear All Data" else "Xóa Toàn Bộ Dữ Liệu"
    val exportCsv get() = if (isEn) "Export CSV Data" else "Xuất Dữ Liệu CSV"

    // Biometric Lock Screen
    val appProtectionTitle get() = if (isEn) "FINANCIAL SECURITY" else "BẢO MẬT TÀI CHÍNH"
    val appProtectionMsg get() = if (isEn) "Application is protected. Please authenticate via fingerprint, face, or device lock to proceed." else "Ứng dụng được bảo vệ. Vui lòng xác thực vân tay, khuôn mặt hoặc khóa thiết bị để tiếp tục."
    val triggerBiometricBtn get() = if (isEn) "Fingerprint / Unlock Device" else "Xác Thực Vân Tay / Mở Khóa"
    val enterBackupPinBtn get() = if (isEn) "Enter Backup PIN" else "Nhập Mã PIN Backup"
    val pinDialogTitle get() = if (isEn) "Enter Security PIN" else "Nhập Mã PIN Bảo Mật"
    val defaultPinNote get() = if (isEn) "Default: 1234 (Can be changed in Settings)" else "Mặc định: 1234 (Có thể thay đổi trong Cài Đặt)"
    val incorrectPin get() = if (isEn) "Incorrect PIN, try again!" else "Mã PIN không đúng, thử lại!"
}
