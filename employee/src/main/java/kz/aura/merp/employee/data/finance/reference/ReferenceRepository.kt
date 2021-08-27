package kz.aura.merp.employee.data.finance.reference

import dagger.hilt.android.scopes.ViewModelScoped
import kz.aura.merp.employee.base.NetworkResult
import kz.aura.merp.employee.base.executeWithResponse
import kz.aura.merp.employee.model.*
import javax.inject.Inject

@ViewModelScoped
class ReferenceRepository @Inject constructor(
    private val referenceService: ReferenceService
) {

    suspend fun fetchBanks(): NetworkResult<ResponseHelper<List<Bank>>> {
        return executeWithResponse {
            referenceService.fetchBanks()
        }
    }

    suspend fun fetchPaymentMethods(): NetworkResult<ResponseHelper<List<PaymentMethod>>> {
        return executeWithResponse {
            referenceService.fetchPaymentMethods()
        }
    }

    suspend fun fetchBusinessProcessStatuses(): NetworkResult<ResponseHelper<List<BusinessProcessStatus>>> {
        return executeWithResponse {
            referenceService.fetchBusinessProcessStatuses()
        }
    }

    suspend fun fetchCallDirections(): NetworkResult<ResponseHelper<List<CallDirection>>> {
        return executeWithResponse {
            referenceService.fetchCallDirections()
        }
    }

    suspend fun fetchCallStatuses(): NetworkResult<ResponseHelper<List<CallStatus>>> {
        return executeWithResponse {
            referenceService.fetchCallStatuses()
        }
    }

    suspend fun fetchPlanResults(): NetworkResult<ResponseHelper<List<PlanResult>>> {
        return executeWithResponse {
            referenceService.fetchPlanResults()
        }
    }

}