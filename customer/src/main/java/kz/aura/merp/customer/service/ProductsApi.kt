package kz.aura.merp.customer.service

import kz.aura.merp.customer.data.model.Product
import kz.aura.merp.customer.data.model.ResponseHelper
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface ProductsApi {
    @GET("/contract/customer/{customerId}")
    fun getAllContracts(@Path("customerId") customerId: Long): Call<ResponseHelper<ArrayList<Product>>>

    @GET("/contract/{id}")
    fun getProduct(@Path("id") id: Long): Call<ResponseHelper<Product>>
}