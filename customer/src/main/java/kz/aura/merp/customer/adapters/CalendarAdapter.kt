package kz.aura.merp.customer.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kz.aura.merp.customer.models.CalendarView
import kz.aura.merp.customer.models.CalendarYearExpandable
import com.example.aura.R
import kotlinx.android.synthetic.main.payment_calendar_row.view.*
import kotlin.collections.ArrayList

class CalendarAdapter(private val arrears: ArrayList<CalendarView>, private val years: ArrayList<CalendarYearExpandable>) : RecyclerView.Adapter<CalendarViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CalendarViewHolder {
        val inflater = LayoutInflater.from(parent.context).inflate(
            R.layout.payment_calendar_row,
            parent,
            false
        )
        return CalendarViewHolder(inflater)
    }

    override fun onBindViewHolder(holder: CalendarViewHolder, position: Int) {
        val calendarYearExpandable = years[position]
        holder.view.calendar_row_date.text = calendarYearExpandable.year
        holder.calendarYearExpandable = calendarYearExpandable
        holder.arrears = arrears
    }

    override fun getItemCount(): Int = years.size
}

class CalendarViewHolder(val view: View, var calendarYearExpandable: CalendarYearExpandable? = null, var arrears: ArrayList<CalendarView>? = null) : RecyclerView.ViewHolder(view) {
    private val foundArrearsInYear = arrayListOf<CalendarView>()
    private val calendarMonthsAdapter = CalendarMonthsAdapter(foundArrearsInYear)

    init {
        view.payment_calendar_months_recycler_view.layoutManager = LinearLayoutManager(view.context)
        view.payment_calendar_months_recycler_view.adapter = calendarMonthsAdapter

        view.setOnClickListener {
            calendarYearExpandable!!.expandable = !calendarYearExpandable!!.expandable
            if (!calendarYearExpandable!!.expandable) {
                view.payment_calendar_show_icon.setImageResource(R.drawable.ic_baseline_keyboard_arrow_down_24)
                view.payment_calendar_months_recycler_view.visibility = View.GONE
            } else {
                view.payment_calendar_show_icon.setImageResource(R.drawable.ic_baseline_keyboard_arrow_up_24)
                view.payment_calendar_months_recycler_view.visibility = View.VISIBLE
                foundArrearsInYear.addAll(arrears!!.filter { findByMonth(it, calendarYearExpandable!!.year) })
                calendarMonthsAdapter.notifyDataSetChanged()
            }
        }
    }

    private fun findByMonth(calendarView: CalendarView, date: String): Boolean {
        val divided = calendarView.calendarPaymentDate.split("-")
        val year = divided[0]
        return year == date
    }
}