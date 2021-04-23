package kz.aura.merp.employee.model

data class TrackEmpProcessServiceApplication (
    val id: Long?,
    val longitude: String,
    val latitude: String,
    val maTrackStepId: Int,
    val context: String?,
    val contextId: Long
)