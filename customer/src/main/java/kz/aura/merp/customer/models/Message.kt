package kz.aura.merp.customer.models

data class Message (
    val customerId: Long,
    val date: String,
    val done: Int,
    val message: String,
    val messageType: Int,
    val read: Int
)