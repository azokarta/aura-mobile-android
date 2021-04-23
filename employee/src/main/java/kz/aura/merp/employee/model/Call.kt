package kz.aura.merp.employee.model

import java.time.Duration

data class Call(
    val phoneNumber: String,
    val callStatusId: Long,
    val callStatusName: String,
    val callDirectionId: Long,
    val callDirectionName: String,
    val callDateTime: String,
    val duration: Duration,
    val description: String?,
    val contractId: Long,
    val contractNumber: Long,
    val callerId: Long,
    val callerFirstName: String?,
    val callerMiddleName: String?,
    val callerLastName: String?
)
