package kz.aura.merp.employee.data.finance.scheduledCall

import dagger.hilt.android.scopes.ViewModelScoped
import kz.aura.merp.employee.model.AssignScheduledCallCommand
import kz.aura.merp.employee.model.ResponseHelper
import kz.aura.merp.employee.model.ScheduledCall
import retrofit2.Response
import javax.inject.Inject

@ViewModelScoped
class ScheduledCallRepository @Inject constructor(
    private val scheduledCallService: ScheduledCallService
) {

    suspend fun fetchLastMonthScheduledCalls(): Response<ResponseHelper<List<ScheduledCall>>> {
        return scheduledCallService.fetchLastMonthScheduledCalls()
    }

    suspend fun fetchScheduledCallsHistory(contractId: Long): Response<ResponseHelper<List<ScheduledCall>>> {
        return scheduledCallService.fetchScheduledCallsHistory(contractId)
    }

    suspend fun assignScheduledCall(contractId: Long, scheduledCall: AssignScheduledCallCommand): Response<ResponseHelper<Nothing>> {
        return scheduledCallService.assignScheduledCall(contractId, scheduledCall)
    }

}