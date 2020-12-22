package kz.aura.merp.employee.data.model

data class TrackStepOrdersBusinessProcess (
    val id: Int,
    val maTrackStepId: Int,
    val maTbpId: Int,
    val maTrackOrder: Int,
    val trackStepNameEn: String,
    val trackStepNameKk: String,
    val trackStepNameRu: String
)