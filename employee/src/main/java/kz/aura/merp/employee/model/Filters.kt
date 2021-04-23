package kz.aura.merp.employee.model

data class Filters(
    val filterId: Long?,
    val contractId: Long?,
    val tovarSn: String?,
    val f1MtLeft: Int?,
    val f2MtLeft: Int?,
    val f3MtLeft: Int?,
    val f4MtLeft: Int?,
    val f5MtLeft: Int?,
    val crmCategoryId: Long?,
    val crmCategoryName: String?,
    val operatorFIO: String?
)
