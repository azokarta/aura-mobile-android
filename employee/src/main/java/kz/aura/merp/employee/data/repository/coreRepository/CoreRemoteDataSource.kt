package kz.aura.merp.employee.data.repository.coreRepository

import kz.aura.merp.employee.data.network.CoreService
import kz.aura.merp.employee.model.ResponseHelper
import kz.aura.merp.employee.model.Salary
import retrofit2.Response
import javax.inject.Inject

class CoreRemoteDataSource @Inject constructor(
    private val coreService: CoreService
) {
    suspend fun getUserInfo(): Response<ResponseHelper<ArrayList<Salary>>> {
        return coreService.getUserInfo()
    }

    suspend fun saveFcmToken(token: String): Response<ResponseHelper<Nothing>> {
        return coreService.saveFcmToken(token)
    }
}