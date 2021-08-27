package kz.aura.merp.employee.data.core

import kz.aura.merp.employee.model.ResponseHelper
import kz.aura.merp.employee.model.Salary
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface CoreService {

    @GET("/current_user_info/salaries")
    suspend fun getUserInfo(): Response<ResponseHelper<List<Salary>>>

    @POST("/current_user_info/set_employee_token/{token}")
    suspend fun saveFcmToken(
        @Path("token") token: String
    ): Response<ResponseHelper<Nothing>>

}