package kz.aura.merp.employee.data.finance.reference

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ViewModelScoped
import kz.aura.merp.employee.base.NetworkResult
import kz.aura.merp.employee.base.executeWithResponse
import kz.aura.merp.employee.model.*
import javax.inject.Inject

@ViewModelScoped
class ReferenceRepository @Inject constructor(
    private val referenceService: ReferenceService,
    @ApplicationContext private val context: Context
) {

    suspend fun fetchBanks(): NetworkResult<ResponseHelper<List<Bank>>> {
        return executeWithResponse(context) {
            referenceService.fetchBanks()
        }
    }

    suspend fun fetchPaymentMethods(): NetworkResult<ResponseHelper<List<PaymentMethod>>> {
        return executeWithResponse(context) {
            referenceService.fetchPaymentMethods()
        }
    }

    suspend fun fetchBusinessProcessStatuses(): NetworkResult<ResponseHelper<List<BusinessProcessStatus>>> {
        return executeWithResponse(context) {
            referenceService.fetchBusinessProcessStatuses()
        }
    }

    suspend fun fetchCallDirections(): NetworkResult<ResponseHelper<List<CallDirection>>> {
        return executeWithResponse(context) {
            referenceService.fetchCallDirections()
        }
    }

    suspend fun fetchCallStatuses(): NetworkResult<ResponseHelper<List<CallStatus>>> {
        return executeWithResponse(context) {
            referenceService.fetchCallStatuses()
        }
    }

    suspend fun fetchPlanResults(): NetworkResult<ResponseHelper<List<PlanResult>>> {
        return executeWithResponse(context) {
            referenceService.fetchPlanResults()
        }
    }

}