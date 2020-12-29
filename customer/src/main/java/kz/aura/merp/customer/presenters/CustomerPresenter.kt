package kz.aura.merp.customer.presenters

import android.content.Context
import kz.aura.merp.customer.data.model.Customer
import kz.aura.merp.customer.data.model.ResponseHelper
import kz.aura.merp.customer.service.CustomerApi
import kz.aura.merp.customer.service.ServiceBuilder
import kz.aura.merp.customer.util.Helpers.exceptionHandler
import kz.aura.merp.customer.views.ICustomerView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

interface ICustomerPresenter {
    fun getData(customerId: Long)
}

class CustomerPresenter(val iCustomerView: ICustomerView, val context: Context): ICustomerPresenter {

    override fun getData(customerId: Long) {
        val apiService = ServiceBuilder.buildService(CustomerApi::class.java, context)
        val callCustomer = apiService.getCustomerData(customerId)

        callCustomer.enqueue(object : Callback<ResponseHelper<Customer>> {
            override fun onFailure(call: Call<ResponseHelper<Customer>>, t: Throwable) {
                exceptionHandler(t, context)
                iCustomerView.onError(t.message!!)
            }

            override fun onResponse(call: Call<ResponseHelper<Customer>>, response: Response<ResponseHelper<Customer>>) {
                if (response.isSuccessful && response.body()!!.success) {
                    println(response.body())
                    iCustomerView.onSuccess(response.body()!!.data)
                } else {
                    exceptionHandler(response.errorBody()!!, context)
                    iCustomerView.onError(response.errorBody()!!)
                }
            }
        })
    }
}