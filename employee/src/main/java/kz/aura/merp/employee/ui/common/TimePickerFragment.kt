package kz.aura.merp.employee.ui.common

import androidx.fragment.app.FragmentManager
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import java.util.*

class TimePickerFragment(
    private val timePickerListener: TimePickerListener,
    private val hour: Int? = null,
    private val minute: Int? = null,
    private val title: String? = null
    ) {

    private var _picker: MaterialTimePicker? = null
    private val picker get() = _picker!!

    interface TimePickerListener {
        fun selectedTime(hour: Int, minute: Int) {}
        fun onCancelTime() {}
    }

    init {
        val c = Calendar.getInstance()
        val hour = c.get(Calendar.HOUR_OF_DAY)
        val minute = c.get(Calendar.MINUTE)

        _picker = MaterialTimePicker.Builder()
            .setTimeFormat(TimeFormat.CLOCK_12H)
            .setHour(this.hour ?: hour)
            .setMinute(this.minute ?: minute)
            .setTitleText(title)
            .build()

        picker.addOnCancelListener {
            timePickerListener.onCancelTime()
        }

        picker.addOnPositiveButtonClickListener {
            timePickerListener.selectedTime(picker.hour, picker.minute)
        }
    }

    fun show(fragmentManager: FragmentManager) {
        picker.show(fragmentManager, "TimePicker")
    }
}