package kz.aura.merp.customer.presenters

import android.content.Context
import kz.aura.merp.customer.models.Message
import kz.aura.merp.customer.models.ResponseHelper
import kz.aura.merp.customer.services.MessageApi
import kz.aura.merp.customer.services.ServiceBuilder
import kz.aura.merp.customer.utils.Helpers.exceptionHandler
import kz.aura.merp.customer.views.IMessageView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

interface IMessagePresenter {
    fun getAll(customerId: Long)
}

class MessagePresenter(val iMessageView: IMessageView, val context: Context): IMessagePresenter {

    override fun getAll(customerId: Long) {
        val apiService = ServiceBuilder.buildService(MessageApi::class.java)
        val callMessages = apiService.getAllMessages(customerId)

        callMessages.enqueue(object : Callback<ResponseHelper<ArrayList<Message>>> {
            override fun onFailure(call: Call<ResponseHelper<ArrayList<Message>>>, t: Throwable) {
                exceptionHandler(t, context)
                iMessageView.onError(t.message!!)
            }

            override fun onResponse(call: Call<ResponseHelper<ArrayList<Message>>>, response: Response<ResponseHelper<ArrayList<Message>>>) {
                if (response.isSuccessful && response.body()!!.success) {
                    iMessageView.onSuccess(response.body()!!.data)
                } else {
                    exceptionHandler(response.errorBody()!!, context)
                    iMessageView.onError(response.errorBody()!!)
                }
            }
        })
    }
}