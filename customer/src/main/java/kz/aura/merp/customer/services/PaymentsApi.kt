package kz.aura.merp.customer.services

import kz.aura.merp.customer.models.PaymentSchedule
import kz.aura.merp.customer.models.ResponseHelper
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface PaymentsApi {
    @GET("/payment-schedule/list/{customerId}")
    fun getAllPayments(@Path("customerId") customerId: Long): Call<ResponseHelper<ArrayList<PaymentSchedule>>>

    @GET("/payment-schedule/awkeyBukrs")
    fun getPaymentsScheduleByAwkeyBukrs(
        @Query("awkey") awkey: Long,
        @Query("bukrs") bukrs: String
    ): Call<ResponseHelper<ArrayList<PaymentSchedule>>>
}