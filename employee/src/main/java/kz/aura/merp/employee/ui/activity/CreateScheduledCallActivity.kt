package kz.aura.merp.employee.ui.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import androidx.activity.viewModels
import androidx.appcompat.widget.Toolbar
import dagger.hilt.android.AndroidEntryPoint
import kz.aura.merp.employee.R
import kz.aura.merp.employee.databinding.ActivityCreateScheduledCallBinding
import kz.aura.merp.employee.ui.dialog.DatePickerFragment
import kz.aura.merp.employee.ui.dialog.TimePickerFragment
import kz.aura.merp.employee.util.CountryCode
import kz.aura.merp.employee.util.ProgressDialog
import kz.aura.merp.employee.util.dateTimeFormat
import kz.aura.merp.employee.viewmodel.FinanceViewModel
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import org.joda.time.format.DateTimeFormatter

@AndroidEntryPoint
class CreateScheduledCallActivity : AppCompatActivity(), TimePickerFragment.TimePickerListener, DatePickerFragment.DatePickerListener {

    private lateinit var binding: ActivityCreateScheduledCallBinding

    private lateinit var progressDialog: ProgressDialog
    private val financeViewModel: FinanceViewModel by viewModels()
    private var countryCode: CountryCode = CountryCode.KZ
    private var selectedHour: Int? = null
    private var selectedMinute: Int? = null
    private var scheduledDateInMillis: Long? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateScheduledCallBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Toolbar
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = getString(R.string.schedule_call)

        // Turn off screenshot
        window.setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE)

        // Initialize Loading Dialog
        progressDialog = ProgressDialog(this)

        setupObservers()

        binding.scheduleDateText.setOnClickListener(::showDatePicker)
        binding.scheduleTimeText.setOnClickListener(::showTimePicker)
    }

    private fun showTimePicker(view: View) {
        val timePicker = TimePickerFragment(this, selectedHour, selectedMinute)
        timePicker.show(supportFragmentManager)
    }

    private fun showDatePicker(view: View) {
        val datePicker = DatePickerFragment(this, this, date = scheduledDateInMillis)
        datePicker.show(supportFragmentManager)
    }

    override fun selectedDate(date: Long, header: String) {
        scheduledDateInMillis = date
        binding.scheduleDateText.setText(header)
    }

    override fun selectedTime(hour: Int, minute: Int) {
       selectedHour = hour
       selectedMinute = minute
       val time = "$hour:$minute"
       binding.scheduleTimeText.setText(time)
    }

    private fun setupObservers() {
        financeViewModel.countryCode.observe(this, { countryCode ->
            this.countryCode = countryCode
            binding.phoneNumberText.mask = countryCode.format
        })
    }

    private fun save() {
        if (validation()) {
            val description = binding.descriptionText.text.toString()
            val phoneNumber = binding.phoneNumberText.text.toString()

            dateTimeFormat(scheduledDateInMillis!!, selectedHour!!, selectedMinute!!)
        }
    }

    private fun validation(): Boolean {
        val phoneNumber = binding.phoneNumberText.text.toString()
        var success = true

        if (phoneNumber.length != countryCode.format.length) {
            binding.phoneNumberField.isErrorEnabled = true
            binding.phoneNumberField.error = getString(R.string.enter_valid_phone_number)
            success = false
        } else {
            binding.phoneNumberField.isErrorEnabled = false
        }

        if (scheduledDateInMillis == null) {
            binding.scheduleDateField.isErrorEnabled = true
            binding.scheduleDateField.error = getString(R.string.error)
            success = false
        } else {
            binding.scheduleDateField.isErrorEnabled = false
        }

        if (selectedHour == null || selectedMinute == null) {
            binding.scheduleTimeField.isErrorEnabled = true
            binding.scheduleTimeField.error = getString(R.string.error)
            success = false
        } else {
            binding.scheduleTimeField.isErrorEnabled = false
        }

        return success
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.save_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.save -> save()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}