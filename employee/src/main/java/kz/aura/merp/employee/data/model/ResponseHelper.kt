package kz.aura.merp.employee.data.model

data class ResponseHelper <T> (
    val success: Boolean,
    val data: T
)