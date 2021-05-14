package kz.aura.merp.employee.model

data class AssignCollectMoneyCommand(
    val phoneNumber: String?,
    var bankId: Long?,
    var paymentMethodId: Long?,
    var collectMoneyAmount: Int?,
    val countryCode: String,
    val longitude: Double,
    val latitude: Double
)
