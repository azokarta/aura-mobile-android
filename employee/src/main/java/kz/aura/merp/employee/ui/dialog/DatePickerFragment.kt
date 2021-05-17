package kz.aura.merp.employee.ui.dialog

import android.content.Context
import androidx.fragment.app.FragmentManager
import com.google.android.material.datepicker.MaterialDatePicker
import kz.aura.merp.employee.R

class DatePickerFragment(
    val context: Context,
    val datePickerListener: DatePickerListener? = null,
    val title: String? = null
) {

    interface DatePickerListener {
        fun selectedDate()
    }

    private var _picker: MaterialDatePicker<Long>? = null
    private val picker get() = _picker!!

    init {
        _picker = MaterialDatePicker.Builder.datePicker()
                    .setTitleText(title ?: context.getString(R.string.select_date))
                    .build()

        picker.addOnPositiveButtonClickListener {

        }
        picker.addOnCancelListener {
            // Respond to cancel button click.
        }
    }

    fun show(fragmentManager: FragmentManager) {
        picker.show(fragmentManager, "DatePicker")
    }
}