package kz.aura.merp.customer.service

import kz.aura.merp.customer.data.model.CalendarView
import kz.aura.merp.customer.data.model.ResponseHelper
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface CalendarViewApi {
    @GET("payment-schedule/calendar-view/{customerId}")
    fun getArrears(@Path("customerId") customerId: Long): Call<ResponseHelper<ArrayList<CalendarView>>>
}