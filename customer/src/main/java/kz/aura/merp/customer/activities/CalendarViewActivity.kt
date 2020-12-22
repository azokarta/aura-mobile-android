package kz.aura.merp.customer.activities

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import kz.aura.merp.customer.adapters.CalendarAdapter
import kz.aura.merp.customer.models.CalendarView
import kz.aura.merp.customer.models.CalendarYearExpandable
import kz.aura.merp.customer.presenters.CalendarViewPresenter
import kz.aura.merp.customer.presenters.ICalendarViewPresenter
import kz.aura.merp.customer.utils.Constants
import kz.aura.merp.customer.views.ICalendarViView
import com.example.aura.R
import com.r0adkll.slidr.Slidr
import com.r0adkll.slidr.model.SlidrInterface
import kotlinx.android.synthetic.main.activity_calendar_view.*
import java.util.*

class CalendarViewActivity : AppCompatActivity(), ICalendarViView {

    private lateinit var slidr: SlidrInterface
    private lateinit var calendarViewPresenter: ICalendarViewPresenter
    private val arrearList = arrayListOf<CalendarView>()
    private val years = arrayListOf<CalendarYearExpandable>()
    private val calendarAdapter = CalendarAdapter(arrearList, years)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calendar_view)

        // Off screenshot
        window.setFlags(
            WindowManager.LayoutParams.FLAG_SECURE,
            WindowManager.LayoutParams.FLAG_SECURE
        )


        slidr = Slidr.attach(this)

        calendarViewPresenter = CalendarViewPresenter(this, applicationContext)
        calendarViewPresenter.getArrears(Constants.CUSTOMER_ID)

        calendarViewRecyclerView.layoutManager = LinearLayoutManager(this)
        calendarViewRecyclerView.adapter = calendarAdapter
        calendarViewRecyclerView.isNestedScrollingEnabled = false;
        calendar_view_progress_bar.visibility = View.VISIBLE
    }

    fun goToBack(view: View) {
        onBackPressed()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if (item.itemId == android.R.id.home) {
            onBackPressed();
        }

        return true;
    }

    override fun onSuccess(arrears: ArrayList<CalendarView>) {
        calendar_view_progress_bar.visibility = View.GONE
        arrears.forEach {
            val divided = it.calendarPaymentDate.split("-")
            val year = divided[0]
            if (CalendarYearExpandable(year, false) !in years) {
                years.add(CalendarYearExpandable(year, false))
            }
        }
        arrearList.addAll(arrears)
        calendarAdapter.notifyDataSetChanged()
    }

    override fun onError(error: Any) {
        calendar_view_progress_bar.visibility = View.GONE
    }
}