package kz.aura.merp.customer.service

import kz.aura.merp.customer.data.model.Customer
import kz.aura.merp.customer.data.model.ResponseHelper
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface CustomerApi {
    @GET("/customer/{id}")
    fun getCustomerData(@Path("id") id: Long): Call<ResponseHelper<Customer>>
}