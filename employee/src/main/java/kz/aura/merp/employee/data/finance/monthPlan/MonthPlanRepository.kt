package kz.aura.merp.employee.data.finance.monthPlan

import dagger.hilt.android.scopes.ViewModelScoped
import kz.aura.merp.employee.base.NetworkResult
import kz.aura.merp.employee.base.executeWithResponse
import kz.aura.merp.employee.model.PaymentSchedule
import kz.aura.merp.employee.model.Plan
import kz.aura.merp.employee.model.ResponseHelper
import javax.inject.Inject

@ViewModelScoped
class MonthPlanRepository @Inject constructor(
    private val monthPlanService: MonthPlanService
) {
    suspend fun fetchPlans(): NetworkResult<ResponseHelper<List<Plan>>> {
        return executeWithResponse {
            monthPlanService.fetchPlans()
        }
    }

    suspend fun fetchPlan(contractId: Long): NetworkResult<ResponseHelper<Plan>> {
        return executeWithResponse {
            monthPlanService.fetchPlan(contractId)
        }
    }

    suspend fun fetchPaymentSchedule(contractId: Long): NetworkResult<ResponseHelper<List<PaymentSchedule>>> {
        return executeWithResponse {
            monthPlanService.fetchPaymentSchedule(contractId)
        }
    }

    suspend fun createDailyPlan(contractId: Long, planTime: String): NetworkResult<ResponseHelper<Nothing>> {
        return executeWithResponse {
            monthPlanService.createDailyPlan(contractId, planTime)
        }
    }
}