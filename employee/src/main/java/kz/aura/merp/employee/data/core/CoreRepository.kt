package kz.aura.merp.employee.data.core

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ViewModelScoped
import kz.aura.merp.employee.base.NetworkResult
import kz.aura.merp.employee.base.executeWithResponse
import kz.aura.merp.employee.model.ResponseHelper
import kz.aura.merp.employee.model.Salary
import javax.inject.Inject

@ViewModelScoped
class CoreRepository @Inject constructor(
    private val coreService: CoreService,
    @ApplicationContext private val context: Context
) {

    suspend fun getUserInfo(): NetworkResult<ResponseHelper<List<Salary>>> {
        return executeWithResponse(context) {
            coreService.getUserInfo()
        }
    }

    suspend fun saveFcmToken(token: String): NetworkResult<ResponseHelper<Nothing>> {
        return executeWithResponse(context) {
            coreService.saveFcmToken(token)
        }
    }

}