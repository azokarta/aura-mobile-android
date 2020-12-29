package kz.aura.merp.customer.views

import kz.aura.merp.customer.data.model.CalendarView

interface ICalendarViView : BaseView {
    fun onSuccess(arrears: ArrayList<CalendarView>)
}