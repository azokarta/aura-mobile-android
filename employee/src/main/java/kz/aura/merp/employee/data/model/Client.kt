package kz.aura.merp.employee.data.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.io.Serializable

@Parcelize
data class Client (
    val contractId: Long,
    val contractDate: String?,
    val paymentSchedule: Int?,
    val addrHomeId: Long?,
    val address: String?,
    val customerId: Long?,
    var price: Double?,
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
): Parcelable {

    override fun equals(other: Any?): Boolean {

        if (javaClass != other?.javaClass) {
            return false
        }

        other as Client

        if (contractId != other.contractId) return false
        if (contractDate != other.contractDate) return false
        if (paymentSchedule != other.paymentSchedule) return false
        if (addrHomeId != other.addrHomeId) return false
        if (address != other.address) return false
        if (customerId != other.customerId) return false
        if (price != other.price) return false
        if (totalPaid != other.totalPaid) return false
        if (nextPaymentDate != other.nextPaymentDate) return false
        if (nextPaymentAmount != other.nextPaymentAmount) return false
        if (longitude != other.longitude) return false
        if (latitude != other.latitude) return false
        if (firstname != other.firstname) return false
        if (middlename != other.middlename) return false
        if (lastname != other.lastname) return false
        if (maCollectMoneyId != other.maCollectMoneyId) return false
        if (taxiExpenseAmount != other.taxiExpenseAmount) return false
        if (phone != other.phone) return false
        if (description != other.description) return false
        if (maCollectResultId != other.maCollectResultId) return false
        if (contractDateFormatted != other.contractDateFormatted) return false
        if (nextPaymentDateFormatted != other.nextPaymentDateFormatted) return false

        return true
    }

}