package kz.aura.merp.customer.service

import kz.aura.merp.customer.data.model.Message
import kz.aura.merp.customer.data.model.ResponseHelper
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface MessageApi {
    @GET("/ma-message/{customerId}")
    fun getAllMessages(@Path("customerId") customerId: Long): Call<ResponseHelper<ArrayList<Message>>>
}