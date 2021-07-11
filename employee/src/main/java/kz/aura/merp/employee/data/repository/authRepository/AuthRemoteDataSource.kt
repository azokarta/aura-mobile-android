package kz.aura.merp.employee.data.repository.authRepository

import kz.aura.merp.employee.model.AuthResponse
import kz.aura.merp.employee.model.ResponseHelper
import kz.aura.merp.employee.model.Salary
import kz.aura.merp.employee.data.network.AuthApi
import kz.aura.merp.employee.util.Interceptor
import kz.aura.merp.employee.util.Link
import retrofit2.Response
import javax.inject.Inject

class AuthRemoteDataSource @Inject constructor(
    private val authApi: AuthApi,
    private val interceptor: Interceptor
) {

    suspend fun signin(username: String, password: String): Response<AuthResponse> {
        interceptor.setHost(Link.AUTH)
        return authApi.signin("password", username, password)
    }

    suspend fun getUserInfo(): Response<ResponseHelper<ArrayList<Salary>>> {
        interceptor.setHost(Link.MAIN)
        return authApi.getUserInfo()
    }

    suspend fun saveFcmToken(token: String): Response<ResponseHelper<Nothing>> {
        interceptor.setHost(Link.MAIN)
        return authApi.saveFcmToken(token)
    }
}