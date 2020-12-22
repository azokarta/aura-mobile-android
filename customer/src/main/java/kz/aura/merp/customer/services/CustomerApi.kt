package kz.aura.merp.customer.services

import kz.aura.merp.customer.models.Customer
import kz.aura.merp.customer.models.ResponseHelper
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface CustomerApi {
    @GET("/customer/{id}")
    fun getCustomerData(@Path("id") id: Long): Call<ResponseHelper<Customer>>
}