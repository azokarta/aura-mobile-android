package kz.aura.merp.employee.model

data class Message (
    val fromId: Long,
    val createdAt: String,
    val from: String,
    val message: String
)