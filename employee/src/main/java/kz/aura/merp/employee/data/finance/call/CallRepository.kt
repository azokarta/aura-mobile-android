package kz.aura.merp.employee.data.finance.call

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ViewModelScoped
import kz.aura.merp.employee.base.NetworkResult
import kz.aura.merp.employee.base.executeWithResponse
import kz.aura.merp.employee.model.AssignCall
import kz.aura.merp.employee.model.Call
import kz.aura.merp.employee.model.ResponseHelper
import javax.inject.Inject

@ViewModelScoped
class CallRepository @Inject constructor(
    private val callService: CallService,
    @ApplicationContext private val context: Context
) {

    suspend fun fetchLastMonthCalls(): NetworkResult<ResponseHelper<List<Call>>> {
        return executeWithResponse(context) {
            callService.fetchLastMonthCalls()
        }
    }

    suspend fun fetchCallHistory(contractId: Long): NetworkResult<ResponseHelper<List<Call>>> {
        return executeWithResponse(context) {
            callService.fetchCallHistory(contractId)
        }
    }

    suspend fun fetchLastMonthCallsByContractId(contractId: Long): NetworkResult<ResponseHelper<List<Call>>> {
        return executeWithResponse(context) {
            callService.fetchLastMonthCallsByContractId(contractId)
        }
    }

    suspend fun assignIncomingCall(contractId: Long, assignCall: AssignCall): NetworkResult<ResponseHelper<Nothing>> {
        return executeWithResponse(context) {
            callService.assignIncomingCall(contractId, assignCall)
        }
    }

    suspend fun assignOutgoingCall(contractId: Long, assignCall: AssignCall): NetworkResult<ResponseHelper<Nothing>> {
        return executeWithResponse(context) {
            callService.assignOutgoingCall(contractId, assignCall)
        }
    }

}