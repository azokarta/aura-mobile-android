package kz.aura.merp.employee.data.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.io.Serializable

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
) : Parcelable {

    override fun equals(other: Any?): Boolean {

        if (javaClass != other?.javaClass) {
            return false
        }

        other as ServiceApplication

        if (id != other.id) return false
        if (customerId != other.customerId) return false
        if (address != other.address) return false
        if (contractId != other.contractId) return false
        if (taxiExpenseAmount != other.taxiExpenseAmount) return false
        if (applicantName != other.applicantName) return false
        if (appStatus != other.appStatus) return false
        if (description != other.description) return false
        if (adate != other.adate) return false
        if (adateFromatted != other.adateFromatted) return false

        return true
    }

}

