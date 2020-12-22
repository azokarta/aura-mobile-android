package kz.aura.merp.customer.services

import kz.aura.merp.customer.models.ResponseHelper
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface HomeApi {
    @GET("/ma-message/unreadMessageCount/{customerId}")
    fun getUnreadMessagesCount(@Path("customerId") customerId: Long): Call<ResponseHelper<Int>>
}