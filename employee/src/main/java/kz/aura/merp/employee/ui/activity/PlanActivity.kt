package kz.aura.merp.employee.ui.activity

import android.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import androidx.activity.viewModels
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import io.nlopez.smartlocation.SmartLocation
import kz.aura.merp.employee.R
import kz.aura.merp.employee.adapter.PaymentScheduleAdapter
import kz.aura.merp.employee.adapter.PhoneNumbersAdapter
import kz.aura.merp.employee.adapter.ResultsAdapter
import kz.aura.merp.employee.adapter.StepsAdapter
import kz.aura.merp.employee.data.model.*
import kz.aura.merp.employee.viewmodel.FinanceViewModel
import kz.aura.merp.employee.databinding.ActivityPlanBinding
import kz.aura.merp.employee.databinding.CauseAlertDialogBinding
import kz.aura.merp.employee.databinding.PriceAlertDialogBinding
import kz.aura.merp.employee.databinding.ResultsAlertDialogBinding
import kz.aura.merp.employee.util.NetworkResult
import kz.aura.merp.employee.util.ProgressDialog
import kz.aura.merp.employee.util.declareErrorByStatus

class PlanActivity : AppCompatActivity(), StepsAdapter.Companion.CompletedStepListener {

    private lateinit var binding: ActivityPlanBinding
    private val mFinanceViewModel: FinanceViewModel by viewModels()
    private lateinit var plan: Plan
    private val results = arrayListOf<PlanResult>()
    private val stepsAdapter: StepsAdapter by lazy { StepsAdapter(this) }
    private val phoneNumbersAdapter: PhoneNumbersAdapter by lazy { PhoneNumbersAdapter() }
    private val paymentScheduleAdapter: PaymentScheduleAdapter by lazy { PaymentScheduleAdapter() }
    private lateinit var progressDialog: ProgressDialog
    private val planResult: ChangePlanResult = ChangePlanResult(0, "", 0, 0, 0.0, 0.0, 0)
    private var currentLatitude: Double = 0.0
    private var currentLongitude: Double = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlanBinding.inflate(layoutInflater)
        plan = intent.getParcelableExtra("plan")!!
        binding.plan = plan
        binding.lifecycleOwner = this
        setContentView(binding.root)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = getString(R.string.plan)

        // Turn off screenshot
        window.setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE)

        // Observe MutableLiveData
        setObserve()

        // Get current location
        SmartLocation.with(this).location().oneFix()
            .start {
                currentLatitude = it.latitude
                currentLongitude = it.longitude
            }

        mFinanceViewModel.fetchPlanResults()
        mFinanceViewModel.fetchBusinessProcessStatuses()

        // Initialize Loading Dialog
        progressDialog = ProgressDialog(this)

        initStepView()
        initPhoneNumbers()
        displayPlanResultIcons()
        setupPaymentSchedule()

        binding.resultBtn.setOnClickListener {
            if (results.isNotEmpty()) {
                showResultsAlertDialog()
            }
        }
        binding.showPaymentScheduleBtn.setOnClickListener {
            val visibilityOfPaymentSchedule = binding.paymentScheduleRecyclerView.visibility
            if (visibilityOfPaymentSchedule == View.VISIBLE) {
                binding.paymentScheduleRecyclerView.visibility = View.GONE
                binding.showPaymentScheduleBtn.text = getString(R.string.showPaymentSchedule)
            } else {
                if (mFinanceViewModel.paymentScheduleResponse.value?.data == null) {
                    mFinanceViewModel.fetchPaymentSchedule(plan.contractId!!)
                }
                binding.paymentScheduleRecyclerView.visibility = View.VISIBLE
                binding.showPaymentScheduleBtn.text = getString(R.string.hidePaymentSchedule)
            }
        }
    }

    private fun initPhoneNumbers() {
        binding.phoneNumbers.layoutManager = LinearLayoutManager(this)
        binding.phoneNumbers.adapter = phoneNumbersAdapter
        phoneNumbersAdapter.setData(plan.customerPhoneNumbers)
    }

    private fun setupPaymentSchedule() {
        binding.paymentScheduleRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.paymentScheduleRecyclerView.adapter = paymentScheduleAdapter
    }

    private fun setObserve() {
        mFinanceViewModel.updatedPlanResponse.observe(this, { res ->
            when (res) {
                is NetworkResult.Success -> {
                    progressDialog.hideLoading()
                    this.plan = res.data!!
                    binding.plan = plan
                    binding.resultBtn.isEnabled = mFinanceViewModel.businessProcessStatusesResponse.value!!.data!!.last().id == plan.planBusinessProcessId
                    displayPlanResultIcons()
                    binding.executePendingBindings()
                }
                is NetworkResult.Loading -> progressDialog.showLoading()
                is NetworkResult.Error -> {
                    progressDialog.hideLoading()
                    declareErrorByStatus(res.message, res.status, this)
                }
            }
        })
        mFinanceViewModel.planResultsResponse.observe(this, { res ->
            when (res) {
                is NetworkResult.Success -> {
                    progressDialog.hideLoading()
                    res.data!!.find { it.id == plan.planResultId }?.let { binding.resultBtn.text = it.name }
                    results.addAll(res.data)
                }
                is NetworkResult.Loading -> progressDialog.showLoading()
                is NetworkResult.Error -> {
                    progressDialog.hideLoading()
                    declareErrorByStatus(res.message, res.status, this)
                }
            }
        })
        mFinanceViewModel.businessProcessStatusesResponse.observe(this, { res ->
            when (res) {
                is NetworkResult.Success -> {
                    progressDialog.hideLoading()
                    binding.resultBtn.isEnabled = res.data!!.last().id == plan.planBusinessProcessId
                    stepsAdapter.setData(res.data)
                }
                is NetworkResult.Loading -> progressDialog.showLoading()
                is NetworkResult.Error -> {
                    progressDialog.hideLoading()
                    declareErrorByStatus(res.message, res.status, this)
                }
            }
        })
        mFinanceViewModel.paymentScheduleResponse.observe(this, { res ->
            when (res) {
                is NetworkResult.Success -> {
                    binding.paymentScheduleProgressBar.visibility = View.GONE
                    binding.paymentScheduleProgressBar.visibility = View.INVISIBLE
                    paymentScheduleAdapter.setData(res.data!!, plan.contractCurrencyName!!)
                }
                is NetworkResult.Loading -> binding.paymentScheduleProgressBar.visibility = View.VISIBLE
                is NetworkResult.Error -> {
                    binding.paymentScheduleProgressBar.visibility = View.GONE
                    declareErrorByStatus(res.message, res.status, this)
                }
            }
        })
        mFinanceViewModel.paymentMethodsResponse.observe(this, { res ->
            when (res) {
                is NetworkResult.Success -> {
                    progressDialog.hideLoading()
                    showPaymentMethodsAlertDialog(res.data!!)
                }
                is NetworkResult.Loading -> progressDialog.showLoading()
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
                    showBanksAlertDialog(res.data!!)
                }
                is NetworkResult.Loading -> progressDialog.showLoading()
                is NetworkResult.Error -> {
                    progressDialog.hideLoading()
                    declareErrorByStatus(res.message, res.status, this)
                }
            }
        })
    }

    private fun initStepView() {
        binding.stepsRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.stepsRecyclerView.adapter = stepsAdapter
        binding.stepsRecyclerView.isNestedScrollingEnabled = false
        plan.planBusinessProcessId?.let { stepsAdapter.setStep(it) }
    }

    override fun stepCompleted(businessProcessStatus: BusinessProcessStatus) {
        confirmStepAlertDialog(businessProcessStatus)
    }

    private fun updateResult(
        resultId: Long,
        reasonDescription: String?,
        bankId: Long? = null,
        paymentMethodId: Long? = null,
        collectMoneyAmount: Int? = null
    ) {
        progressDialog.showLoading()
        mFinanceViewModel.updatePlanResult(
            plan.contractId!!,
            ChangePlanResult(
                resultId,
                reasonDescription,
                bankId,
                paymentMethodId,
                currentLatitude,
                currentLongitude,
                collectMoneyAmount
            )
        )
    }

    private fun displayPlanResultIcons() {
        val planResultId: Long? = plan.planResultId
        val paymentMethodId = plan.planPaymentMethodId
        val bankDrawable = when (plan.planPaymentBankId) {
            2L -> R.drawable.ic_forte_bank
            4L -> R.drawable.ic_halyk_bank
            5L -> R.drawable.ic_center_credit_bank
            6L -> R.drawable.ic_atf_bank
            7L -> R.drawable.ic_kaspi_bank
            else -> R.drawable.ic_baseline_account_balance_24
        }
        when (planResultId) {
            1L -> {
                binding.resultBtn.setCompoundDrawablesWithIntrinsicBounds(
                    ContextCompat.getDrawable(this, R.drawable.ic_baseline_help_outline_24),
                    null,
                    null,
                    null
                )
            }
            2L -> {
                when (paymentMethodId) {
                    1L -> {
                        binding.resultBtn.setCompoundDrawablesWithIntrinsicBounds(
                            ContextCompat.getDrawable(this, R.drawable.ic_baseline_money_24),
                            null,
                            null,
                            null
                        )
                    }
                    2L -> {
                        binding.resultBtn.setCompoundDrawablesWithIntrinsicBounds(
                            ContextCompat.getDrawable(this, R.drawable.ic_outline_change_circle_24),
                            null,
                            ContextCompat.getDrawable(this, bankDrawable),
                            null
                        )
                    }
                    3L -> {
                        binding.resultBtn.setCompoundDrawablesWithIntrinsicBounds(
                            ContextCompat.getDrawable(this, R.drawable.ic_baseline_account_balance_24),
                            null,
                            ContextCompat.getDrawable(this, bankDrawable),
                            null
                        )
                    }
                }
            }
            3L -> {
                binding.resultBtn.setCompoundDrawablesWithIntrinsicBounds(
                    ContextCompat.getDrawable(this, R.drawable.ic_baseline_date_range_24),
                    null,
                    null,
                    null
                )
            }
            4L -> {
                binding.resultBtn.setCompoundDrawablesWithIntrinsicBounds(
                    ContextCompat.getDrawable(this, R.drawable.ic_baseline_close_24),
                    null,
                    null,
                    null
                )
            }
        }

    }

    private fun changeResultBtnText() {
        binding.resultBtn.text = if (plan.planResultId != null) {
            results.find { it.id == plan.planResultId }?.name
        } else getString(R.string.result)
    }

    private fun showPriceAlertDialog() {
        val builder = AlertDialog.Builder(this)
        val priceView = LayoutInflater.from(this).inflate(R.layout.price_alert_dialog, null)
        val priceBinding = PriceAlertDialogBinding.bind(priceView)
        builder.setTitle(getString(R.string.price))

        builder.setPositiveButton(getString(R.string.save)) { dialog, _ ->
            updateResult(
                planResult.resultId,
                planResult.reasonDescription,
                planResult.bankId,
                planResult.paymentMethodId,
                priceBinding.priceEditText.text.toString().toInt()
            )
        }
        builder.setNegativeButton(getString(R.string.cancel)) { dialog, _ ->
            changeResultBtnText()
            dialog.dismiss()
        }
        builder.setOnCancelListener {
            changeResultBtnText()
        }

        builder.setView(priceBinding.root)

        val dialog = builder.create()

        dialog.show()
    }

    private fun showBanksAlertDialog(banks: ArrayList<Bank>) {
        val builder = AlertDialog.Builder(this)
        val banksView = LayoutInflater.from(this).inflate(R.layout.results_alert_dialog, null)
        val banksBinding = ResultsAlertDialogBinding.bind(banksView)

        builder.setTitle(getString(R.string.paymentMethod))
        builder.setNegativeButton(getString(R.string.cancel)) { dialog, _ ->
            changeResultBtnText()
            dialog.dismiss()
        }
        builder.setOnCancelListener {
            changeResultBtnText()
        }
        builder.setView(banksBinding.root)

        val dialog = builder.create()
        val banksAdapter = ResultsAdapter {
            dialog.dismiss()
            planResult.bankId = banks[it].id
            showPriceAlertDialog()
        }
        val banksByIcon: List<Result> = banks.map {
            when (it.id) {
                2L -> Result(it.name, R.drawable.ic_forte_bank)
                4L -> Result(it.name, R.drawable.ic_halyk_bank)
                5L -> Result(it.name, R.drawable.ic_center_credit_bank)
                6L -> Result(it.name, R.drawable.ic_atf_bank)
                7L -> Result(it.name, R.drawable.ic_kaspi_bank)
                else -> return
            }
        }
        banksAdapter.setData(banksByIcon)

        banksBinding.resultRecyclerView.layoutManager = LinearLayoutManager(this)
        banksBinding.resultRecyclerView.adapter = banksAdapter

        dialog.show()
    }

    private fun showResultsAlertDialog() {
        val builder = AlertDialog.Builder(this)
        val resultsView = LayoutInflater.from(this).inflate(R.layout.results_alert_dialog, null)
        val resultsBinding = ResultsAlertDialogBinding.bind(resultsView)

        builder.setTitle(getString(R.string.result))
        builder.setNegativeButton(getString(R.string.cancel)) { dialog, _ ->
            dialog.dismiss()
        }
        builder.setView(resultsBinding.root)

        val dialog = builder.create()
        val resultsAdapter = ResultsAdapter {
            dialog.dismiss()
            binding.resultBtn.text = results[it].name
            when (results[it].id) {
                2L -> {
                    planResult.resultId = results[it].id
                    if (mFinanceViewModel.paymentMethodsResponse.value?.data == null) {
                        progressDialog.showLoading()
                        mFinanceViewModel.fetchPaymentMethods()
                    } else {
                        showPaymentMethodsAlertDialog(mFinanceViewModel.paymentMethodsResponse.value!!.data!!)
                    }
                }
                else -> {
                    planResult.resultId = results[it].id
                    showCauseAlertDialog()
                }
            }
        }
        val resultsByIcon: List<Result> = results.map {
            when (it.id) {
                1L -> Result(it.name, R.drawable.ic_baseline_help_outline_24)
                2L -> Result(it.name, R.drawable.ic_baseline_attach_money_24)
                3L -> Result(it.name, R.drawable.ic_baseline_date_range_24)
                4L -> Result(it.name, R.drawable.ic_baseline_close_24)
                else -> return
            }
        }
        resultsAdapter.setData(resultsByIcon)

        resultsBinding.resultRecyclerView.layoutManager = LinearLayoutManager(this)
        resultsBinding.resultRecyclerView.adapter = resultsAdapter

        dialog.show()
    }

    private fun showCauseAlertDialog() {
        val builder = AlertDialog.Builder(this)
        val layoutInflater = LayoutInflater.from(this).inflate(R.layout.cause_alert_dialog, null)
        val dialogBinding = CauseAlertDialogBinding.bind(layoutInflater)
        dialogBinding.description = plan.planReasonDescription

        builder.setTitle(getString(R.string.cause))

        builder.setNegativeButton(getString(R.string.cancel)) { dialog, _ ->
            changeResultBtnText()
            dialog.dismiss()
        }
        builder.setPositiveButton(getString(R.string.save)) { _, _ ->
            updateResult(
                planResult.resultId,
                dialogBinding.finBusinessProcessesCause.text.toString(),
                planResult.bankId,
                planResult.paymentMethodId
            )
        }
        builder.setOnCancelListener {
            changeResultBtnText()
        }

        builder.setView(dialogBinding.root)
        val dialog = builder.create()
        dialog.show()
    }

    private fun showPaymentMethodsAlertDialog(paymentMethods: ArrayList<PaymentMethod>) {
        val builder = AlertDialog.Builder(this)
        val paymentMethodsView = LayoutInflater.from(this).inflate(R.layout.results_alert_dialog, null)
        val paymentMethodsBinding = ResultsAlertDialogBinding.bind(paymentMethodsView)

        builder.setTitle(getString(R.string.paymentMethod))
        builder.setNegativeButton(getString(R.string.cancel)) { dialog, _ ->
            changeResultBtnText()
            dialog.dismiss()
        }
        builder.setOnCancelListener {
            changeResultBtnText()
        }
        builder.setView(paymentMethodsBinding.root)

        val dialog = builder.create()
        val paymentMethodsAdapter = ResultsAdapter {
            dialog.dismiss()
            when (paymentMethods[it].id) {
                3L, 2L -> {
                    planResult.paymentMethodId = paymentMethods[it].id
                    if (mFinanceViewModel.banksResponse.value?.data == null) {
                        mFinanceViewModel.fetchBanks()
                    } else showBanksAlertDialog(mFinanceViewModel.banksResponse.value!!.data!!)
                }
                1L -> showPriceAlertDialog()
                else -> updateResult(
                    planResult.resultId,
                    planResult.reasonDescription,
                    planResult.bankId,
                    paymentMethods[it].id
                )
            }
        }
        val paymentMethodsIcon: List<Result> = paymentMethods.map {
            when (it.id) {
                1L -> Result(it.name, R.drawable.ic_baseline_money_24)
                2L -> Result(it.name, R.drawable.ic_outline_change_circle_24)
                3L -> Result(it.name, R.drawable.ic_baseline_account_balance_24)
                else -> return
            }
        }
        paymentMethodsAdapter.setData(paymentMethodsIcon)

        paymentMethodsBinding.resultRecyclerView.layoutManager = LinearLayoutManager(this)
        paymentMethodsBinding.resultRecyclerView.adapter = paymentMethodsAdapter

        dialog.show()
    }

    private fun confirmStepAlertDialog(businessProcessStatus: BusinessProcessStatus) {
        val builder = AlertDialog.Builder(this)

        builder.setTitle(getString(R.string.reallyWantToChangeStatusOfPlan))

        builder.setNegativeButton(getString(R.string.no)) { dialog, _ ->
            stepsAdapter.setStep(plan.planBusinessProcessId)
            dialog.dismiss()
        }
        builder.setPositiveButton(getString(R.string.yes)) { _, _ ->
            progressDialog.showLoading()
            mFinanceViewModel.updateBusinessProcessStep(
                plan.contractId!!,
                ChangeBusinessProcess(businessProcessStatus.id, currentLatitude, currentLongitude)
            )
        }
        builder.setOnCancelListener {
            stepsAdapter.setStep(plan.planBusinessProcessId)
        }

        builder.create().show()
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

}