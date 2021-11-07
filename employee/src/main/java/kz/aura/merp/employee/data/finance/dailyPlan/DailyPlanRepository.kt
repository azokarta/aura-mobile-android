package kz.aura.merp.employee.data.finance.dailyPlan

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ViewModelScoped
import kz.aura.merp.employee.base.NetworkResult
import kz.aura.merp.employee.base.executeWithResponse
import kz.aura.merp.employee.model.ChangeBusinessProcess
import kz.aura.merp.employee.model.ChangePlanResult
import kz.aura.merp.employee.model.DailyPlan
import kz.aura.merp.employee.model.ResponseHelper
import javax.inject.Inject

@ViewModelScoped
class DailyPlanRepository @Inject constructor(
    private val dailyPlanService: DailyPlanService,
    @ApplicationContext private val context: Context
) {

    suspend fun fetchDailyPlans(): NetworkResult<ResponseHelper<List<DailyPlan>>> {
        return executeWithResponse(context) {
            dailyPlanService.fetchDailyPlans()
        }
    }

    suspend fun fetchDailyPlan(dailyPlanId: Long): NetworkResult<ResponseHelper<DailyPlan>> {
        return executeWithResponse(context) {
            dailyPlanService.fetchDailyPlan(dailyPlanId)
        }
    }

    suspend fun updateBusinessProcess(contractId: Long, businessProcess: ChangeBusinessProcess): NetworkResult<ResponseHelper<Nothing>> {
        return executeWithResponse(context) {
            dailyPlanService.updateBusinessProcessStatus(contractId, businessProcess)
        }
    }

    suspend fun changeResult(contractId: Long?, plan: ChangePlanResult): NetworkResult<ResponseHelper<Nothing>> {
        return executeWithResponse(context) {
            dailyPlanService.changeResult(contractId, plan)
        }
    }

}