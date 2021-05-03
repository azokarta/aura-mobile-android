package kz.aura.merp.employee.ui.activity

import android.R.attr.country
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
import com.google.android.material.textfield.TextInputLayout
import dagger.hilt.android.AndroidEntryPoint
import io.nlopez.smartlocation.SmartLocation
import kz.aura.merp.employee.R
import kz.aura.merp.employee.databinding.ActivityAddContributionBinding
import kz.aura.merp.employee.model.ChangePlanResult
import kz.aura.merp.employee.model.Contribution
import kz.aura.merp.employee.util.NetworkResult
import kz.aura.merp.employee.util.ProgressDialog
import kz.aura.merp.employee.util.declareErrorByStatus
import kz.aura.merp.employee.viewmodel.FinanceViewModel


@AndroidEntryPoint
class AddContributionActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddContributionBinding

    private val mFinanceViewModel: FinanceViewModel by viewModels()
    private lateinit var progressDialog: ProgressDialog
    private val paymentField = "PAYMENT"
    private val reasonDescriptionField = "REASON_DESCRIPTION"
    private val amountField = "AMOUNT"
    private val bankField = "BANK"
    private lateinit var clientPhoneNumbers: Array<String>

    private var contractId: Long? = null
    private var resultId: Long? = null
    private var paymentMethodId: Long? = null
    private var bankId: Long? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddContributionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Toolbar
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = getString(R.string.add_contribution)

        // Turn off screenshot
        window.setFlags(
            WindowManager.LayoutParams.FLAG_SECURE,
            WindowManager.LayoutParams.FLAG_SECURE
        )

        contractId = intent.getLongExtra("contractId", 0)
        clientPhoneNumbers = intent.getStringArrayExtra("clientPhoneNumbers")!!

        // Initialize Loading Dialog
        progressDialog = ProgressDialog(this)

        setObservers()

        mFinanceViewModel.fetchPlanResults()

        setupAutocompleteText(clientPhoneNumbers.toList(), binding.phoneNumberField)

        binding.resultText.setOnItemClickListener { _, _, i, _ ->
            resultId = mFinanceViewModel.planResultsResponse.value!!.data!![i].id
            when (i) {
                0, 2, 3 -> {
                    visibleField(reasonDescriptionField)
                }
                1 -> {
                    visibleField(paymentField)
                    fetchPaymentMethods()
                }
            }
        }

        binding.paymentMethodText.setOnItemClickListener { _, _, i, _ ->
            paymentMethodId = mFinanceViewModel.paymentMethodsResponse.value!!.data!![i].id
            when (i) {
                0, 2 -> {
                    visibleField(bankField)
                    fetchBanks()
                }
                1, 3 -> {
                    visibleField(amountField)
                }
            }
        }

        binding.bankText.setOnItemClickListener { _, _, i, _ ->
            bankId = mFinanceViewModel.banksResponse.value!!.data!![i].id
            visibleField(amountField)
        }

        binding.save.setOnClickListener(::save)
    }

    private fun save(view: View) {
        var reasonDescription: String? = binding.reasonDescriptionText.text.toString()
        var phoneNumber: String? = binding.phoneNumberText.text.toString()
        var amount: String? = binding.amountText.text.toString()
        when (resultId) {
            1L, 3L, 4L -> {
                paymentMethodId = null
                bankId = null
                amount = null
                phoneNumber = null
            }
            else -> {
                reasonDescription = null
            }
        }
        when (paymentMethodId) {
            1L, 4L -> {
                bankId = null
            }
        }

        SmartLocation.with(this).location().oneFix()
            .start {
                mFinanceViewModel.assignCollectMoney(
                    contractId,
                    ChangePlanResult(
                        phoneNumber,
                        resultId,
                        reasonDescription,
                        bankId,
                        paymentMethodId,
                        it.longitude,
                        it.latitude,
                        amount?.toInt()
                    )
                )
            }

    }

//    private fun define(phoneNumber: String) {
//        if (!phoneNumber.replace(" ", "")
//                .contains(country.getPhoneCode())
//        ) throw RuntimeException("Код телефона неправильно указан")
//
//        if (!deleteChars(phoneNumber).matches("[0-9]+")) throw RuntimeException("Номер телефона содержит недопустимое знаки")
//
//        if (deleteChars(phoneNumber).length !== deleteChars(country.getTelPattern()).length() + deleteChars(
//                country.getPhoneCode()
//            ).length()
//        ) throw RuntimeException("Длина номера телефона неправильно")
//    }
//
//    private fun deleteChars(str: String): String {
//        return str.replace("+", "")
//            .replace("-", "")
//            .replace("(", "")
//            .replace(")", "")
//            .replace(" ", "")
//    }

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
        mFinanceViewModel.assignCollectMoneyResponse.observe(this, { res ->
            when (res) {
                is NetworkResult.Success -> {
                    progressDialog.hideLoading()
                    val intent = Intent();
                    intent.putExtra("contributions", res.data)
                    setResult(RESULT_OK, intent);
                    finish();
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
    }

    private fun setupAutocompleteText(list: List<String>?, textField: TextInputLayout) {
        if (!list.isNullOrEmpty()) {
            val adapter = ArrayAdapter(this, R.layout.list_item, list)
            (textField.editText as? AutoCompleteTextView)?.setAdapter(adapter)
        }
    }

    private fun visibleField(field: String) {
        when (field) {
            "REASON_DESCRIPTION" -> {
                binding.reasonDescriptionField.isVisible = true
                binding.paymentMethodField.isVisible = false
                binding.amountField.isVisible = false
                binding.bankField.isVisible = false
                binding.phoneNumberField.isVisible = false
            }
            "PAYMENT" -> {
                binding.paymentMethodField.isVisible = true
                binding.amountField.isVisible = false
                binding.bankField.isVisible = false
                binding.reasonDescriptionField.isVisible = false
                binding.phoneNumberField.isVisible = false
            }
            "AMOUNT" -> {
                binding.amountField.isVisible = true
                binding.paymentMethodField.isVisible = true
                binding.reasonDescriptionField.isVisible = false
                binding.phoneNumberField.isVisible = true
                binding.bankField.isVisible = !(paymentMethodId == 1L || paymentMethodId == 4L)
            }
            "BANK" -> {
                binding.bankField.isVisible = true
                binding.paymentMethodField.isVisible = true
                binding.reasonDescriptionField.isVisible = false
                binding.amountField.isVisible = false
                binding.phoneNumberField.isVisible = false
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}