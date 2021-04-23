package kz.aura.merp.employee.model

data class ResponseHelper <T> (
    val success: Boolean,
    val data: T
)