package kz.aura.merp.customer.presenters

import android.content.Context
import kz.aura.merp.customer.models.ResponseHelper
import kz.aura.merp.customer.services.HomeApi
import kz.aura.merp.customer.services.ServiceBuilder
import kz.aura.merp.customer.utils.Helpers
import kz.aura.merp.customer.views.IHomeView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

interface IHomePresenter {
    fun getUnreadMessagesCount(customerId: Long)
}

class HomePresenter (val iHomeView: IHomeView, val context: Context) : IHomePresenter {
    val apiService = ServiceBuilder.buildService(HomeApi::class.java)

    override fun getUnreadMessagesCount(customerId: Long) {
        val callUnreadMessagesCount = apiService.getUnreadMessagesCount(customerId)
        callUnreadMessagesCount.enqueue(object : Callback<ResponseHelper<Int>> {
            override fun onResponse(
                call: Call<ResponseHelper<Int>>,
                response: Response<ResponseHelper<Int>>
            ) {
                if (response.isSuccessful && response.body()!!.success) {
                    iHomeView.onSuccessUnreadMessagesCount(response.body()!!.data)
                } else {
                    Helpers.exceptionHandler(response.errorBody()!!, context)
                    iHomeView.onError(response.errorBody()!!)
                }
            }

            override fun onFailure(call: Call<ResponseHelper<Int>>, t: Throwable) {
                Helpers.exceptionHandler(t, context)
                t.message?.let { iHomeView.onError(it) }
            }

        })
    }
}