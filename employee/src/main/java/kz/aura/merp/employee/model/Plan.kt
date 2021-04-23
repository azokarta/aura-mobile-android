package kz.aura.merp.employee.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Plan (
    val contractId: Long? = null,
    val contractNumber: Long? = null,
    val contractDate: String? = null,
    val contractCurrencyName: String? = null,
    val price: Double? = null,
    val totalPaid: Double? = null,
    val contractStatusName: String? = null,
    val problem: Boolean,
    val nextPaymentDate: String? = null,
    val paymentOverDueDays: Int? = null,
    val toPay: Double? = null,
    val customerId: Long? = null,
    val customerFirstname: String? = null,
    val customerMiddlename: String? = null,
    val customerLastname: String? = null,
    val customerPhoneNumbers: List<String?>? = null,
    val customerAddressId: Long? = null,
    val customerAddress: String? = null,
    val planLongitude: Double? = null,
    val planLatitude: Double? = null,
    var planReasonDescription: String? = null,
    var planResultId: Long? = null,
    val planResultName: String? = null,
    val planBusinessProcessId: Long? = null,
    val planBusinessProcessName: String? = null,
    val planPaymentMethodId: Long? = null,
    val planPaymentMethodName: String? = null,
    val planPaymentBankId: Long? = null,
    val planPaymentBankName: String? = null,
    val planCollectMoneyAmount: Int? = null
): Parcelable {
    fun getFullName() = "${customerLastname ?: ""} ${customerFirstname ?: ""} ${customerMiddlename ?: ""}"
}