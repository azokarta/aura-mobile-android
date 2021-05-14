package kz.aura.merp.employee.model

data class ResponseHelper <T> (
    val data: T,
    val status: String,
    val message: String
)