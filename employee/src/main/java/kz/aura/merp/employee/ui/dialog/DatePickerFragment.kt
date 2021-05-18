package kz.aura.merp.employee.ui.dialog

import android.content.Context
import androidx.fragment.app.FragmentManager
import com.google.android.material.datepicker.MaterialDatePicker
import kz.aura.merp.employee.R
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import org.joda.time.format.DateTimeFormatter
import java.util.*

class DatePickerFragment(
    val context: Context,
    private val datePickerListener: DatePickerListener? = null,
    val title: String? = null
) {

    interface DatePickerListener {
        fun selectedDate(date: String, header: String) {}
        fun onCancelDate() {}
    }

    private var _picker: MaterialDatePicker<Long>? = null
    private val picker get() = _picker!!

    init {
        _picker = MaterialDatePicker.Builder.datePicker()
                    .setTitleText(title ?: context.getString(R.string.select_date))
                    .build()

        val dtf: DateTimeFormatter = DateTimeFormat.forPattern("dd.MM.yyyy")
        val currentDate = DateTime.now()

        picker.addOnPositiveButtonClickListener {
            val selectedDate = dtf.print(currentDate.withMillis(it))
            datePickerListener?.selectedDate(selectedDate, picker.headerText)
        }
        picker.addOnCancelListener {
            datePickerListener?.onCancelDate()
        }
    }

    fun show(fragmentManager: FragmentManager) {
        picker.show(fragmentManager, "DatePicker")
    }
}