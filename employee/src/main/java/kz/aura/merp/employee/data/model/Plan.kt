package kz.aura.merp.employee.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Plan (
    val contractId: Long,
    val contractDate: String?,
    val contractNumber: Long,
    val paymentSchedule: Int?,
    val addrHomeId: Long?,
    val address: String?,
    val customerId: Long?,
    val paymentOverdueDays: Int,
    var toPay: Double?,
    val totalPaid: Double?,
    val nextPaymentDate: String?,
    val nextPaymentAmount: Double?,
    val longitude: String?,
    val latitude: String?,
    val firstname: String?,
    val middlename: String?,
    val lastname: String?,
    val maCollectMoneyId: Long,
    var taxiExpenseAmount: Double?,
    val phone: String?,
    var description: String?,
    var maCollectResultId: Int,
    val contractDateFormatted: String?,
    val nextPaymentDateFormatted: String?
): Parcelable