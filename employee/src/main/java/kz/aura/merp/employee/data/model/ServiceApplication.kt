package kz.aura.merp.employee.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ServiceApplication (
    var id: Long,
    var customerId : Long?,
    var address: String? = "",
    var contractId: Long?,
    var taxiExpenseAmount : Double?,
    val applicantName: String?,
    var appStatus: Int,
    var description: String?,
    val adate: String?,
    val adateFromatted: String?
) : Parcelable