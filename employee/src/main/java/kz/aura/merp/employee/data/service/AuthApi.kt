package kz.aura.merp.employee.data.service

import kz.aura.merp.employee.data.model.AuthResponse
import kz.aura.merp.employee.data.model.ResponseHelper
import kz.aura.merp.employee.data.model.Staff
import okhttp3.RequestBody
import retrofit2.Call
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

    @GET("/userinfo")
    suspend fun getUserInfo(
        @Query("mobileNumber") mobileNumber: String
    ): Response<ResponseHelper<Staff>>
}