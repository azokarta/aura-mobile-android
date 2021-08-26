package kz.aura.merp.employee.data.repository.authRepository

import kz.aura.merp.employee.model.AuthResponse
import kz.aura.merp.employee.data.network.AuthService
import retrofit2.Response
import javax.inject.Inject

class AuthRemoteDataSource @Inject constructor(
    private val authService: AuthService
) {
    suspend fun signin(username: String, password: String): Response<AuthResponse> {
        return authService.signin("password", username, password)
    }
}