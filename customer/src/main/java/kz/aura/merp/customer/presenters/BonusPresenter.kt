package kz.aura.merp.customer.presenters

import android.content.Context
import kz.aura.merp.customer.models.Bonus
import kz.aura.merp.customer.models.ResponseHelper
import kz.aura.merp.customer.services.BonusApi
import kz.aura.merp.customer.services.ServiceBuilder
import kz.aura.merp.customer.utils.Helpers.exceptionHandler
import kz.aura.merp.customer.views.IBonusView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

interface IBonusPresenter {
    fun getBonuses(customerId: Long)
}

class BonusPresenter(val iBonusView: IBonusView, val context: Context): IBonusPresenter {

    override fun getBonuses(customerId: Long) {
        val apiService = ServiceBuilder.buildService(BonusApi::class.java)
        val callBonuses = apiService.getBonuses(customerId)

        callBonuses.enqueue(object : Callback<ResponseHelper<ArrayList<Bonus>>> {
            override fun onFailure(call: Call<ResponseHelper<ArrayList<Bonus>>>, t: Throwable) {
                exceptionHandler(t, context)
                iBonusView.onError(t.message!!)
            }

            override fun onResponse(call: Call<ResponseHelper<ArrayList<Bonus>>>, response: Response<ResponseHelper<ArrayList<Bonus>>>) {
                if (response.isSuccessful && response.body()!!.success) {
                    iBonusView.onSuccess(response.body()!!.data)
                } else {
                    exceptionHandler(response.errorBody()!!, context)
                    iBonusView.onError(response.errorBody()!!)
                }
            }
        })
    }
}