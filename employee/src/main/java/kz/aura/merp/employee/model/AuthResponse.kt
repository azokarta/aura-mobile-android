package kz.aura.merp.employee.model

import com.google.gson.annotations.SerializedName

data class AuthResponse (
    @SerializedName("access_token")
    val accessToken: String,
    @SerializedName("token_type")
    val tokenType: String,
    @SerializedName("refresh_token")
    val refreshToken: String,
    @SerializedName("expires_in")
    val expiresIn: Long,
    val scope: String,
    val email: String,
    val userId: Long,
    val jti: String
)