package kz.aura.merp.employee.data.repository.financeRepository

import kz.aura.merp.employee.data.network.FinanceApi
import kz.aura.merp.employee.model.*
import kz.aura.merp.employee.util.Interceptor
import kz.aura.merp.employee.util.Link
import retrofit2.Response
import javax.inject.Inject

class FinanceRemoteDataSource @Inject constructor(
    private val financeApi: FinanceApi,
    interceptor: Interceptor
) {

    init {
        interceptor.setHost(Link.FINANCE)
    }

    suspend fun fetchPlans(): Response<ResponseHelper<ArrayList<Plan>>> {
        return financeApi.fetchPlans()
    }

    suspend fun updateBusinessProcessStep(contractId: Long, businessProcess: ChangeBusinessProcess): Response<ResponseHelper<Plan>> {
        return financeApi.updateBusinessProcessStep(contractId, businessProcess)
    }

    suspend fun fetchBusinessProcessStatuses(): Response<ResponseHelper<ArrayList<BusinessProcessStatus>>> {
        return financeApi.fetchBusinessProcessStatuses()
    }

    suspend fun fetchPaymentSchedule(contractId: Long): Response<ResponseHelper<ArrayList<PaymentSchedule>>> {
        return financeApi.fetchPaymentSchedule(contractId)
    }

    suspend fun fetchContributions(): Response<ResponseHelper<ArrayList<Contribution>>> {
        return financeApi.fetchContributions()
    }

    suspend fun fetchCalls(): Response<ResponseHelper<ArrayList<Call>>> {
        return financeApi.fetchCalls()
    }

    suspend fun fetchCallHistory(contractId: Long): Response<ResponseHelper<ArrayList<Call>>> {
        return financeApi.fetchCallHistory(contractId)
    }

    suspend fun fetchCallsForMonth(contractId: Long): Response<ResponseHelper<ArrayList<Call>>> {
        return financeApi.fetchCallsForMonth(contractId)
    }

    suspend fun assignCollectMoney(contractId: Long?, plan: ChangePlanResult): Response<ResponseHelper<ArrayList<Contribution>>> {
        return financeApi.assignCollectMoney(contractId, plan)
    }

    suspend fun fetchBanks(): Response<ResponseHelper<ArrayList<Bank>>> {
        return financeApi.fetchBanks()
    }

    suspend fun fetchPaymentMethods(): Response<ResponseHelper<ArrayList<PaymentMethod>>> {
        return financeApi.fetchPaymentMethods()
    }

    suspend fun fetchPlanResults(): Response<ResponseHelper<ArrayList<PlanResult>>> {
        return financeApi.fetchPlanResults()
    }

    suspend fun fetchPlanContributions(contractId: Long): Response<ResponseHelper<ArrayList<Contribution>>> {
        return financeApi.fetchPlanContributions(contractId)
    }

    suspend fun fetchCallDirections(): Response<ResponseHelper<ArrayList<CallDirection>>> {
        return financeApi.fetchCallDirections()
    }

    suspend fun fetchCallStatuses(): Response<ResponseHelper<ArrayList<CallStatus>>> {
        return financeApi.fetchCallStatuses()
    }

    suspend fun assignCall(assignCall: AssignCall, contractId: Long): Response<ResponseHelper<ArrayList<Call>>> {
        return financeApi.assignCall(assignCall, contractId)
    }

    suspend fun fetchScheduledCalls(): Response<ResponseHelper<ArrayList<ScheduledCall>>> {
        return financeApi.fetchScheduledCalls()
    }

    suspend fun fetchScheduledCallsHistory(contractId: Long): Response<ResponseHelper<ArrayList<ScheduledCall>>> {
        return financeApi.fetchScheduledCallsHistory(contractId)
    }
}