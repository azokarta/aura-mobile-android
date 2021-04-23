package kz.aura.merp.employee.model

data class ScheduledCall(
    val id: Long,
    val contractId: Long,
    val contractNumber: Long,
    val phoneNumber: String?,
    val scheduledDateTime: String,
    val description: String?,
    val authorId: Long,
    val authorFirstname: String?,
    val authorMiddlename: String?,
    val authorLastname: String?,
)
