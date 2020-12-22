package kz.aura.merp.customer.services

import kz.aura.merp.customer.models.Bonus
import kz.aura.merp.customer.models.ResponseHelper
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface BonusApi {
    @GET("/ma-customer-bonus/{customerId}")
    fun getBonuses(@Path("customerId") customerId: Long): Call<ResponseHelper<ArrayList<Bonus>>>
}