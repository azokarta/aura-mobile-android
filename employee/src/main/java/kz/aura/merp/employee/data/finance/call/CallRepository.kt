package kz.aura.merp.employee.data.finance.call

import dagger.hilt.android.scopes.ViewModelScoped
import kz.aura.merp.employee.base.NetworkResult
import kz.aura.merp.employee.base.executeWithResponse
import kz.aura.merp.employee.model.AssignCall
import kz.aura.merp.employee.model.Call
import kz.aura.merp.employee.model.ResponseHelper
import javax.inject.Inject

@ViewModelScoped
class CallRepository @Inject constructor(
    private val callService: CallService
) {

    suspend fun fetchLastMonthCalls(): NetworkResult<ResponseHelper<List<Call>>> {
        return executeWithResponse {
            callService.fetchLastMonthCalls()
        }
    }

    suspend fun fetchCallsHistory(contractId: Long): NetworkResult<ResponseHelper<List<Call>>> {
        return executeWithResponse {
            callService.fetchCallsHistory(contractId)
        }
    }

    suspend fun fetchLastMonthCallsByContractId(contractId: Long): NetworkResult<ResponseHelper<List<Call>>> {
        return executeWithResponse {
            callService.fetchLastMonthCallsByContractId(contractId)
        }
    }

    suspend fun assignIncomingCall(contractId: Long, assignCall: AssignCall): NetworkResult<ResponseHelper<Nothing>> {
        return executeWithResponse {
            callService.assignIncomingCall(contractId, assignCall)
        }
    }

    suspend fun assignOutgoingCall(contractId: Long, assignCall: AssignCall): NetworkResult<ResponseHelper<Nothing>> {
        return executeWithResponse {
            callService.assignOutgoingCall(contractId, assignCall)
        }
    }

}