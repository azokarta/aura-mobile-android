package kz.aura.merp.employee.ui.finance.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.activity.viewModels
import androidx.appcompat.widget.Toolbar
import androidx.core.view.isVisible
import com.google.android.material.textfield.TextInputLayout
import dagger.hilt.android.AndroidEntryPoint
import io.nlopez.smartlocation.SmartLocation
import kz.aura.merp.employee.R
import kz.aura.merp.employee.base.BaseActivity
import kz.aura.merp.employee.base.NetworkResult
import kz.aura.merp.employee.databinding.ActivityChangeResultBinding
import kz.aura.merp.employee.model.AssignCollectMoneyCommand
import kz.aura.merp.employee.model.AssignScheduledCallCommand
import kz.aura.merp.employee.model.ChangePlanResult
import kz.aura.merp.employee.ui.common.DatePickerFragment
import kz.aura.merp.employee.ui.common.TimePickerFragment
import kz.aura.merp.employee.util.*
import kz.aura.merp.employee.view.PermissionsListener
import kz.aura.merp.employee.viewmodel.finance.ChangeResultViewModel

@AndroidEntryPoint
class ChangeResultActivity : BaseActivity(), TimePickerFragment.TimePickerListener, DatePickerFragment.DatePickerListener, PermissionsListener {

    private lateinit var binding: ActivityChangeResultBinding

    private val changeResultViewModel: ChangeResultViewModel by viewModels()
    private lateinit var progressDialog: ProgressDialog
    private lateinit var clientPhoneNumbers: Array<String>

    private var contractId: Long? = null
    private var resultId: Long? = null
    private var paymentMethodId: Long? = null
    private var bankId: Long? = null
    private var businessProcessId: Long? = null
    private var selectedHour: Int? = null
    private var selectedMinute: Int? = null
    private var scheduledDate: String? = null
    private lateinit var permissions: Permissions

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChangeResultBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = getString(R.string.change_result)

        permissions = Permissions(this, this, this)
        permissions.setListener(this)

        contractId = intent.getLongExtra("contractId", 0L)
        clientPhoneNumbers = intent.getStringArrayExtra("clientPhoneNumbers")!!
        businessProcessId = intent.getLongExtra("businessProcessId", 0L)

        progressDialog = ProgressDialog(this)

        setObservers()

        changeResultViewModel.fetchPlanResults()

        setupAutocompleteText(clientPhoneNumbers.toList(), binding.phoneNumberField)

        binding.resultText.setOnItemClickListener { _, _, i, _ ->
            val results = changeResultViewModel.planResultsResponse.value?.data?.data
            resultId = results?.get(i)?.id
            clearChilds(Field.RESULT)
            when (resultId) {
                1L, 4L -> visibleField(Field.REASON_DESCRIPTION)
                3L -> visibleField(Field.RESCHEDULED)
                2L -> {
                    visibleField(Field.PAYMENT)
                    fetchPaymentMethods()
                }
            }
        }

        binding.paymentMethodText.setOnItemClickListener { _, _, i, _ ->
            val paymentMethods = changeResultViewModel.paymentMethodsResponse.value?.data?.data
            paymentMethodId = paymentMethods?.get(i)?.id
            clearChilds(Field.PAYMENT)
            when (paymentMethodId) {
                3L, 2L  -> {
                    visibleField(Field.BANK)
                    fetchBanks()
                }
                1L, 4L -> {
                    visibleField(Field.AMOUNT)
                }
            }
        }

        binding.bankText.setOnItemClickListener { _, _, i, _ ->
            val banks = changeResultViewModel.banksResponse.value?.data?.data
            bankId = banks?.get(i)?.id
            visibleField(Field.AMOUNT)
            clearChilds(Field.BANK)
        }

        binding.save.setOnClickListener {
            hideKeyboard(this)
            if (paymentMethodId == 1L && businessProcessId != 2L) {
                showException(getString(R.string.have_not_marked_the_status), this)
            } else {
                save()
            }
        }
        binding.scheduleTimeText.setOnClickListener(::showTimePicker)
        binding.scheduleDateText.setOnClickListener(::showDatePicker)
    }

    private fun showDatePicker(view: View) {
        val datePicker = DatePickerFragment(this, this, date = convertStrToDateMillis(scheduledDate))
        datePicker.show(supportFragmentManager)
    }

    private fun save() {
        if (validation()) {
            if (!permissions.isLocationServicesEnabled()) return

            val reasonDescription: String? = if (binding.reasonDescriptionText.text.toString().isBlank()) null else binding.reasonDescriptionText.text.toString()
            val phoneNumber: String = binding.phoneNumberText.text.toString()
            val amount: Int? = if (binding.amountText.text.toString().isBlank()) null else binding.amountText.text.toString().toInt()
            val scheduledDateTime = "$scheduledDate $selectedHour:$selectedMinute"
            val countryCallingCode = changeResultViewModel.preferences.countryCallingCode
            val countryCode = Country.values().find { it.phoneCode == countryCallingCode } ?: Country.KZ

            progressDialog.showLoading()
            SmartLocation.with(this).location().oneFix()
                .start {
                    when (resultId) {
                        3L -> {
                            changeResultViewModel.changeResult(
                                contractId,
                                ChangePlanResult(
                                    resultId,
                                    reasonDescription,
                                    it.longitude,
                                    it.latitude,
                                    null,
                                    AssignScheduledCallCommand(
                                        phoneNumber,
                                        countryCode.name,
                                        it.longitude,
                                        it.latitude,
                                        scheduledDateTime,
                                        reasonDescription
                                    )
                                )
                            )
                        }
                        2L -> {
                            changeResultViewModel.changeResult(
                                contractId,
                                ChangePlanResult(
                                    resultId,
                                    reasonDescription,
                                    it.longitude,
                                    it.latitude,
                                    AssignCollectMoneyCommand(
                                        phoneNumber,
                                        bankId,
                                        paymentMethodId,
                                        amount,
                                        countryCode.name,
                                        it.longitude,
                                        it.latitude
                                    )
                                )
                            )
                        }
                        else -> {
                            changeResultViewModel.changeResult(
                                contractId,
                                ChangePlanResult(
                                    resultId,
                                    reasonDescription,
                                    it.longitude,
                                    it.latitude
                                )
                            )
                        }
                    }
                }
        }
    }

    private fun showTimePicker(view: View) {
        val timePicker = TimePickerFragment(this, selectedHour, selectedMinute)
        timePicker.show(supportFragmentManager)
    }

    override fun selectedTime(hour: Int, minute: Int) {
        selectedHour = hour
        selectedMinute = minute
        val time = "$hour:$minute"
        binding.scheduleTimeText.setText(time)
    }

    override fun selectedDate(date: Long, header: String) {
        scheduledDate = convertDateMillisToStr(date)
        binding.scheduleDateText.setText(convertDateMillisToStr(date))
    }

    private fun validation(): Boolean {
        val phoneNumber: String = binding.phoneNumberText.text.toString()
        val amount: String = binding.amountText.text.toString()
        val reasonDescription: String = binding.reasonDescriptionText.text.toString()
        var success = true

        when (resultId) {
            2L -> {
                if (paymentMethodId == null) {
                    fieldErrorEnabled(binding.paymentMethodField, true)
                    success = false
                } else fieldErrorEnabled(binding.paymentMethodField, false)
                when (paymentMethodId) {
                    3L, 2L -> {
                        if (bankId == null) {
                            fieldErrorEnabled(binding.bankField, true)
                            success = false
                        } else fieldErrorEnabled(binding.bankField, false)

                        if (amount.isBlank()) {
                            fieldErrorEnabled(binding.amountField, true)
                            success = false
                        } else fieldErrorEnabled(binding.amountField, false)

                        if (phoneNumber.isBlank()) {
                            fieldErrorEnabled(binding.phoneNumberField, true)
                            success = false
                        } else fieldErrorEnabled(binding.phoneNumberField, false)
                    }
                    1L, 4L -> {
                        if (amount.isBlank()) {
                            fieldErrorEnabled(binding.amountField, true)
                            success = false
                        } else fieldErrorEnabled(binding.amountField, false)

                        if (phoneNumber.isBlank()) {
                            fieldErrorEnabled(binding.phoneNumberField, true)
                            success = false
                        } else fieldErrorEnabled(binding.phoneNumberField, false)
                    }
                }
            }
            3L -> {
                if (selectedHour == null || selectedMinute == null) {
                    fieldErrorEnabled(binding.scheduleTimeField, true)
                    success = false
                } else fieldErrorEnabled(binding.scheduleTimeField, false)

                if (scheduledDate == null) {
                    fieldErrorEnabled(binding.scheduleDateField, true)
                    success = false
                } else fieldErrorEnabled(binding.scheduleDateField, false)

                if (phoneNumber.isBlank()) {
                    fieldErrorEnabled(binding.phoneNumberField, true)
                    success = false
                } else fieldErrorEnabled(binding.phoneNumberField, false)
            }
            else -> {
                if (reasonDescription.isBlank()) {
                    fieldErrorEnabled(binding.reasonDescriptionField, true)
                    success = false
                } else fieldErrorEnabled(binding.reasonDescriptionField, false)
            }
        }

        if (resultId == null) {
            fieldErrorEnabled(binding.resultField, true)
            success = false
        } else fieldErrorEnabled(binding.resultField, false)

        return success
    }

    private fun fieldErrorEnabled(field: TextInputLayout, enabled: Boolean) {
        if (enabled) {
            field.isErrorEnabled = true
            field.error = getString(R.string.empty_field)
        } else field.isErrorEnabled = false
    }

    private fun fetchPaymentMethods() {
        if (changeResultViewModel.paymentMethodsResponse.value == null) {
            changeResultViewModel.fetchPaymentMethods()
        }
    }

    private fun fetchBanks() {
        if (changeResultViewModel.banksResponse.value == null) {
            changeResultViewModel.fetchBanks()
        }
    }

    private fun setObservers() {
        changeResultViewModel.planResultsResponse.observe(this, { res ->
            when (res) {
                is NetworkResult.Success -> {
                    progressDialog.hideLoading()
                    val planResults = res.data?.data
                    setupAutocompleteText(planResults?.map { it.name }, binding.resultField)
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
        changeResultViewModel.paymentMethodsResponse.observe(this, { res ->
            when (res) {
                is NetworkResult.Success -> {
                    progressDialog.hideLoading()
                    val paymentMethods = res.data?.data
                    setupAutocompleteText(paymentMethods?.map { it.name }, binding.paymentMethodField)
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
        changeResultViewModel.banksResponse.observe(this, { res ->
            when (res) {
                is NetworkResult.Success -> {
                    progressDialog.hideLoading()
                    val banks = res.data?.data
                    setupAutocompleteText(banks?.map { it.name }, binding.bankField)
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
        changeResultViewModel.changeResultResponse.observe(this, { res ->
            when (res) {
                is NetworkResult.Success -> {
                    progressDialog.hideLoading()
                    val intent = Intent()
                    setResult(RESULT_OK, intent)
                    finish()
                }
                is NetworkResult.Loading -> {
                }
                is NetworkResult.Error -> {
                    progressDialog.hideLoading()
                    showException(res.message, this)
                }
            }
        })
    }

    private fun setupAutocompleteText(list: List<String>?, textField: TextInputLayout) {
        if (!list.isNullOrEmpty()) {
            val adapter = ArrayAdapter(this, R.layout.list_item, list)
            (textField.editText as? AutoCompleteTextView)?.setAdapter(adapter)
        }
    }

    private fun visibleField(field: Field) {
        when (field) {
            Field.REASON_DESCRIPTION -> {
                binding.reasonDescriptionField.isVisible = true
                binding.paymentMethodField.isVisible = false
                binding.amountField.isVisible = false
                binding.bankField.isVisible = false
                binding.phoneNumberField.isVisible = false
                binding.scheduleDateField.isVisible = false
                binding.scheduleTimeField.isVisible = false
            }
            Field.RESCHEDULED -> {
                binding.scheduleDateField.isVisible = true
                binding.scheduleTimeField.isVisible = true
                binding.reasonDescriptionField.isVisible = true
                binding.paymentMethodField.isVisible = false
                binding.amountField.isVisible = false
                binding.bankField.isVisible = false
                binding.phoneNumberField.isVisible = true
            }
            Field.PAYMENT -> {
                binding.paymentMethodField.isVisible = true
                binding.amountField.isVisible = false
                binding.bankField.isVisible = false
                binding.reasonDescriptionField.isVisible = false
                binding.phoneNumberField.isVisible = false
                binding.scheduleDateField.isVisible = false
                binding.scheduleTimeField.isVisible = false
            }
            Field.AMOUNT -> {
                binding.amountField.isVisible = true
                binding.paymentMethodField.isVisible = true
                binding.reasonDescriptionField.isVisible = false
                binding.phoneNumberField.isVisible = true
                binding.scheduleDateField.isVisible = false
                binding.scheduleTimeField.isVisible = false
                binding.bankField.isVisible = !(paymentMethodId == 1L || paymentMethodId == 4L)
            }
            Field.BANK -> {
                binding.bankField.isVisible = true
                binding.paymentMethodField.isVisible = true
                binding.reasonDescriptionField.isVisible = false
                binding.amountField.isVisible = false
                binding.phoneNumberField.isVisible = false
                binding.scheduleDateField.isVisible = false
                binding.scheduleTimeField.isVisible = false
            }
        }
    }

    private fun clearChilds(parent: Field) {
        when (parent) {
            Field.RESULT -> {
                binding.paymentMethodText.setText("")
                binding.bankText.setText("")
                binding.amountText.setText("")
                binding.phoneNumberText.setText("")
                binding.reasonDescriptionText.setText("")
                binding.scheduleDateText.setText("")
                binding.scheduleTimeText.setText("")

                binding.paymentMethodField.isErrorEnabled = false
                binding.bankField.isErrorEnabled = false
                binding.amountField.isErrorEnabled = false
                binding.phoneNumberField.isErrorEnabled = false
                binding.reasonDescriptionField.isErrorEnabled = false
                binding.scheduleDateField.isErrorEnabled = false
                binding.scheduleTimeField.isErrorEnabled = false

                selectedHour = null
                scheduledDate = null
                selectedMinute = null
                paymentMethodId = null
                bankId = null
            }
            Field.PAYMENT -> {
                binding.bankText.setText("")
                binding.amountText.setText("")
                binding.phoneNumberText.setText("")


                binding.bankField.isErrorEnabled = false
                binding.amountField.isErrorEnabled = false
                binding.phoneNumberField.isErrorEnabled = false

                bankId = null
            }
            Field.BANK -> {
                binding.amountText.setText("")
                binding.phoneNumberText.setText("")

                binding.amountField.isErrorEnabled = false
                binding.phoneNumberField.isErrorEnabled = false
            }
        }
    }

    enum class Field {
        REASON_DESCRIPTION, PAYMENT, AMOUNT, BANK, RESULT, RESCHEDULED
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