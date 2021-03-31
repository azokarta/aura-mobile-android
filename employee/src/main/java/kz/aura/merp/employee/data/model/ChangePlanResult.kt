package kz.aura.merp.employee.data.model

data class ChangePlanResult(
    var resultId: Long,
    var reasonDescription: String?,
    var bankId: Long?,
    var paymentMethodId: Long?,
    var longitude: Double?,
    var latitude: Double?,
    var collectMoneyAmount: Int?
)
