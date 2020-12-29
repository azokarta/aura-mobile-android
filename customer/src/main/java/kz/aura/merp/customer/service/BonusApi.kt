package kz.aura.merp.customer.service

import kz.aura.merp.customer.data.model.Bonus
import kz.aura.merp.customer.data.model.ResponseHelper
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface BonusApi {
    @GET("/ma-customer-bonus/{customerId}")
    fun getBonuses(@Path("customerId") customerId: Long): Call<ResponseHelper<ArrayList<Bonus>>>
}