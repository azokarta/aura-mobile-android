package kz.aura.merp.employee.model

data class ChangePlanResult(
    var resultId: Long?,
    var reasonDescription: String? = null,
    var longitude: Double?,
    var latitude: Double?,
    val assignCollectMoneyCommand: AssignCollectMoneyCommand? = null,
    val assignScheduledCallCommand: AssignScheduledCallCommand? = null
)
