package kz.aura.merp.employee.model

data class AssignCall(
    val countryCode: String,
    val phoneNumber: String?,
    val callStatusId: Long? = null,
    val callTime: String,
    val duration: String? = null,
    val description: String,
    val longitude: Double,
    val latitude: Double
)
