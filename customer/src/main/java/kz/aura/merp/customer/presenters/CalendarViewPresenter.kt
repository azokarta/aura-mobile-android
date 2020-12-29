package kz.aura.merp.customer.presenters

import android.content.Context
import kz.aura.merp.customer.data.model.CalendarView
import kz.aura.merp.customer.data.model.ResponseHelper
import kz.aura.merp.customer.service.CalendarViewApi
import kz.aura.merp.customer.service.ServiceBuilder
import kz.aura.merp.customer.util.Helpers
import kz.aura.merp.customer.views.ICalendarViView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

interface ICalendarViewPresenter {
    fun getArrears(customerId: Long)
}

class CalendarViewPresenter(val iCalendarViView: ICalendarViView, val context: Context): ICalendarViewPresenter {

    override fun getArrears(customerId: Long) {
        val apiService = ServiceBuilder.buildService(CalendarViewApi::class.java, context)
        val callArrears = apiService.getArrears(customerId)

        callArrears.enqueue(object : Callback<ResponseHelper<ArrayList<CalendarView>>> {
            override fun onFailure(call: Call<ResponseHelper<ArrayList<CalendarView>>>, t: Throwable) {
                Helpers.exceptionHandler(t, context)
                iCalendarViView.onError(t.message!!)
            }

            override fun onResponse(call: Call<ResponseHelper<ArrayList<CalendarView>>>, response: Response<ResponseHelper<ArrayList<CalendarView>>>) {
                if (response.isSuccessful && response.body()!!.success) {
                    iCalendarViView.onSuccess(response.body()!!.data)
                } else {
                    Helpers.exceptionHandler(response.errorBody()!!, context)
                    iCalendarViView.onError(response.errorBody()!!)
                }
            }
        })
    }
}