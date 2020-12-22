package kz.aura.merp.customer.presenters

import android.content.Context
import kz.aura.merp.customer.models.PaymentSchedule
import kz.aura.merp.customer.models.ResponseHelper
import kz.aura.merp.customer.services.PaymentsApi
import kz.aura.merp.customer.services.ServiceBuilder
import kz.aura.merp.customer.utils.Helpers.exceptionHandler
import kz.aura.merp.customer.views.IPaymentsView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

interface IPaymentsPresenter {
    fun getAll(customerId: Long)
    fun getPaymentsSchedule(awkey: Long, bukrs: String)
}

class PaymentsPresenter(val iPaymentsView: IPaymentsView, val context: Context): IPaymentsPresenter {
    private val apiService = ServiceBuilder.buildService(PaymentsApi::class.java)

    override fun getAll(customerId: Long) {
        val callPayments = apiService.getAllPayments(customerId)

        iPaymentsView.showProgressBar()

        callPayments.enqueue(object : Callback<ResponseHelper<ArrayList<PaymentSchedule>>> {
            override fun onFailure(call: Call<ResponseHelper<ArrayList<PaymentSchedule>>>, t: Throwable) {
                exceptionHandler(t, context)
                t.message?.let { iPaymentsView.onError(it) }
            }

            override fun onResponse(call: Call<ResponseHelper<ArrayList<PaymentSchedule>>>, response: Response<ResponseHelper<ArrayList<PaymentSchedule>>>) {
                if (response.isSuccessful && response.body()!!.success) {
                    iPaymentsView.hideProgressBar()
                    iPaymentsView.onSuccessPayments(response.body()!!.data)
                } else {
                    iPaymentsView.hideProgressBar()
                    exceptionHandler(response.errorBody()!!, context)
                    iPaymentsView.onError(response.errorBody()!!)
                }
            }
        })
    }

    override fun getPaymentsSchedule(awkey: Long, bukrs: String) {
        val callPaymentsSchedule = apiService.getPaymentsScheduleByAwkeyBukrs(awkey, bukrs)

        iPaymentsView.showProgressBar()

        callPaymentsSchedule.enqueue(object : Callback<ResponseHelper<ArrayList<PaymentSchedule>>> {
            override fun onFailure(call: Call<ResponseHelper<ArrayList<PaymentSchedule>>>, t: Throwable) {
                exceptionHandler(t, context)
                iPaymentsView.onError(t.message!!)
            }

            override fun onResponse(call: Call<ResponseHelper<ArrayList<PaymentSchedule>>>, response: Response<ResponseHelper<ArrayList<PaymentSchedule>>>) {
                if (response.isSuccessful && response.body()!!.success) {
                    iPaymentsView.hideProgressBar()
                    iPaymentsView.onSuccessPaymentsSchedule(response.body()!!.data)
                } else {
                    iPaymentsView.hideProgressBar()
                    exceptionHandler(response.errorBody()!!, context)
                    iPaymentsView.onError(response.errorBody()!!)
                }
            }
        })
    }

}