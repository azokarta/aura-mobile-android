package kz.aura.merp.customer.services

import kz.aura.merp.customer.models.Feedback
import kz.aura.merp.customer.models.ResponseHelper
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface FeedbackApi {
    @GET("/ma-feedback/{customerId}")
    fun getFeedback(@Path("customerId") customerId: Long): Call<ResponseHelper<ArrayList<Feedback>>>
}