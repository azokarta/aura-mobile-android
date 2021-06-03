package kz.aura.merp.employee.ui.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import dagger.hilt.android.AndroidEntryPoint
import io.nlopez.smartlocation.SmartLocation
import kz.aura.merp.employee.R
import kz.aura.merp.employee.databinding.ActivityIncomingBinding
import kz.aura.merp.employee.model.AssignCall
import kz.aura.merp.employee.ui.dialog.TimePickerFragment
import kz.aura.merp.employee.util.*
import kz.aura.merp.employee.viewmodel.FinanceViewModel
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import org.joda.time.format.DateTimeFormatter

@AndroidEntryPoint
class IncomingActivity : AppCompatActivity(), TimePickerFragment.TimePickerListener {

    private lateinit var binding: ActivityIncomingBinding

    private lateinit var progressDialog: ProgressDialog
    private val mFinanceViewModel: FinanceViewModel by viewModels()
    private lateinit var phoneNumber: String
    private var contractId: Long = 0L
    private var selectedHour: Int? = null
    private var selectedMinute: Int? = null
    private var countryCode: CountryCode = CountryCode.KZ

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityIncomingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        contractId = intent.getLongExtra("contractId", 0L)
        phoneNumber = intent.getStringExtra("phoneNumber")!!

        // Toolbar
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = getString(R.string.add_incoming_call)

        // Turn off screenshot
        window.setFlags(
            WindowManager.LayoutParams.FLAG_SECURE,
            WindowManager.LayoutParams.FLAG_SECURE
        )

        // Initialize Loading Dialog
        progressDialog = ProgressDialog(this)

        setupObservers()

        mFinanceViewModel.getCountryCode()

        binding.save.setOnClickListener(::save)

        binding.scheduleTimeText.setOnClickListener {
            showTimePicker()
        }
    }

    private fun save(view: View) {
        val description = binding.descriptionText.text.toString()
        val typedPhoneNumber = binding.phoneNumberText.text.toString()

        if (validation()) {
            if (isLocationServiceEnabled()) {
                progressDialog.showLoading()
                SmartLocation.with(this).location().oneFix()
                    .start {
                        val assign = AssignCall(
                            countryCode = countryCode.name,
                            phoneNumber = typedPhoneNumber,
                            callTime = "$selectedHour:$selectedMinute",
                            description = description,
                            longitude = it.longitude,
                            latitude = it.latitude
                        )
                        mFinanceViewModel.assignIncomingCall(assign, contractId)
                    }
            } else {
                showException(getString(R.string.enable_location), this)
            }
        }
    }

    private fun validation(): Boolean {
        val typedPhoneNumber = binding.phoneNumberText.text.toString()
        var success = true

        if (typedPhoneNumber.isBlank() || typedPhoneNumber.length != countryCode.format.length) {
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

    private fun isLocationServiceEnabled(): Boolean =
        SmartLocation.with(this).location().state().locationServicesEnabled()

    private fun removeCountryCodeFromPhone(phone: String): String {
        if (countryCode == CountryCode.KZ) {
            return when (phone.first()) {
                '8' -> phone.removePrefix("8")
                '+' -> phone.removePrefix("+7")
                else -> phone
            }
        }
        return phone
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
        mFinanceViewModel.assignCallResponse.observe(this, { res ->
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
        mFinanceViewModel.countryCode.observe(this, { countryCode ->
            this.countryCode = countryCode
            binding.phoneNumberText.mask = countryCode.format
            val phoneWithoutCountryCode = removeCountryCodeFromPhone(phoneNumber)
            binding.phoneNumberText.setText(phoneWithoutCountryCode)
        })
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}