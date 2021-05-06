package kz.aura.merp.employee.data.network

import kz.aura.merp.employee.model.AuthResponse
import kz.aura.merp.employee.model.ResponseHelper
import kz.aura.merp.employee.model.Salary
import retrofit2.Response
import retrofit2.http.*

interface AuthApi {
    @GET("/ocr_transaction")
    suspend fun fetchTransactionId(): Response<ResponseHelper<String>>

    @POST("/ocr_transaction/token")
    suspend fun fetchToken(
        @Query("phoneNumber") phoneNumber: String,
        @Query("transactionId") transactionId: String
    ): Response<ResponseHelper<AuthResponse>>

    @POST("/oauth/token")
    suspend fun signin(
         @Query("grant_type") grant_type: String = "password",
         @Query("username") username: String,
         @Query("password") password: String
    ): Response<AuthResponse>

    @GET("/current_user_info/salaries")
    suspend fun getUserInfo(): Response<ResponseHelper<ArrayList<Salary>>>
}