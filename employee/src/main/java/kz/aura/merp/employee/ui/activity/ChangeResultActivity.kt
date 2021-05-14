package kz.aura.merp.employee.ui.activity

import android.content.Intent
import android.os.Bundle
import android.view.WindowManager
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.isVisible
import com.google.android.material.textfield.TextInputLayout
import dagger.hilt.android.AndroidEntryPoint
import io.nlopez.smartlocation.SmartLocation
import kz.aura.merp.employee.R
import kz.aura.merp.employee.databinding.ActivityChangeResultBinding
import kz.aura.merp.employee.model.ChangePlanResult
import kz.aura.merp.employee.util.*
import kz.aura.merp.employee.viewmodel.FinanceViewModel


@AndroidEntryPoint
class ChangeResultActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChangeResultBinding

    private val mFinanceViewModel: FinanceViewModel by viewModels()
    private lateinit var progressDialog: ProgressDialog
    private lateinit var clientPhoneNumbers: Array<String>

    private var contractId: Long? = null
    private var resultId: Long? = null
    private var paymentMethodId: Long? = null
    private var bankId: Long? = null
    private var businessProcessId: Long? = null

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

        contractId = intent.getLongExtra("contractId", 0L)
        clientPhoneNumbers = intent.getStringArrayExtra("clientPhoneNumbers")!!
        businessProcessId = intent.getLongExtra("businessProcessId", 0L)

        // Initialize Loading Dialog
        progressDialog = ProgressDialog(this)

        setObservers()

        mFinanceViewModel.fetchPlanResults()

        setupAutocompleteText(clientPhoneNumbers.toList(), binding.phoneNumberField)

        binding.resultText.setOnItemClickListener { _, _, i, _ ->
            resultId = mFinanceViewModel.planResultsResponse.value!!.data!![i].id
            clearChilds(Field.RESULT)
            when (i) {
                0, 2, 3 -> {
                    visibleField(Field.REASON_DESCRIPTION)
                }
                1 -> {
                    visibleField(Field.PAYMENT)
                    fetchPaymentMethods()
                }
            }
        }

        binding.paymentMethodText.setOnItemClickListener { _, _, i, _ ->
            paymentMethodId = mFinanceViewModel.paymentMethodsResponse.value!!.data!![i].id
            clearChilds(Field.PAYMENT)
            when (i) {
                0, 2 -> {
                    visibleField(Field.BANK)
                    fetchBanks()
                }
                1, 3 -> {
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
                if (validation()) {
                    save()
                } else {
                    showException(getString(R.string.fill_out_all_fields), this)
                }
            }
        }
    }

    private fun save() {
        progressDialog.showLoading()
        val reasonDescription: String? = if (binding.reasonDescriptionText.text.toString().isBlank()) null else binding.reasonDescriptionText.text.toString()
        val phoneNumber: String = binding.phoneNumberText.text.toString()
        val amount: String? = if (binding.amountText.text.toString().isBlank()) null else binding.amountText.text.toString()

//        SmartLocation.with(this).location().oneFix()
//            .start {
//                mFinanceViewModel.changeResult(
//                    contractId,
//                    ChangePlanResult(
//                        phoneNumber,
//                        resultId,
//                        reasonDescription,
//                        bankId,
//                        paymentMethodId,
//                        it.longitude,
//                        it.latitude,
//                        amount?.toInt()
//                    )
//                )
//            }

    }

    private fun validation(): Boolean {
        val phoneNumber: String = binding.phoneNumberText.text.toString()
        val amount: String = binding.amountText.text.toString()
        return if (resultId == 2L) {
            when (paymentMethodId) {
                3L, 2L -> {
                    !(bankId == null || amount.isBlank() || phoneNumber.isBlank())
                }
                1L, 4L -> {
                    !(amount.isBlank() || phoneNumber.isBlank())
                }
                else -> false
            }
        } else resultId != null
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
                    declareErrorByStatus(res.message, res.status, this)
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
                    declareErrorByStatus(res.message, res.status, this)
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
                    declareErrorByStatus(res.message, res.status, this)
                }
            }
        })
        mFinanceViewModel.changeResultResponse.observe(this, { res ->
            when (res) {
                is NetworkResult.Success -> {
                    progressDialog.hideLoading()
                    val intent = Intent();
                    intent.putExtra("contributions", res.data)
                    setResult(RESULT_OK, intent);
                    finish();
                }
                is NetworkResult.Loading -> {}
                is NetworkResult.Error -> {
                    progressDialog.hideLoading()
                    declareErrorByStatus(res.message, res.status, this)
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
            }
            Field.PAYMENT -> {
                binding.paymentMethodField.isVisible = true
                binding.amountField.isVisible = false
                binding.bankField.isVisible = false
                binding.reasonDescriptionField.isVisible = false
                binding.phoneNumberField.isVisible = false
            }
            Field.AMOUNT -> {
                binding.amountField.isVisible = true
                binding.paymentMethodField.isVisible = true
                binding.reasonDescriptionField.isVisible = false
                binding.phoneNumberField.isVisible = true
                binding.bankField.isVisible = !(paymentMethodId == 1L || paymentMethodId == 4L)
            }
            Field.BANK -> {
                binding.bankField.isVisible = true
                binding.paymentMethodField.isVisible = true
                binding.reasonDescriptionField.isVisible = false
                binding.amountField.isVisible = false
                binding.phoneNumberField.isVisible = false
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

                paymentMethodId = null
                bankId = null
            }
            Field.PAYMENT -> {
                binding.bankText.setText("")
                binding.amountText.setText("")
                binding.phoneNumberText.setText("")

                bankId = null
            }
            Field.BANK -> {
                binding.amountText.setText("")
                binding.phoneNumberText.setText("")
            }
        }
    }

    enum class Field {
        REASON_DESCRIPTION, PAYMENT, AMOUNT, BANK, RESULT
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}