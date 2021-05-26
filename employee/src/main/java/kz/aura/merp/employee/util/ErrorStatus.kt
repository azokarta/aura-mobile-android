package kz.aura.merp.employee.util

enum class ErrorStatus(val status: Int? = null) {
    INTERNAL_SERVER_ERROR(500),
    NOT_FOUND(404),
    INTERNET_IS_NOT_AVAILABLE,
    FORBIDDEN(401),
    BAD_REQUEST(400)
}