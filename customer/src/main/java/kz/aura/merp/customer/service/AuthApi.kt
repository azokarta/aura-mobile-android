package kz.aura.merp.customer.service

import kz.aura.merp.customer.data.model.Auth
import kz.aura.merp.customer.data.model.ResponseHelper
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query


interface AuthApi {
    @GET("/ocr_transaction")
    fun fetchTransactionId(): Call<ResponseHelper<String>>

    @GET("/ocr_transaction/token")
    fun fetchToken(@Query("transactionId") transactionId: String): Call<ResponseHelper<Auth>>
}