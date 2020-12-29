package kz.aura.merp.customer.service

import kz.aura.merp.customer.data.model.ResponseHelper
import kz.aura.merp.customer.data.model.Service
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface ServicesApi {
    @GET("/service/customer/{customerId}")
    fun getAllServices(@Path("customerId") customerId: Long): Call<ResponseHelper<ArrayList<Service>>>

    @GET("/service/{id}")
    fun getService(@Path("id") id: Long): Call<ResponseHelper<Service>>
}