package kz.aura.merp.customer.services

import kz.aura.merp.customer.models.Message
import kz.aura.merp.customer.models.ResponseHelper
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface MessageApi {
    @GET("/ma-message/{customerId}")
    fun getAllMessages(@Path("customerId") customerId: Long): Call<ResponseHelper<ArrayList<Message>>>
}