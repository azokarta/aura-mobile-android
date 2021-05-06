package kz.aura.merp.employee.data.network

import kz.aura.merp.employee.model.*
import retrofit2.Response
import retrofit2.http.*

interface FinanceApi {
    @GET("/plans")
    suspend fun fetchPlans(): Response<ResponseHelper<ArrayList<Plan>>>

    @POST("/plans/{contractId}/assign_collect_money")
    suspend fun assignCollectMoney(
        @Path("contractId") contractId: Long?,
        @Body plan: ChangePlanResult
    ): Response<ResponseHelper<ArrayList<Contribution>>>

    @POST("/plans/{contractId}/change_business_process_status")
    suspend fun updateBusinessProcessStep(
        @Path("contractId") contractId: Long,
        @Body businessProcess: ChangeBusinessProcess
    ): Response<ResponseHelper<Plan>>

    @GET("/references/business_process_statuses")
    suspend fun fetchBusinessProcessStatuses(): Response<ResponseHelper<ArrayList<BusinessProcessStatus>>>

    @GET("/references/collect_money_results")
    suspend fun fetchPlanResults(): Response<ResponseHelper<ArrayList<PlanResult>>>

    @GET("/plans/{contractId}/payments")
    suspend fun fetchPaymentSchedule(@Path("contractId") contractId: Long): Response<ResponseHelper<ArrayList<PaymentSchedule>>>

    @GET("/references/banks")
    suspend fun fetchBanks(): Response<ResponseHelper<ArrayList<Bank>>>

    @GET("/references/payment_methods")
    suspend fun fetchPaymentMethods(): Response<ResponseHelper<ArrayList<PaymentMethod>>>

    @GET("/plans/collect_moneys")
    suspend fun fetchContributions(): Response<ResponseHelper<ArrayList<Contribution>>>

    @GET("/plans/calls")
    suspend fun fetchCalls(): Response<ResponseHelper<ArrayList<Call>>>

    @GET("/plans/scheduled_calls")
    suspend fun fetchScheduledCalls(): Response<ResponseHelper<ArrayList<ScheduledCall>>>

    @GET("/plans/{contractId}/history/calls")
    suspend fun fetchCallHistory(@Path("contractId") contractId: Long): Response<ResponseHelper<ArrayList<Call>>>

    @GET("/plans/{contractId}/collect_moneys")
    suspend fun fetchPlanContributions(@Path("contractId") contractId: Long): Response<ResponseHelper<ArrayList<Contribution>>>

    @POST("/plans/{contractId}/assign_call")
    suspend fun assignCall(
        @Body assignCall: AssignCall,
        @Path("contractId") contractId: Long): Response<ResponseHelper<ArrayList<Call>>>

    @GET("/references/call_directions")
    suspend fun fetchCallDirections(): Response<ResponseHelper<ArrayList<CallDirection>>>

    @GET("/references/call_statuses")
    suspend fun fetchCallStatuses(): Response<ResponseHelper<ArrayList<CallStatus>>>

    @GET("/plans/{contractId}/history/scheduled_calls")
    suspend fun fetchScheduledCallsHistory(@Path("contractId") contractId: Long): Response<ResponseHelper<ArrayList<ScheduledCall>>>

    @GET("/plans/{contractId}/calls")
    suspend fun fetchCallsForMonth(@Path("contractId") contractId: Long): Response<ResponseHelper<ArrayList<Call>>>
}