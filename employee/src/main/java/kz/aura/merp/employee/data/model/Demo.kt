package kz.aura.merp.employee.data.model

import java.io.Serializable

data class Demo(
    val demoId: Long,
    val clientName: String,
    val address: String,
    var price: Double,
    var note: String,
    val crmPhoneDtoList: ArrayList<PhoneNumber>,
    var resultId: Int?,
    val dateTime: String,
    val customerId: Long,
    val contractNumber: Long,
    val dealerId: Long,
    val recoId: Long,
    val dateTimeFormatted: String
): Serializable {

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

    override fun hashCode(): Int {
        var result = demoId.hashCode()
        result = 31 * result + clientName.hashCode()
        result = 31 * result + address.hashCode()
        result = 31 * result + price.hashCode()
        result = 31 * result + note.hashCode()
        result = 31 * result + crmPhoneDtoList.hashCode()
        result = 31 * result + (resultId ?: 0)
        result = 31 * result + dateTime.hashCode()
        result = 31 * result + customerId.hashCode()
        result = 31 * result + contractNumber.hashCode()
        result = 31 * result + dealerId.hashCode()
        result = 31 * result + recoId.hashCode()
        result = 31 * result + dateTimeFormatted.hashCode()
        return result
    }

}