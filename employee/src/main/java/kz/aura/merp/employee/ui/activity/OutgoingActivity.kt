package kz.aura.merp.employee.ui.activity

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.activity.viewModels
import androidx.appcompat.widget.Toolbar
import dagger.hilt.android.AndroidEntryPoint
import io.nlopez.smartlocation.SmartLocation
import kz.aura.merp.employee.R
import kz.aura.merp.employee.databinding.ActivityOutgoingBinding
import kz.aura.merp.employee.model.AssignCall
import kz.aura.merp.employee.util.*
import kz.aura.merp.employee.view.PermissionsListener
import kz.aura.merp.employee.viewmodel.FinanceViewModel
import org.joda.time.DateTime
import org.joda.time.Duration
import org.joda.time.format.DateTimeFormat
import org.joda.time.format.DateTimeFormatter

@AndroidEntryPoint
class OutgoingActivity : AppCompatActivity(), PermissionsListener {

    private lateinit var binding: ActivityOutgoingBinding

    private lateinit var progressDialog: ProgressDialog
    private val financeViewModel: FinanceViewModel by viewModels()
    private lateinit var phoneNumber: String
    private var contractId: Long = 0L
    private var callStatusId: Long = 0L
    private var duration: Duration? = null
    private var startedTime: DateTime? = null
    private val requestCode = 1000
    private var countryCode: CountryCode = CountryCode.KZ
    private lateinit var permissions: Permissions

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOutgoingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        contractId = intent.getLongExtra("contractId", 0L)
        phoneNumber = intent.getStringExtra("phoneNumber")!!

        // Toolbar
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = getString(R.string.add_outgoing_call)

        // Turn off screenshot
        window.setFlags(
            WindowManager.LayoutParams.FLAG_SECURE,
            WindowManager.LayoutParams.FLAG_SECURE
        )

        permissions = Permissions(this, this, this)
        permissions.setListener(this)

        // Initialize Loading Dialog
        progressDialog = ProgressDialog(this)

        binding.save.setOnClickListener { save() }

        binding.callStatusText.setOnItemClickListener { _, _, i, _ ->
            callStatusId = financeViewModel.callStatusesResponse.value!!.data!![i].id
        }

        setupObservers()

        financeViewModel.fetchCallStatuses()
        financeViewModel.getCountryCode()


        startedTime = DateTime.now()
        dialPhoneNumber(phoneNumber)
    }

    private fun setupObservers() {
        financeViewModel.callStatusesResponse.observe(this, { res ->
            when (res) {
                is NetworkResult.Success -> {
                    progressDialog.hideLoading()
                    if (!res.data.isNullOrEmpty()) {
                        val adapter = ArrayAdapter(
                            this,
                            R.layout.list_item,
                            res.data.map { it.name })
                        (binding.callStatusField.editText as? AutoCompleteTextView)?.setAdapter(
                            adapter
                        )
                    }
                }
                is NetworkResult.Loading -> {
                    progressDialog.showLoading()
                }
                is NetworkResult.Error -> {
                    progressDialog.hideLoading()
                    showException(res.message, this)
                }
            }
        })
        financeViewModel.assignCallResponse.observe(this, { res ->
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
            val phoneWithoutCountryCode = removeCountryCodeFromPhone(phoneNumber)
            binding.phoneNumberText.setText(phoneWithoutCountryCode)
        })
    }

    private fun validation(): Boolean {
        val typedPhoneNumber = binding.phoneNumberText.text.toString()
        var success = true

        if (phoneNumber.isBlank() || typedPhoneNumber.length != countryCode.format.length) {
            success = false
            binding.phoneNumberField.isErrorEnabled = true
            binding.phoneNumberField.error = getString(R.string.enter_valid_phone_number)
        } else binding.phoneNumberField.isErrorEnabled = false

        if (callStatusId == 0L) {
            success = false
            binding.callStatusField.isErrorEnabled = true
            binding.callStatusField.error = getString(R.string.error)
        } else binding.callStatusField.isErrorEnabled = false

        return success
    }

    private fun save() {
        hideKeyboard(this)
        val typedPhoneNumber = binding.phoneNumberText.text.toString()
        val description = binding.descriptionText.text.toString()
        val dtf: DateTimeFormatter = DateTimeFormat.forPattern("HH:mm")
        val currentDate: String = dtf.print(DateTime.now())

        if (validation()) {
            if (!permissions.isLocationServicesEnabled()) return

            progressDialog.showLoading()
            SmartLocation.with(this).location().oneFix()
                .start {
                    val assign = AssignCall(
                        countryCode = countryCode.name,
                        phoneNumber = typedPhoneNumber,
                        callStatusId = callStatusId,
                        callTime = currentDate,
                        duration = duration!!.toString(),
                        description = description,
                        longitude = it.longitude,
                        latitude = it.latitude
                    )
                    financeViewModel.assignOutgoingCall(assign, contractId)
                }
        }
    }

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

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    private fun dialPhoneNumber(phoneNumber: String?) {
        val intent = Intent(Intent.ACTION_DIAL).apply {
            data = Uri.parse("tel:$phoneNumber")
        }
        startActivityForResult(intent, requestCode)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == this.requestCode) {
            val endTime = DateTime.now()
            val callingMinutes = endTime!!.minusMinutes(startedTime!!.minuteOfHour).minuteOfHour
            duration = if (callingMinutes < 10) {
                Duration(startedTime, startedTime!!.plusMinutes(10))
            } else {
                Duration(startedTime, endTime)
            }
        }
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