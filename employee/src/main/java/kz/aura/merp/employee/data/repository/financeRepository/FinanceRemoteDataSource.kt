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



    // Month plan
    suspend fun fetchPlans(): Response<ResponseHelper<ArrayList<Plan>>> {
        return financeApi.fetchPlans()
    }

    suspend fun fetchPaymentSchedule(contractId: Long): Response<ResponseHelper<ArrayList<PaymentSchedule>>> {
        return financeApi.fetchPaymentSchedule(contractId)
    }

    suspend fun createDailyPlan(contractId: Long, planTime: String): Response<ResponseHelper<*>> {
        return financeApi.createDailyPlan(contractId, planTime)
    }



    // Daily plan
    suspend fun fetchDailyPlan(): Response<ResponseHelper<ArrayList<Plan>>> {
        return financeApi.fetchDailyPlan()
    }

    suspend fun updateBusinessProcess(contractId: Long, businessProcess: ChangeBusinessProcess): Response<ResponseHelper<Plan>> {
        return financeApi.updateBusinessProcessStatus(contractId, businessProcess)
    }

    suspend fun changeResult(contractId: Long?, plan: ChangePlanResult): Response<ResponseHelper<*>> {
        return financeApi.changeResult(contractId, plan)
    }



    // Contributions
    suspend fun fetchContributions(): Response<ResponseHelper<ArrayList<Contribution>>> {
        return financeApi.fetchContributions()
    }

    suspend fun fetchContributionsByContractId(contractId: Long): Response<ResponseHelper<ArrayList<Contribution>>> {
        return financeApi.fetchContributionsByContractId(contractId)
    }



    // Calls
    suspend fun fetchLastMonthCalls(): Response<ResponseHelper<ArrayList<Call>>> {
        return financeApi.fetchLastMonthCalls()
    }

    suspend fun fetchCallHistory(contractId: Long): Response<ResponseHelper<ArrayList<Call>>> {
        return financeApi.fetchCallHistory(contractId)
    }

    suspend fun fetchLastMonthCallsByContractId(contractId: Long): Response<ResponseHelper<ArrayList<Call>>> {
        return financeApi.fetchLastMonthCallsByContractId(contractId)
    }

    suspend fun assignIncomingCall(contractId: Long, assignCall: AssignCall): Response<ResponseHelper<*>> {
        return financeApi.assignIncomingCall(contractId, assignCall)
    }

    suspend fun assignOutgoingCall(contractId: Long, assignCall: AssignCall): Response<ResponseHelper<*>> {
        return financeApi.assignOutgoingCall(contractId, assignCall)
    }



    // Scheduled calls
    suspend fun fetchLastMonthScheduledCalls(): Response<ResponseHelper<ArrayList<ScheduledCall>>> {
        return financeApi.fetchLastMonthScheduledCalls()
    }

    suspend fun fetchScheduledCallsHistory(contractId: Long): Response<ResponseHelper<ArrayList<ScheduledCall>>> {
        return financeApi.fetchScheduledCallsHistory(contractId)
    }


    // References
    suspend fun fetchBanks(): Response<ResponseHelper<ArrayList<Bank>>> {
        return financeApi.fetchBanks()
    }

    suspend fun fetchPaymentMethods(): Response<ResponseHelper<ArrayList<PaymentMethod>>> {
        return financeApi.fetchPaymentMethods()
    }

    suspend fun fetchBusinessProcessStatuses(): Response<ResponseHelper<ArrayList<BusinessProcessStatus>>> {
        return financeApi.fetchBusinessProcessStatuses()
    }

    suspend fun fetchCallDirections(): Response<ResponseHelper<ArrayList<CallDirection>>> {
        return financeApi.fetchCallDirections()
    }

    suspend fun fetchCallStatuses(): Response<ResponseHelper<ArrayList<CallStatus>>> {
        return financeApi.fetchCallStatuses()
    }

    suspend fun fetchPlanResults(): Response<ResponseHelper<ArrayList<PlanResult>>> {
        return financeApi.fetchPlanResults()
    }
}