package kz.aura.merp.employee.model

import org.joda.time.Duration

data class AssignCall(
    val phoneNumber: String,
    val callStatusId: Long?,
    val callDirectionId: Long,
    val callDateTime: String,
    val duration: String,
    val description: String,
    val longitude: Double,
    val latitude: Double
)
