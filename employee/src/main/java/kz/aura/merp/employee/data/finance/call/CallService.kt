package kz.aura.merp.employee.data.finance.call

import kz.aura.merp.employee.model.AssignCall
import kz.aura.merp.employee.model.Call
import kz.aura.merp.employee.model.ResponseHelper
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface CallService {

    @GET("/calls")
    suspend fun fetchLastMonthCalls(): Response<ResponseHelper<List<Call>>>

    @GET("/calls/{contractId}/history")
    suspend fun fetchCallHistory(@Path("contractId") contractId: Long): Response<ResponseHelper<List<Call>>>

    @GET("/calls/{contractId}")
    suspend fun fetchLastMonthCallsByContractId(@Path("contractId") contractId: Long): Response<ResponseHelper<List<Call>>>

    @POST("/calls/{contractId}/assign_incoming_call")
    suspend fun assignIncomingCall(
        @Path("contractId") contractId: Long,
        @Body assignCall: AssignCall
    ): Response<ResponseHelper<Nothing>>

    @POST("/calls/{contractId}/assign_outgoing_call")
    suspend fun assignOutgoingCall(
        @Path("contractId") contractId: Long,
        @Body assignCall: AssignCall
    ): Response<ResponseHelper<Nothing>>

}