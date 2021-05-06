package kz.aura.merp.employee.data.repository.authRepository

import kz.aura.merp.employee.data.DataStoreRepository
import kz.aura.merp.employee.model.AuthResponse
import kz.aura.merp.employee.model.ResponseHelper
import kz.aura.merp.employee.model.Salary
import kz.aura.merp.employee.data.network.AuthApi
import kz.aura.merp.employee.util.Interceptor
import kz.aura.merp.employee.util.Link
import retrofit2.Response
import javax.inject.Inject

class AuthRemoteDataSource @Inject constructor(
    private val authApi: AuthApi
) {

    suspend fun signin(username: String, password: String): Response<AuthResponse> {
        return authApi.signin("password", username, password)
    }

    suspend fun getUserInfo(): Response<ResponseHelper<ArrayList<Salary>>> {
        return authApi.getUserInfo()
    }
}