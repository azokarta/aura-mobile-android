package kz.aura.merp.employee.model

data class AssignScheduledCallCommand (
    val phoneNumber: String?,
    val countryCode: String,
    val longitude: Double,
    val latitude: Double,
    val scheduledDateTime: String,
    val description: String
)