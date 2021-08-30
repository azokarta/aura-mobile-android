package kz.aura.merp.employee.data.finance.scheduledCall

import kz.aura.merp.employee.model.AssignScheduledCallCommand
import kz.aura.merp.employee.model.ResponseHelper
import kz.aura.merp.employee.model.ScheduledCall
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface ScheduledCallService {

    @GET("/scheduled_calls")
    suspend fun fetchLastMonthScheduledCalls(): Response<ResponseHelper<List<ScheduledCall>>>

    @GET("/scheduled_calls/{contractId}/history")
    suspend fun fetchScheduledCallHistory(@Path("contractId") contractId: Long): Response<ResponseHelper<List<ScheduledCall>>>

    @POST("/scheduled_calls/{contractId}/assign_scheduled_call")
    suspend fun assignScheduledCall(
        @Path("contractId") contractId: Long,
        @Body scheduledCall: AssignScheduledCallCommand
    ): Response<ResponseHelper<Nothing>>

}