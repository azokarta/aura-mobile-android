package kz.aura.merp.employee.model

data class PlanHistoryItem(
    val id: Long? = null,
    val contractId: Long? = null,
    val planReasonDescription: String? = null,
    val planCollectMoneyAmount: Double? = null,
    val createOn: String? = null,
    val planResultId: Long? = null,
    val planResultName: String? = null,
    val planBusinessProcessId: Long? = null,
    val planBusinessProcessName: String? = null,
    val planPaymentMethodId: Long? = null,
    val planPaymentMethodName: String? = null,
    val planPaymentBankId: Long? = null,
    val planPaymentBankName: String? = null,
    val collectorId: Long? = null,
    val collectorFirstname: String? = null,
    val collectorMiddlename: String? = null,
    val collectorLastname: String? = null,
    val planCollectMoneyCurrency: String? = null
) {
    fun collectorFullName() = "${collectorLastname ?: ""} ${collectorFirstname ?: ""} ${collectorMiddlename ?: ""}"
}