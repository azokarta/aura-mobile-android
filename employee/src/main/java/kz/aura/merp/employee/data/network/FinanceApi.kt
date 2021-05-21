package kz.aura.merp.employee.data.network

import kz.aura.merp.employee.model.*
import retrofit2.Response
import retrofit2.http.*

interface FinanceApi {

    // Month plan
    @GET("/month_plan")
    suspend fun fetchPlans(): Response<ResponseHelper<ArrayList<Plan>>>

    @GET("/month_plan/{contractId}")
    suspend fun fetchPlan(
        @Path("contractId") contractId: Long
    ): Response<ResponseHelper<Plan>>

    @GET("/month_plan/{contractId}/payments")
    suspend fun fetchPaymentSchedule(@Path("contractId") contractId: Long): Response<ResponseHelper<ArrayList<PaymentSchedule>>>

    @POST("/month_plan/{contractId}/create_daily_plan")
    suspend fun createDailyPlan(
        @Path("contractId") contractId: Long,
        @Query("planTime") planTime: String
    ): Response<ResponseHelper<*>>



    // Daily plan
    @GET("/daily_plan")
    suspend fun fetchDailyPlan(): Response<ResponseHelper<ArrayList<Plan>>>

    @POST("/daily_plan/{contractId}/change_business_process_status")
    suspend fun updateBusinessProcessStatus(
        @Path("contractId") contractId: Long,
        @Body businessProcess: ChangeBusinessProcess
    ): Response<ResponseHelper<*>>

    @POST("/daily_plan/{contractId}/change_result")
    suspend fun changeResult(
        @Path("contractId") contractId: Long?,
        @Body plan: ChangePlanResult
    ): Response<ResponseHelper<*>>



    // Calls
    @GET("/calls")
    suspend fun fetchLastMonthCalls(): Response<ResponseHelper<ArrayList<Call>>>

    @GET("/calls/{contractId}/history")
    suspend fun fetchCallHistory(@Path("contractId") contractId: Long): Response<ResponseHelper<ArrayList<Call>>>

    @GET("/calls/{contractId}")
    suspend fun fetchLastMonthCallsByContractId(@Path("contractId") contractId: Long): Response<ResponseHelper<ArrayList<Call>>>

    @POST("/calls/{contractId}/assign_incoming_call")
    suspend fun assignIncomingCall(
        @Path("contractId") contractId: Long,
        @Body assignCall: AssignCall
    ): Response<ResponseHelper<*>>

    @POST("/calls/{contractId}/assign_outgoing_call")
    suspend fun assignOutgoingCall(
        @Path("contractId") contractId: Long,
        @Body assignCall: AssignCall
    ): Response<ResponseHelper<*>>


    // Contributions
    @GET("/collect_moneys")
    suspend fun fetchContributions(): Response<ResponseHelper<ArrayList<Contribution>>>

    @GET("/collect_moneys/{contractId}")
    suspend fun fetchContributionsByContractId(@Path("contractId") contractId: Long): Response<ResponseHelper<ArrayList<Contribution>>>



    // Scheduled calls
    @GET("/scheduled_calls")
    suspend fun fetchLastMonthScheduledCalls(): Response<ResponseHelper<ArrayList<ScheduledCall>>>

    @GET("/scheduled_calls/{contractId}/history")
    suspend fun fetchScheduledCallsHistory(@Path("contractId") contractId: Long): Response<ResponseHelper<ArrayList<ScheduledCall>>>



    // References
    @GET("/references/call_directions")
    suspend fun fetchCallDirections(): Response<ResponseHelper<ArrayList<CallDirection>>>

    @GET("/references/call_statuses")
    suspend fun fetchCallStatuses(): Response<ResponseHelper<ArrayList<CallStatus>>>

    @GET("/references/business_process_statuses")
    suspend fun fetchBusinessProcessStatuses(): Response<ResponseHelper<ArrayList<BusinessProcessStatus>>>

    @GET("/references/collect_money_results")
    suspend fun fetchPlanResults(): Response<ResponseHelper<ArrayList<PlanResult>>>

    @GET("/references/banks")
    suspend fun fetchBanks(): Response<ResponseHelper<ArrayList<Bank>>>

    @GET("/references/payment_methods")
    suspend fun fetchPaymentMethods(): Response<ResponseHelper<ArrayList<PaymentMethod>>>

}