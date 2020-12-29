package kz.aura.merp.customer.data.model

data class ResponseHelper <T> (
    val success: Boolean,
    val data: T
)