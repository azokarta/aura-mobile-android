package kz.aura.merp.employee.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class DailyPlan(
    val dailyPlanId: Long?,
    val contractId: Long,
    val contractNumber: Long?,
    val bukrs: String?,
    val companyName: String?,
    val branchId: Long?,
    val branchName: String?,
    val contractDate: String?,
    val contractStatusId: Long?,
    val contractStatusName: String?,
    val problem: Boolean?,
    val contractCurrencyName: String?,
    val price: Double?,
    val totalPaid: Double?,
    val nextPaymentDate: String?,
    val paymentOverDueDays: Int?,
    val toPay: Double?,
    val customerId: Long?,
    val customerFirstname: String?,
    val customerMiddlename: String?,
    val customerLastname: String?,
    val customerAddressId: Long?,
    val customerAddress: String?,
    val customerPhoneNumbers: List<String?>?,
    val planBusinessProcessId: Long?,
    val planBusinessProcessName: String?,
    var planResultId: Long?,
    var planResultName: String?,
    var planDateTime: String?
): Parcelable {
    fun getFullName() = "${customerLastname ?: ""} ${customerFirstname ?: ""} ${customerMiddlename ?: ""}"
}
