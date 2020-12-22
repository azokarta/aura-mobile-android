package kz.aura.merp.employee.data.model

import java.io.Serializable

data class PhoneNumber (
    var phoneNumber: String
): Serializable {
    override fun equals(other: Any?): Boolean {
        if (other?.javaClass != javaClass) {
            return false
        }
        other as PhoneNumber
        if (phoneNumber != other.phoneNumber) {
            return false
        }
        return true
    }

    override fun hashCode(): Int {
        return phoneNumber.hashCode()
    }
}