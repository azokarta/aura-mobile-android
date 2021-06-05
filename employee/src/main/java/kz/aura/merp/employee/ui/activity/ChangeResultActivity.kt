package kz.aura.merp.employee.ui.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.isVisible
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputLayout
import dagger.hilt.android.AndroidEntryPoint
import io.nlopez.smartlocation.SmartLocation
import kz.aura.merp.employee.R
import kz.aura.merp.employee.databinding.ActivityChangeResultBinding
import kz.aura.merp.employee.model.AssignCollectMoneyCommand
import kz.aura.merp.employee.model.AssignScheduledCallCommand
import kz.aura.merp.employee.model.ChangePlanResult
import kz.aura.merp.employee.ui.dialog.DatePickerFragment
import kz.aura.merp.employee.ui.dialog.TimePickerFragment
import kz.aura.merp.employee.util.*
import kz.aura.merp.employee.viewmodel.FinanceViewModel
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import org.joda.time.format.DateTimeFormatter


@AndroidEntryPoint
class ChangeResultActivity : AppCompatActivity(), TimePickerFragment.TimePickerListener, DatePickerFragment.DatePickerListener {

    private lateinit var binding: ActivityChangeResultBinding

    private val mFinanceViewModel: FinanceViewModel by viewModels()
    private lateinit var progressDialog: ProgressDialog
    private lateinit var clientPhoneNumbers: Array<String>

    private var contractId: Long? = null
    private var resultId: Long? = null
    private var paymentMethodId: Long? = null
    private var bankId: Long? = null
    private var businessProcessId: Long? = null
    private var selectedHour: Int? = null
    private var selectedMinute: Int? = null
    private var countryCode: CountryCode = CountryCode.KZ
    private var scheduledDate: String? = null
    private lateinit var permissions: Permissions

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChangeResultBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Toolbar
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = getString(R.string.change_result)

        // Turn off screenshot
        window.setFlags(
            WindowManager.LayoutParams.FLAG_SECURE,
            WindowManager.LayoutParams.FLAG_SECURE
        )

        permissions = Permissions(this, this)

        contractId = intent.getLongExtra("contractId", 0L)
        clientPhoneNumbers = intent.getStringArrayExtra("clientPhoneNumbers")!!
        businessProcessId = intent.getLongExtra("businessProcessId", 0L)

        // Initialize Loading Dialog
        progressDialog = ProgressDialog(this)

        setObservers()

        mFinanceViewModel.fetchPlanResults()
        mFinanceViewModel.getCountryCode()

        setupAutocompleteText(clientPhoneNumbers.toList(), binding.phoneNumberField)

        binding.resultText.setOnItemClickListener { _, _, i, _ ->
            resultId = mFinanceViewModel.planResultsResponse.value!!.data!![i].id
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
            paymentMethodId = mFinanceViewModel.paymentMethodsResponse.value!!.data!![i].id
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
            bankId = mFinanceViewModel.banksResponse.value!!.data!![i].id
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
        permissions.requestGpsPermission()
    }

    private fun showDatePicker(view: View) {
        val datePicker = DatePickerFragment(this, this, date = convertStrToDateMillis(scheduledDate))
        datePicker.show(supportFragmentManager)
    }

    private fun save() {
        if (validation()) {
            val reasonDescription: String? = if (binding.reasonDescriptionText.text.toString().isBlank()) null else binding.reasonDescriptionText.text.toString()
            val phoneNumber: String = binding.phoneNumberText.text.toString()
            val amount: Int? = if (binding.amountText.text.toString().isBlank()) null else binding.amountText.text.toString().toInt()
            val scheduledDateTime = "$scheduledDate $selectedHour:$selectedMinute"

            if (isLocationServicesEnabled( permissions)) {
                progressDialog.showLoading()
                SmartLocation.with(this).location().oneFix()
                    .start {
                        when (resultId) {
                            3L -> {
                                mFinanceViewModel.changeResult(
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
                                mFinanceViewModel.changeResult(
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
                                mFinanceViewModel.changeResult(
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
        binding.scheduleDateText.setText(header)
    }

    private fun validation(): Boolean {
        val phoneNumber: String = binding.phoneNumberText.text.toString()
        val amount: String = binding.amountText.text.toString()
        val reasonDescription: String = binding.reasonDescriptionText.text.toString()
        var success = true

        when (resultId) {
            2L -> {
                if (paymentMethodId == null) {
                    binding.paymentMethodField.isErrorEnabled = true
                    binding.paymentMethodField.error = getString(R.string.error)
                    success = false
                } else binding.paymentMethodField.isErrorEnabled = false
                when (paymentMethodId) {
                    3L, 2L -> {
                        if (bankId == null) {
                            binding.bankField.isErrorEnabled = true
                            binding.bankField.error = getString(R.string.error)
                            success = false
                        } else binding.bankField.isErrorEnabled = false

                        if (amount.isBlank()) {
                            binding.amountField.isErrorEnabled = true
                            binding.amountField.error = getString(R.string.error)
                            success = false
                        } else binding.amountField.isErrorEnabled = false

                        if (phoneNumber.isBlank()) {
                            binding.phoneNumberField.isErrorEnabled = true
                            binding.phoneNumberField.error =
                                getString(R.string.enter_valid_phone_number)
                            success = false
                        } else binding.phoneNumberField.isErrorEnabled = false
                    }
                    1L, 4L -> {
                        if (amount.isBlank()) {
                            binding.amountField.isErrorEnabled = true
                            binding.amountField.error = getString(R.string.error)
                            success = false
                        } else binding.amountField.isErrorEnabled = false

                        if (phoneNumber.isBlank()) {
                            binding.phoneNumberField.isErrorEnabled = true
                            binding.phoneNumberField.error =
                                getString(R.string.enter_valid_phone_number)
                            success = false
                        } else binding.phoneNumberField.isErrorEnabled = false
                    }
                }
            }
            3L -> {
                if (selectedHour == null || selectedMinute == null) {
                    binding.scheduleTimeField.isErrorEnabled = true
                    binding.scheduleTimeField.error = getString(R.string.error)
                    success = false
                } else binding.scheduleTimeField.isErrorEnabled = false

                if (scheduledDate == null) {
                    binding.scheduleDateField.isErrorEnabled = true
                    binding.scheduleDateField.error = getString(R.string.error)
                    success = false
                } else binding.scheduleDateField.isErrorEnabled = false

                if (phoneNumber.isBlank()) {
                    binding.phoneNumberField.isErrorEnabled = true
                    binding.phoneNumberField.error = getString(R.string.enter_valid_phone_number)
                    success = false
                } else binding.phoneNumberField.isErrorEnabled = false
            }
            else -> {
                if (reasonDescription.isBlank()) {
                    binding.reasonDescriptionField.isErrorEnabled = true
                    binding.reasonDescriptionField.error = getString(R.string.error)
                    success = false
                } else binding.reasonDescriptionField.isErrorEnabled = false
            }
        }

        if (resultId == null) {
            binding.resultField.isErrorEnabled = true
            binding.resultField.error = getString(R.string.error)
            success = false
        } else binding.resultField.isErrorEnabled = false

        return success
    }

    private fun fetchPaymentMethods() {
        if (mFinanceViewModel.paymentMethodsResponse.value?.data.isNullOrEmpty()) {
            mFinanceViewModel.fetchPaymentMethods()
        }
    }

    private fun fetchBanks() {
        if (mFinanceViewModel.banksResponse.value?.data.isNullOrEmpty()) {
            mFinanceViewModel.fetchBanks()
        }
    }

    private fun setObservers() {
        mFinanceViewModel.planResultsResponse.observe(this, { res ->
            when (res) {
                is NetworkResult.Success -> {
                    progressDialog.hideLoading()
                    setupAutocompleteText(res.data?.map { it.name }, binding.resultField)
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
        mFinanceViewModel.paymentMethodsResponse.observe(this, { res ->
            when (res) {
                is NetworkResult.Success -> {
                    progressDialog.hideLoading()
                    setupAutocompleteText(res.data?.map { it.name }, binding.paymentMethodField)
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
        mFinanceViewModel.banksResponse.observe(this, { res ->
            when (res) {
                is NetworkResult.Success -> {
                    progressDialog.hideLoading()
                    setupAutocompleteText(res.data?.map { it.name }, binding.bankField)
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
        mFinanceViewModel.changeResultResponse.observe(this, { res ->
            when (res) {
                is NetworkResult.Success -> {
                    progressDialog.hideLoading()
                    val intent = Intent();
                    setResult(RESULT_OK, intent);
                    finish();
                }
                is NetworkResult.Loading -> {
                }
                is NetworkResult.Error -> {
                    progressDialog.hideLoading()
                    showException(res.message, this)
                }
            }
        })
        mFinanceViewModel.countryCode.observe(this, { countryCode ->
            this.countryCode = countryCode
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
}