package kz.aura.merp.employee.data.model

data class StaffLocation (
    val staffId: Long,
    val attendanceTypeId: Long? = null,
    val maTrackStepId: Int? = null,
    val maTbpId: Int? = null,
    val longitude: String,
    val latitude: String,
    val lastTime: String? = null,
    val lastTimeFormatted: String? = null
)