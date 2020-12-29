package kz.aura.merp.customer.service

import kz.aura.merp.customer.data.model.ResponseHelper
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface HomeApi {
    @GET("/ma-message/unreadMessageCount/{customerId}")
    fun getUnreadMessagesCount(@Path("customerId") customerId: Long): Call<ResponseHelper<Int>>
}