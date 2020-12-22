package kz.aura.merp.customer.models

data class ResponseHelper <T> (
    val success: Boolean,
    val data: T
)