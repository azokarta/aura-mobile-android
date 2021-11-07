package kz.aura.merp.employee.data.finance.scheduledCall

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ViewModelScoped
import kz.aura.merp.employee.base.NetworkResult
import kz.aura.merp.employee.base.executeWithResponse
import kz.aura.merp.employee.model.AssignScheduledCallCommand
import kz.aura.merp.employee.model.ResponseHelper
import kz.aura.merp.employee.model.ScheduledCall
import javax.inject.Inject

@ViewModelScoped
class ScheduledCallRepository @Inject constructor(
    private val scheduledCallService: ScheduledCallService,
    @ApplicationContext private val context: Context
) {

    suspend fun fetchLastMonthScheduledCalls(): NetworkResult<ResponseHelper<List<ScheduledCall>>> {
        return executeWithResponse(context) {
            scheduledCallService.fetchLastMonthScheduledCalls()
        }
    }

    suspend fun fetchScheduledCallHistory(contractId: Long): NetworkResult<ResponseHelper<List<ScheduledCall>>> {
        return executeWithResponse(context) {
            scheduledCallService.fetchScheduledCallHistory(contractId)
        }
    }

    suspend fun assignScheduledCall(contractId: Long, scheduledCall: AssignScheduledCallCommand): NetworkResult<ResponseHelper<Nothing>> {
        return executeWithResponse(context) {
            scheduledCallService.assignScheduledCall(contractId, scheduledCall)
        }
    }

}