package kz.aura.merp.employee.data.auth

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ViewModelScoped
import kz.aura.merp.employee.base.NetworkResult
import kz.aura.merp.employee.base.executeWithResponse
import kz.aura.merp.employee.model.AuthResponse
import javax.inject.Inject

@ViewModelScoped
class AuthRepository @Inject constructor(
    private val authService: AuthService,
    @ApplicationContext private val context: Context
) {
    suspend fun signin(username: String, password: String): NetworkResult<AuthResponse> {
        return executeWithResponse(context) {
            authService.signin("password", username, password)
        }
    }
}