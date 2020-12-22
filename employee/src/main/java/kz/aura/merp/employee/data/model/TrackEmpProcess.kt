package kz.aura.merp.employee.data.model

data class TrackEmpProcess (
    val id: Long?,
    val longitude: String,
    val latitude: String,
    val maTrackStepId: Int,
    val context: String?,
    val contextId: Long
)