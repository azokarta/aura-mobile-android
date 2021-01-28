package kz.aura.merp.employee.data.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.io.Serializable

@Parcelize
data class Demo(
    val demoId: Long,
    val clientName: String?,
    val address: String?,
    var price: Double?,
    var note: String?,
    val crmPhoneDtoList: ArrayList<PhoneNumber>?,
    var resultId: Int?,
    val dateTime: String?,
    val customerId: Long?,
    val contractNumber: Long?,
    val dealerId: Long?,
    val recoId: Long?,
    val dateTimeFormatted: String?,
    var ocrDemoStatus: String?
): Parcelable {

    override fun equals(other: Any?): Boolean {

        if (javaClass != other?.javaClass) {
            return false
        }

        other as Demo

        if (demoId != other.demoId) return false
        if (clientName != other.clientName) return false
        if (address != other.address) return false
        if (price != other.price) return false
        if (note != other.note) return false
        if (crmPhoneDtoList != other.crmPhoneDtoList) return false
        if (resultId != other.resultId) return false
        if (dateTime != other.dateTime) return false
        if (customerId != other.customerId) return false
        if (contractNumber != other.contractNumber) return false
        if (dealerId != other.dealerId) return false
        if (recoId != other.recoId) return false
        if (dateTimeFormatted != other.dateTimeFormatted) return false

        return true
    }

}