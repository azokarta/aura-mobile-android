package kz.aura.merp.customer.models

data class PushNotification (
    val data: Notification,
    val to: String
)