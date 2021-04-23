package kz.aura.merp.employee.model

data class Error (
    val status: String?,
    val message: String?,
    val error: String?,
    val trace: String?,
    val code: String?
)