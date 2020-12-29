package kz.aura.merp.customer.data.model

data class Auth (
        val token: String,
        var customerId: Long
)