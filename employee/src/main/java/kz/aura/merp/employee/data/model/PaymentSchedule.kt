package kz.aura.merp.employee.data.model

data class PaymentSchedule (
        val bukrs: String?,
        val forPay: Double?,
        val paymentDate: String?,
        val paid: Double?,
        val firstPayment: Boolean?
)