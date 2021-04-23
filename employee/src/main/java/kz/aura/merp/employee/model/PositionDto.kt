package kz.aura.merp.employee.model

data class PositionDto (
    val positionId: Long,
    val spras: String,
    val nameRu: String,
    val nameEn: String,
    val nameTr: String
)