package kz.aura.merp.customer.service

import kz.aura.merp.customer.data.model.Feedback
import kz.aura.merp.customer.data.model.ResponseHelper
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface FeedbackApi {
    @GET("/ma-feedback/{customerId}")
    fun getFeedback(@Path("customerId") customerId: Long): Call<ResponseHelper<ArrayList<Feedback>>>
}