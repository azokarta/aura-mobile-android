package kz.aura.merp.employee.ui.finance.activity

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.widget.Toolbar
import dagger.hilt.android.AndroidEntryPoint
import io.nlopez.smartlocation.SmartLocation
import kz.aura.merp.employee.R
import kz.aura.merp.employee.base.BaseActivity
import kz.aura.merp.employee.base.NetworkResult
import kz.aura.merp.employee.databinding.ActivityIncomingBinding
import kz.aura.merp.employee.model.AssignCall
import kz.aura.merp.employee.ui.common.TimePickerFragment
import kz.aura.merp.employee.util.*
import kz.aura.merp.employee.view.PermissionsListener
import kz.aura.merp.employee.viewmodel.finance.IncomingViewModel
import ru.tinkoff.decoro.watchers.MaskFormatWatcher

@AndroidEntryPoint
class IncomingActivity : BaseActivity(), TimePickerFragment.TimePickerListener, PermissionsListener {

    private lateinit var binding: ActivityIncomingBinding

    private lateinit var progressDialog: ProgressDialog
    private val incomingViewModel: IncomingViewModel by viewModels()
    private var contractId: Long = 0L
    private var selectedHour: Int? = null
    private var selectedMinute: Int? = null
    private lateinit var permissions: Permissions
    private var formatWatcher: MaskFormatWatcher? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityIncomingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = getString(R.string.add_incoming_call)

        contractId = intent.getLongExtra("contractId", 0L)
        val phoneNumber = intent.getStringExtra("phoneNumber")!!

        permissions = Permissions(this, this, this)
        permissions.setListener(this)

        progressDialog = ProgressDialog(this)

        setupObservers()

        binding.save.setOnClickListener { save() }

        binding.scheduleTimeText.setOnClickListener {
            showTimePicker()
        }

        setPhoneNumberFormatter(phoneNumber)
    }

    private fun save() {
        hideKeyboard(this)
        val country = incomingViewModel.preferences.getCountryCode()
        val description = binding.descriptionText.text.toString()
        val typedPhoneNumber = formatWatcher?.mask?.toUnformattedString()

        if (validation()) {
            if (!permissions.isLocationServicesEnabled()) return

            progressDialog.showLoading()
            SmartLocation.with(this).location().oneFix()
                .start {
                    val assign = AssignCall(
                        countryCode = country.name,
                        phoneNumber = typedPhoneNumber,
                        callTime = "$selectedHour:$selectedMinute",
                        description = description,
                        longitude = it.longitude,
                        latitude = it.latitude
                    )
                    incomingViewModel.assignIncomingCall(assign, contractId)
                }
        }
    }

    private fun validation(): Boolean {
        val typedPhoneNumber = binding.phoneNumberText.text.toString()
        val country = incomingViewModel.preferences.getCountryCode()
        var success = true

        if (typedPhoneNumber.isBlank() || typedPhoneNumber.length != country.format.length) {
            success = false
            binding.phoneNumberField.isErrorEnabled = true
            binding.phoneNumberField.error = getString(R.string.enter_valid_phone_number)
        } else binding.phoneNumberField.isErrorEnabled = false

        if (selectedHour == null || selectedMinute == null) {
            success = false
            binding.scheduleTimeField.isErrorEnabled = true
            binding.scheduleTimeField.error = getString(R.string.error)
        } else binding.scheduleTimeField.isErrorEnabled = false

        return success
    }

    private fun setPhoneNumberFormatter(phoneNumber: String) {
        val country = incomingViewModel.preferences.getCountryCode()
        formatWatcher = phoneMaskFormatWatcher(country.format)
        formatWatcher?.installOn(binding.phoneNumberText)

        when (country) {
            Country.KZ -> {
                if (phoneNumber.first() == '8') {
                    binding.phoneNumberText.setText(phoneNumber.replace("8", country.phoneCode))
                } else binding.phoneNumberText.setText(phoneNumber)
            }
            else -> binding.phoneNumberText.setText(phoneNumber)
        }
    }

    private fun showTimePicker() {
        val timePicker = TimePickerFragment(this, selectedHour, selectedMinute)
        timePicker.show(supportFragmentManager)
    }

    override fun selectedTime(hour: Int, minute: Int) {
        selectedHour = hour
        selectedMinute = minute
        val time = "$hour:$minute"
        binding.scheduleTimeText.setText(time)
    }

    private fun setupObservers() {
        incomingViewModel.assignIncomingCallResponse.observe(this, { res ->
            when (res) {
                is NetworkResult.Success -> {
                    progressDialog.hideLoading()
                    val intent = Intent();
                    setResult(RESULT_OK, intent);
                    finish();
                }
                is NetworkResult.Loading -> progressDialog.showLoading()
                is NetworkResult.Error -> {
                    progressDialog.hideLoading()
                    showException(res.message, this)
                }
            }
        })
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