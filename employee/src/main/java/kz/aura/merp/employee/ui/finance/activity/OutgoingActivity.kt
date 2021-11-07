package kz.aura.merp.employee.ui.finance.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.activity.viewModels
import androidx.appcompat.widget.Toolbar
import dagger.hilt.android.AndroidEntryPoint
import io.nlopez.smartlocation.SmartLocation
import kz.aura.merp.employee.R
import kz.aura.merp.employee.base.BaseActivity
import kz.aura.merp.employee.base.NetworkResult
import kz.aura.merp.employee.databinding.ActivityOutgoingBinding
import kz.aura.merp.employee.model.AssignCall
import kz.aura.merp.employee.util.*
import kz.aura.merp.employee.view.PermissionsListener
import kz.aura.merp.employee.viewmodel.finance.OutgoingViewModel
import org.joda.time.DateTime
import org.joda.time.Duration
import org.joda.time.format.DateTimeFormat
import org.joda.time.format.DateTimeFormatter
import ru.tinkoff.decoro.watchers.MaskFormatWatcher

@AndroidEntryPoint
class OutgoingActivity : BaseActivity(), PermissionsListener {

    private lateinit var binding: ActivityOutgoingBinding

    private lateinit var progressDialog: ProgressDialog
    private val outgoingViewModel: OutgoingViewModel by viewModels()
    private lateinit var phoneNumber: String
    private var contractId: Long = 0L
    private var callStatusId: Long = 0L
    private var duration: Duration? = null
    private var startedTime: DateTime? = null
    private val requestCode = 1000
    private var country: Country = Country.KZ
    private lateinit var permissions: Permissions
    private var formatWatcher: MaskFormatWatcher? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOutgoingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = getString(R.string.add_outgoing_call)

        contractId = intent.getLongExtra("contractId", 0L)
        phoneNumber = intent.getStringExtra("phoneNumber")!!

        permissions = Permissions(this, this, this)
        permissions.setListener(this)

        progressDialog = ProgressDialog(this)

        binding.save.setOnClickListener { save() }

        binding.callStatusText.setOnItemClickListener { _, _, i, _ ->
            callStatusId = outgoingViewModel.callStatusesResponse.value!!.data!!.data[i].id
        }

        setupObservers()

        outgoingViewModel.fetchCallStatuses()

        setPhoneNumberFormatter(phoneNumber)

        startedTime = DateTime.now()
        dialPhoneNumber(phoneNumber)
    }

    private fun setPhoneNumberFormatter(phoneNumber: String) {
        val country = outgoingViewModel.preferences.getCountryCode()
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

    private fun setupObservers() {
        outgoingViewModel.callStatusesResponse.observe(this, { res ->
            when (res) {
                is NetworkResult.Success -> {
                    progressDialog.hideLoading()
                    val callStatuses = res.data?.data
                    if (!callStatuses.isNullOrEmpty()) {
                        val adapter = ArrayAdapter(
                            this,
                            R.layout.list_item,
                            callStatuses.map { it.name })
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
        outgoingViewModel.assignOutgoingCallResponse.observe(this, { res ->
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
    }

    private fun validation(): Boolean {
        val typedPhoneNumber = binding.phoneNumberText.text.toString()
        var success = true

        if (phoneNumber.isBlank() || typedPhoneNumber.length != country.format.length) {
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
        val typedPhoneNumber = formatWatcher?.mask?.toUnformattedString()
        val description = binding.descriptionText.text.toString()
        val dtf: DateTimeFormatter = DateTimeFormat.forPattern("HH:mm")
        val currentDate: String = dtf.print(DateTime.now())

        if (validation()) {
            if (!permissions.isLocationServicesEnabled()) return

            progressDialog.showLoading()
            SmartLocation.with(this).location().oneFix()
                .start {
                    val assign = AssignCall(
                        countryCode = country.name,
                        phoneNumber = typedPhoneNumber,
                        callStatusId = callStatusId,
                        callTime = currentDate,
                        duration = duration!!.toString(),
                        description = description,
                        longitude = it.longitude,
                        latitude = it.latitude
                    )
                    outgoingViewModel.assignOutgoingCall(assign, contractId)
                }
        }
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