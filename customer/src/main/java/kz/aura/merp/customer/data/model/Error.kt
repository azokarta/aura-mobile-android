package kz.aura.merp.customer.data.model

import java.util.*

data class Error (
    val status: Long,
    val message: String,
    val error: String,
    val success: Boolean,
    val timestamp: Date
)