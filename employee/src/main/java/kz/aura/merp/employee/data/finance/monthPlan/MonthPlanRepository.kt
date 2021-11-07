package kz.aura.merp.employee.data.finance.monthPlan

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ViewModelScoped
import kz.aura.merp.employee.base.NetworkResult
import kz.aura.merp.employee.base.executeWithResponse
import kz.aura.merp.employee.model.PaymentSchedule
import kz.aura.merp.employee.model.Plan
import kz.aura.merp.employee.model.ResponseHelper
import javax.inject.Inject

@ViewModelScoped
class MonthPlanRepository @Inject constructor(
    private val monthPlanService: MonthPlanService,
    @ApplicationContext private val context: Context
) {
    suspend fun fetchPlans(): NetworkResult<ResponseHelper<List<Plan>>> {
        return executeWithResponse(context) {
            monthPlanService.fetchPlans()
        }
    }

    suspend fun fetchPlan(contractId: Long): NetworkResult<ResponseHelper<Plan>> {
        return executeWithResponse(context) {
            monthPlanService.fetchPlan(contractId)
        }
    }

    suspend fun fetchPaymentSchedule(contractId: Long): NetworkResult<ResponseHelper<List<PaymentSchedule>>> {
        return executeWithResponse(context) {
            monthPlanService.fetchPaymentSchedule(contractId)
        }
    }

    suspend fun createDailyPlan(contractId: Long, planTime: String): NetworkResult<ResponseHelper<Nothing>> {
        return executeWithResponse(context) {
            monthPlanService.createDailyPlan(contractId, planTime)
        }
    }
}