package kz.aura.merp.employee.data.model

import java.io.Serializable

data class ServiceApplication (
    var id: Long,
    var customerId : Long,
    var address: String = "",
    var contractId: Long,
    var taxiExpenseAmount : Double,
    val applicantName: String,
    var appStatus: Int,
    var description: String,
    val adate: String,
    val adateFromatted: String
) : Serializable {

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

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + customerId.hashCode()
        result = 31 * result + address.hashCode()
        result = 31 * result + contractId.hashCode()
        result = 31 * result + taxiExpenseAmount.hashCode()
        result = 31 * result + applicantName.hashCode()
        result = 31 * result + appStatus
        result = 31 * result + description.hashCode()
        result = 31 * result + adate.hashCode()
        result = 31 * result + adateFromatted.hashCode()
        return result
    }

}

