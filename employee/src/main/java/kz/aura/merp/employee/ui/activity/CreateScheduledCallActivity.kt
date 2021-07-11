package kz.aura.merp.employee.ui.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.activity.viewModels
import androidx.appcompat.widget.Toolbar
import dagger.hilt.android.AndroidEntryPoint
import io.nlopez.smartlocation.SmartLocation
import kz.aura.merp.employee.R
import kz.aura.merp.employee.databinding.ActivityCreateScheduledCallBinding
import kz.aura.merp.employee.model.AssignScheduledCallCommand
import kz.aura.merp.employee.ui.dialog.DatePickerFragment
import kz.aura.merp.employee.ui.dialog.TimePickerFragment
import kz.aura.merp.employee.util.*
import kz.aura.merp.employee.view.PermissionsListener
import kz.aura.merp.employee.viewmodel.FinanceViewModel

@AndroidEntryPoint
class CreateScheduledCallActivity : BaseActivity(), TimePickerFragment.TimePickerListener, DatePickerFragment.DatePickerListener, PermissionsListener {

    private lateinit var binding: ActivityCreateScheduledCallBinding

    private lateinit var progressDialog: ProgressDialog
    private val financeViewModel: FinanceViewModel by viewModels()
    private var countryCode: CountryCode = CountryCode.KZ
    private var selectedHour: Int? = null
    private var selectedMinute: Int? = null
    private var scheduledDate: String? = null
    private var contractId: Long? = null
    private lateinit var permissions: Permissions

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateScheduledCallBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Toolbar
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = getString(R.string.schedule_call)

        contractId = intent.getLongExtra("contractId", 0L)

        permissions = Permissions(this, this, this)
        permissions.setListener(this)

        // Initialize Loading Dialog
        progressDialog = ProgressDialog(this)

        setupObservers()

        binding.scheduleDateText.setOnClickListener { showDatePicker() }
        binding.scheduleTimeText.setOnClickListener { showTimePicker() }
        binding.save.setOnClickListener { save() }
    }

    private fun showTimePicker() {
        val timePicker = TimePickerFragment(this, selectedHour, selectedMinute)
        timePicker.show(supportFragmentManager)
    }

    private fun showDatePicker() {
        val datePicker = DatePickerFragment(this, this, date = convertStrToDateMillis(scheduledDate))
        datePicker.show(supportFragmentManager)
    }

    override fun selectedDate(date: Long, header: String) {
        scheduledDate = convertDateMillisToStr(date)
        binding.scheduleDateText.setText(header)
    }

    override fun selectedTime(hour: Int, minute: Int) {
       selectedHour = hour
       selectedMinute = minute
       val time = "$hour:$minute"
       binding.scheduleTimeText.setText(time)
    }

    private fun setupObservers() {
        financeViewModel.assignScheduledCallResponse.observe(this, { res ->
            when (res) {
                is NetworkResult.Success -> {
                    progressDialog.hideLoading()
                    val intent = Intent();
                    setResult(RESULT_OK, intent);
                    finish();
                }
                is NetworkResult.Loading -> {}
                is NetworkResult.Error -> {
                    progressDialog.hideLoading()
                    showException(res.message, this)
                }
            }
        })
        financeViewModel.countryCode.observe(this, { countryCode ->
            this.countryCode = countryCode
            binding.phoneNumberText.mask = countryCode.format
        })
    }

    private fun save() {
        if (validation()) {
            if (!permissions.isLocationServicesEnabled()) return

            val description = binding.descriptionText.text.toString()
            val phoneNumber = binding.phoneNumberText.text.toString()
            val scheduledDateTime = collectDateTimeInsideStr(scheduledDate!!, selectedHour!!, selectedMinute!!)

            progressDialog.showLoading()
            SmartLocation.with(this).location().oneFix()
                .start {
                    financeViewModel.assignScheduledCall(
                        contractId!!,
                        AssignScheduledCallCommand(
                            phoneNumber,
                            countryCode.name,
                            it.longitude,
                            it.latitude,
                            scheduledDateTime,
                            description
                        )
                    )
                }
        }
    }

    private fun isLocationServiceEnabled(): Boolean =
        SmartLocation.with(this).location().state().locationServicesEnabled()

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

        if (scheduledDate == null) {
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

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    override fun sendResultOfRequestLocation(granted: Boolean) {
        if (granted) {
            permissions.enableLocation()
        } else {
            showException(getString(R.string.have_not_allowed_access_to_the_location), this)
        }
    }

    override fun sendResultOfEnableLocation(granted: Boolean) {
        if (granted) {
            save()
        }
    }
}