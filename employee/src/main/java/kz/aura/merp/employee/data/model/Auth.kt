package kz.aura.merp.employee.data.model

data class Auth (
    val accessToken: String,
    var userInfo: Staff
)