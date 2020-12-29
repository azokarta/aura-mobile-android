package kz.aura.merp.customer.data.model

data class Bonus (
    val id: Long,
    val amount: Double,
    val operationDate: String,
    val drcrk: String,
    val description: String,
    val maCustomerBonusTypeId: Int,
    val confirmedByCustomer: Int,
    val confirmedByCustomerDate: String,
    val waers: String,
    val maBonusTypeName: String
)