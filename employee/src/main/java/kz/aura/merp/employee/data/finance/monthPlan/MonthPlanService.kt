package kz.aura.merp.employee.data.finance.monthPlan

import kz.aura.merp.employee.model.PaymentSchedule
import kz.aura.merp.employee.model.Plan
import kz.aura.merp.employee.model.ResponseHelper
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface MonthPlanService {

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

}