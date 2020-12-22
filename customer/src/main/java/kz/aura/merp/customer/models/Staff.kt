package kz.aura.merp.customer.models

data class Staff (
    val staffId: Long,
    val mobile: String,
    val lastName: String,
    val firstName: String,
    val middleName: String,
    val sacked: Int,
    val sackedDate: String
)