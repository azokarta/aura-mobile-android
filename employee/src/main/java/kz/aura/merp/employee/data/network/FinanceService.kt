package kz.aura.merp.employee.data.network

import kz.aura.merp.employee.model.*
import retrofit2.Response
import retrofit2.http.*

interface FinanceService {

    // Month plan
    @GET("/month_plan")
    suspend fun fetchPlans(): Response<ResponseHelper<List<Plan>>>

    @GET("/month_plan/{contractId}")
    suspend fun fetchPlan(
        @Path("contractId") contractId: Long
    ): Response<ResponseHelper<Plan>>

    @GET("/month_plan/{contractId}/payments")
    suspend fun fetchPaymentSchedule(@Path("contractId") contractId: Long): Response<ResponseHelper<List<PaymentSchedule>>>

    @POST("/month_plan/{contractId}/create_daily_plan")
    suspend fun createDailyPlan(
        @Path("contractId") contractId: Long,
        @Query("planTime") planTime: String
    ): Response<ResponseHelper<Nothing>>



    // Daily plan
    @GET("/daily_plan")
    suspend fun fetchDailyPlans(): Response<ResponseHelper<List<DailyPlan>>>

    @GET("/daily_plan/{daily_plan_id}")
    suspend fun fetchDailyPlan(
        @Path("daily_plan_id") dailyPlanId: Long,
    ): Response<ResponseHelper<DailyPlan>>

    @POST("/daily_plan/{contractId}/change_business_process_status")
    suspend fun updateBusinessProcessStatus(
        @Path("contractId") contractId: Long,
        @Body businessProcess: ChangeBusinessProcess
    ): Response<ResponseHelper<Nothing>>

    @POST("/daily_plan/{contractId}/change_result")
    suspend fun changeResult(
        @Path("contractId") contractId: Long?,
        @Body plan: ChangePlanResult
    ): Response<ResponseHelper<Nothing>>



    // Calls
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


    // Contributions
    @GET("/collect_moneys")
    suspend fun fetchContributions(): Response<ResponseHelper<List<Contribution>>>

    @GET("/collect_moneys/{contractId}")
    suspend fun fetchContributionsByContractId(@Path("contractId") contractId: Long): Response<ResponseHelper<List<Contribution>>>



    // Scheduled calls
    @GET("/scheduled_calls")
    suspend fun fetchLastMonthScheduledCalls(): Response<ResponseHelper<List<ScheduledCall>>>

    @GET("/scheduled_calls/{contractId}/history")
    suspend fun fetchScheduledCallsHistory(@Path("contractId") contractId: Long): Response<ResponseHelper<List<ScheduledCall>>>

    @POST("/scheduled_calls/{contractId}/assign_scheduled_call")
    suspend fun assignScheduledCall(
        @Path("contractId") contractId: Long,
        @Body scheduledCall: AssignScheduledCallCommand
    ): Response<ResponseHelper<Nothing>>



    // References
    @GET("/references/call_directions")
    suspend fun fetchCallDirections(): Response<ResponseHelper<List<CallDirection>>>

    @GET("/references/call_statuses")
    suspend fun fetchCallStatuses(): Response<ResponseHelper<List<CallStatus>>>

    @GET("/references/business_process_statuses")
    suspend fun fetchBusinessProcessStatuses(): Response<ResponseHelper<List<BusinessProcessStatus>>>

    @GET("/references/collect_money_results")
    suspend fun fetchPlanResults(): Response<ResponseHelper<List<PlanResult>>>

    @GET("/references/banks")
    suspend fun fetchBanks(): Response<ResponseHelper<List<Bank>>>

    @GET("/references/payment_methods")
    suspend fun fetchPaymentMethods(): Response<ResponseHelper<List<PaymentMethod>>>

}