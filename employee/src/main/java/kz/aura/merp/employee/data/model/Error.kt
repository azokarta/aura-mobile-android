package kz.aura.merp.employee.data.model

import java.util.*

data class Error (
    val status: Long,
    val message: String,
    val error: String,
    val success: Boolean,
    val timestamp: Date
)