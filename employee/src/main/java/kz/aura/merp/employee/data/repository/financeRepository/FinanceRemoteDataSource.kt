package kz.aura.merp.employee.data.repository.financeRepository

import kz.aura.merp.employee.data.network.FinanceApi
import kz.aura.merp.employee.di.NetworkModule
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
}