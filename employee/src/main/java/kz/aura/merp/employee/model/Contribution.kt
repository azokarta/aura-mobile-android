package kz.aura.merp.employee.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Contribution(
    val id: Long?,
    val contractId: Long,
    val contractNumber: Long,
    val bukrs: String,
    val companyName: String?,
    val branchId: Long,
    val branchName: String?,
    val statusId: Long,
    val statusName: String,
    val phoneNumber: String,
    val smsSent: Boolean,
    val resultId: Long,
    val resultName: String?,
    val collectMoneyAmount: Double?,
    val collectMoneyCurrency: String?,
    val collectDate: String?,
    val paymentMethodId: Long?,
    val paymentMethodName: String?,
    val paymentBankId: Long?,
    val paymentBankName: String?,
    val reasonDescription: String?,
    val collectorId: Long,
    val collectorFirstname: String?,
    val collectorMiddlename: String?,
    val collectorLastname: String?
): Parcelable