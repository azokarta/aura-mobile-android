package kz.aura.merp.employee.data.finance.dailyPlan

import kz.aura.merp.employee.model.ChangeBusinessProcess
import kz.aura.merp.employee.model.ChangePlanResult
import kz.aura.merp.employee.model.DailyPlan
import kz.aura.merp.employee.model.ResponseHelper
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface DailyPlanService {

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

}