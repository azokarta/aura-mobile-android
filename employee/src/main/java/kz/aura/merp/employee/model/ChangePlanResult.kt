package kz.aura.merp.employee.model

data class ChangePlanResult(
    val phoneNumber: String?,
    var resultId: Long?,
    var reasonDescription: String? = null,
    var bankId: Long?,
    var paymentMethodId: Long?,
    var longitude: Double?,
    var latitude: Double?,
    var collectMoneyAmount: Int?
)
