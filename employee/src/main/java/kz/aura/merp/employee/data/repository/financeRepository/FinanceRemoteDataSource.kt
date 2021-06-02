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
    suspend fun fetchPlans(): Response<ResponseHelper<List<Plan>>> {
        return financeApi.fetchPlans()
    }

    suspend fun fetchPlan(contractId: Long): Response<ResponseHelper<Plan>> {
        return financeApi.fetchPlan(contractId)
    }

    suspend fun fetchPaymentSchedule(contractId: Long): Response<ResponseHelper<List<PaymentSchedule>>> {
        return financeApi.fetchPaymentSchedule(contractId)
    }

    suspend fun createDailyPlan(contractId: Long, planTime: String): Response<ResponseHelper<Nothing>> {
        return financeApi.createDailyPlan(contractId, planTime)
    }



    // Daily plan
    suspend fun fetchDailyPlan(): Response<ResponseHelper<List<Plan>>> {
        return financeApi.fetchDailyPlan()
    }

    suspend fun updateBusinessProcess(contractId: Long, businessProcess: ChangeBusinessProcess): Response<ResponseHelper<Nothing>> {
        return financeApi.updateBusinessProcessStatus(contractId, businessProcess)
    }

    suspend fun changeResult(contractId: Long?, plan: ChangePlanResult): Response<ResponseHelper<Nothing>> {
        return financeApi.changeResult(contractId, plan)
    }



    // Contributions
    suspend fun fetchContributions(): Response<ResponseHelper<List<Contribution>>> {
        return financeApi.fetchContributions()
    }

    suspend fun fetchContributionsByContractId(contractId: Long): Response<ResponseHelper<List<Contribution>>> {
        return financeApi.fetchContributionsByContractId(contractId)
    }



    // Calls
    suspend fun fetchLastMonthCalls(): Response<ResponseHelper<List<Call>>> {
        return financeApi.fetchLastMonthCalls()
    }

    suspend fun fetchCallHistory(contractId: Long): Response<ResponseHelper<List<Call>>> {
        return financeApi.fetchCallHistory(contractId)
    }

    suspend fun fetchLastMonthCallsByContractId(contractId: Long): Response<ResponseHelper<List<Call>>> {
        return financeApi.fetchLastMonthCallsByContractId(contractId)
    }

    suspend fun assignIncomingCall(contractId: Long, assignCall: AssignCall): Response<ResponseHelper<Nothing>> {
        return financeApi.assignIncomingCall(contractId, assignCall)
    }

    suspend fun assignOutgoingCall(contractId: Long, assignCall: AssignCall): Response<ResponseHelper<Nothing>> {
        return financeApi.assignOutgoingCall(contractId, assignCall)
    }



    // Scheduled calls
    suspend fun fetchLastMonthScheduledCalls(): Response<ResponseHelper<List<ScheduledCall>>> {
        return financeApi.fetchLastMonthScheduledCalls()
    }

    suspend fun fetchScheduledCallsHistory(contractId: Long): Response<ResponseHelper<List<ScheduledCall>>> {
        return financeApi.fetchScheduledCallsHistory(contractId)
    }

    suspend fun assignScheduledCall(contractId: Long, scheduledCall: AssignScheduledCallCommand): Response<ResponseHelper<Nothing>> {
        return financeApi.assignScheduledCall(contractId, scheduledCall)
    }


    // References
    suspend fun fetchBanks(): Response<ResponseHelper<List<Bank>>> {
        return financeApi.fetchBanks()
    }

    suspend fun fetchPaymentMethods(): Response<ResponseHelper<List<PaymentMethod>>> {
        return financeApi.fetchPaymentMethods()
    }

    suspend fun fetchBusinessProcessStatuses(): Response<ResponseHelper<List<BusinessProcessStatus>>> {
        return financeApi.fetchBusinessProcessStatuses()
    }

    suspend fun fetchCallDirections(): Response<ResponseHelper<List<CallDirection>>> {
        return financeApi.fetchCallDirections()
    }

    suspend fun fetchCallStatuses(): Response<ResponseHelper<List<CallStatus>>> {
        return financeApi.fetchCallStatuses()
    }

    suspend fun fetchPlanResults(): Response<ResponseHelper<List<PlanResult>>> {
        return financeApi.fetchPlanResults()
    }
}