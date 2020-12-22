package kz.aura.merp.customer.presenters

import android.content.Context
import kz.aura.merp.customer.models.Feedback
import kz.aura.merp.customer.models.ResponseHelper
import kz.aura.merp.customer.services.FeedbackApi
import kz.aura.merp.customer.services.ServiceBuilder
import kz.aura.merp.customer.utils.Helpers.exceptionHandler
import kz.aura.merp.customer.views.IFeedbackView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


interface IFeedbackPresenter {
    fun getFeedback(customerId: Long)
}

class FeedbackPresenter(val iFeedbackView: IFeedbackView, val context: Context): IFeedbackPresenter {

    override fun getFeedback(customerId: Long) {
        val apiService = ServiceBuilder.buildService(FeedbackApi::class.java)
        val callFeedback = apiService.getFeedback(customerId)

        callFeedback.enqueue(object : Callback<ResponseHelper<ArrayList<Feedback>>> {
            override fun onFailure(call: Call<ResponseHelper<ArrayList<Feedback>>>, t: Throwable) {
                exceptionHandler(t, context)
                iFeedbackView.onError(t.message!!)
            }

            override fun onResponse(
                call: Call<ResponseHelper<ArrayList<Feedback>>>,
                response: Response<ResponseHelper<ArrayList<Feedback>>>
            ) {
                if (response.isSuccessful && response.body()!!.success) {
                    iFeedbackView.onSuccess(response.body()!!.data)
                } else {
                    exceptionHandler(response.errorBody()!!, context)
                    iFeedbackView.onError(response.errorBody()!!)
                }
            }
        })
    }
}