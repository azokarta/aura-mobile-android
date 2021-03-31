package kz.aura.merp.employee.data.repository.authRepository

import dagger.Provides
import kz.aura.merp.employee.data.model.AuthResponse
import kz.aura.merp.employee.data.model.ResponseHelper
import kz.aura.merp.employee.data.model.Staff
import kz.aura.merp.employee.data.network.AuthApi
import kz.aura.merp.employee.util.Link
import retrofit2.Response
import javax.inject.Inject

class AuthRemoteDataSource @Inject constructor(
    private val authApi: AuthApi
) {

    suspend fun signin(username: String, password: String): Response<AuthResponse> {
        return authApi.signin(username = username, password = password)
    }

    suspend fun getUserInfo(phoneNumber: String): Response<ResponseHelper<Staff>> {
        return authApi.getUserInfo(phoneNumber)
    }
}