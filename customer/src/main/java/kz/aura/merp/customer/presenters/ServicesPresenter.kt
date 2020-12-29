package kz.aura.merp.customer.presenters

import android.content.Context
import kz.aura.merp.customer.data.model.ResponseHelper
import kz.aura.merp.customer.data.model.Service
import kz.aura.merp.customer.service.ServiceBuilder
import kz.aura.merp.customer.service.ServicesApi
import kz.aura.merp.customer.util.Helpers.exceptionHandler
import kz.aura.merp.customer.views.IServicesView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

interface IServicesPresenter {
    fun getAll(customerId: Long)
    fun getService(serviceId: Long)
}

class ServicesPresenter(val iServicesView: IServicesView, val context: Context): IServicesPresenter {

    private val apiService = ServiceBuilder.buildService(ServicesApi::class.java, context)

    override fun getAll(customerId: Long) {
        val callServices = apiService.getAllServices(customerId)

        callServices.enqueue(object : Callback<ResponseHelper<ArrayList<Service>>> {
            override fun onFailure(call: Call<ResponseHelper<ArrayList<Service>>>, t: Throwable) {
                exceptionHandler(t, context)
                iServicesView.onError(t.message!!)
            }

            override fun onResponse(call: Call<ResponseHelper<ArrayList<Service>>>, response: Response<ResponseHelper<ArrayList<Service>>>) {
                if (response.isSuccessful && response.body()!!.success) {
                    iServicesView.onSuccessServices(response.body()!!.data)
                } else {
                    exceptionHandler(response.errorBody()!!, context)
                    iServicesView.onError(response.errorBody()!!)
                }
            }
        })
    }

    override fun getService(serviceId: Long) {
        val callService = apiService.getService(serviceId)

        callService.enqueue(object : Callback<ResponseHelper<Service>> {
            override fun onFailure(call: Call<ResponseHelper<Service>>, t: Throwable) {
                exceptionHandler(t, context)
                iServicesView.onError(t.message!!)
            }

            override fun onResponse(call: Call<ResponseHelper<Service>>, response: Response<ResponseHelper<Service>>) {
                if (response.isSuccessful && response.body()!!.success) {
                    iServicesView.onSuccessService(response.body()!!.data)
                } else {
                    exceptionHandler(response.errorBody()!!, context)
                    iServicesView.onError(response.errorBody()!!)
                }
            }
        })
    }

}