package kz.aura.merp.customer.data.model

import java.io.Serializable

data class PaymentCalendar (
    val productImage: String,
    val nextPayment: Int,
    val dateTo: String,
    val date: String
): Serializable