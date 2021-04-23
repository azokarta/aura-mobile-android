package kz.aura.merp.employee.data.network

import kz.aura.merp.employee.model.*
import retrofit2.Response
import retrofit2.http.*

interface FinanceApi {
    @GET("/plans")
    suspend fun fetchPlans(): Response<ResponseHelper<ArrayList<Plan>>>

    @POST("/plans/{contractId}/change_result")
    suspend fun updatePlanResult(
        @Path("contractId") contractId: Long,
        @Body plan: ChangePlanResult
    ): Response<ResponseHelper<Plan>>

    @POST("/plans/{contractId}/change_business_process_status")
    suspend fun updateBusinessProcessStep(
        @Path("contractId") contractId: Long,
        @Body businessProcess: ChangeBusinessProcess
    ): Response<ResponseHelper<Plan>>

    @GET("/references/business_process_status")
    suspend fun fetchBusinessProcessStatuses(): Response<ResponseHelper<ArrayList<BusinessProcessStatus>>>

    @GET("/references/plan_result")
    suspend fun fetchPlanResults(): Response<ResponseHelper<ArrayList<PlanResult>>>

    @GET("/plans/{contractId}/payments")
    suspend fun fetchPaymentSchedule(@Path("contractId") contractId: Long): Response<ResponseHelper<ArrayList<PaymentSchedule>>>

    @GET("/references/banks")
    suspend fun fetchBanks(): Response<ResponseHelper<ArrayList<Bank>>>

    @GET("/references/paymentMethods")
    suspend fun fetchPaymentMethods(): Response<ResponseHelper<ArrayList<PaymentMethod>>>

    @GET("/plans/{contractId}/payment_history")
    suspend fun fetchHistory(@Path("contractId") contractId: Long): Response<ResponseHelper<ArrayList<PlanHistoryItem>>>

    @GET("/plans/collect_money_results")
    suspend fun fetchContributions(): Response<ResponseHelper<ArrayList<Contribution>>>

    @GET("/plans/calls")
    suspend fun fetchCalls(): Response<ResponseHelper<ArrayList<Call>>>

    @GET("/plans/collect_money_results")
    suspend fun fetchScheduledCalls(): Response<ResponseHelper<ArrayList<ScheduledCall>>>
}