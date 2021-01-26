package kz.aura.merp.employee.data.service

import kz.aura.merp.employee.data.model.Auth
import kz.aura.merp.employee.data.model.ResponseHelper
import retrofit2.Response
import retrofit2.http.*

interface AuthApi {
    @GET("/ocr_transaction")
    suspend fun fetchTransactionId(): Response<ResponseHelper<String>>

    @POST("/ocr_transaction/token")
    suspend fun fetchToken(
        @Query("phoneNumber") phoneNumber: String,
        @Query("transactionId") transactionId: String
    ): Response<ResponseHelper<Auth>>
}