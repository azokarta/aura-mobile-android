package kz.aura.merp.customer.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kz.aura.merp.customer.data.model.CalendarView
import com.example.aura.R
import kotlinx.android.synthetic.main.payment_calendar_month_row.view.*

class CalendarMonthsAdapter(private val arrears: ArrayList<CalendarView>) : RecyclerView.Adapter<CalendarMonthsViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CalendarMonthsViewHolder {
        val inflater = LayoutInflater.from(parent.context).inflate(
            R.layout.payment_calendar_month_row,
            parent,
            false
        )
        return CalendarMonthsViewHolder(inflater)
    }

    override fun onBindViewHolder(holder: CalendarMonthsViewHolder, position: Int) {
        val arrear = arrears[position]
        holder.view.calendar_month.text = arrear.calendarPaymentDate
        holder.view.calendar_arrear.text = arrear.calendarPaymentDue.toString()+arrear.waers
    }

    override fun getItemCount(): Int = arrears.size

}

class CalendarMonthsViewHolder(val view: View) : RecyclerView.ViewHolder(view) {

}