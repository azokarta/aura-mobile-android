package kz.aura.merp.employee.ui.fragment.finance

import android.content.Intent
import android.opengl.Visibility
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import io.nlopez.smartlocation.SmartLocation
import kz.aura.merp.employee.R
import kz.aura.merp.employee.adapter.ContributionsAdapter
import kz.aura.merp.employee.databinding.FragmentPlanContributionsBinding
import kz.aura.merp.employee.model.ChangePlanResult
import kz.aura.merp.employee.model.Plan
import kz.aura.merp.employee.model.PlanResult
import kz.aura.merp.employee.model.Result
import kz.aura.merp.employee.ui.activity.AddContributionActivity
import kz.aura.merp.employee.ui.dialog.*
import kz.aura.merp.employee.util.NetworkResult
import kz.aura.merp.employee.util.ProgressDialog
import kz.aura.merp.employee.util.declareErrorByStatus
import kz.aura.merp.employee.util.verifyAvailableNetwork
import kz.aura.merp.employee.viewmodel.FinanceViewModel
import kz.aura.merp.employee.viewmodel.SharedViewModel

@AndroidEntryPoint
class PlanContributionsFragment : Fragment(),
    ResultsDialogFragment.ResultsDialogListener,
    BanksDialogFragment.BanksDialogListener,
    PriceDialogFragment.PriceDialogListener,
    CauseDialogFragment.CauseDialogListener,
    PaymentMethodsDialog.PaymentMethodsDialogListener,
    PhoneNumbersDialogFragment.PhoneNumbersDialogListener
{

    private var _binding: FragmentPlanContributionsBinding? = null
    private val binding get() = _binding!!

    private lateinit var plan: Plan
    private val results = arrayListOf<PlanResult>()
    private val planResult: ChangePlanResult = ChangePlanResult("", 0, null, 0, 0, 0.0, 0.0, 0)
    private val mFinanceViewModel: FinanceViewModel by activityViewModels()
    private lateinit var progressDialog: ProgressDialog
    private val contributionsAdapter: ContributionsAdapter by lazy { ContributionsAdapter() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            plan = it.getParcelable(ARG_PARAM1)!!
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlanContributionsBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this

        // Initialize Loading Dialog
        progressDialog = ProgressDialog(requireContext())

        setupRecyclerView()

        setupObservers()

        binding.addContribution.setOnClickListener {
            val intent = Intent(requireContext(), AddContributionActivity::class.java)
            intent.putExtra("contractId", plan.contractId!!)
            intent.putExtra("clientPhoneNumbers", plan.customerPhoneNumbers!!.toTypedArray())
            startActivity(intent)
        }

        mFinanceViewModel.fetchPlanContributions(plan.contractId!!)
        mFinanceViewModel.fetchPlanResults()

        // If network is disconnected and user clicks restart, get data again
        binding.networkDisconnected.restart.setOnClickListener {
            if (verifyAvailableNetwork(requireContext())) {
                mFinanceViewModel.fetchPlanContributions(plan.contractId!!)
                binding.progressBar.isVisible = true
                binding.recyclerView.isVisible = true
                binding.networkDisconnected.root.isVisible = false
            }
        }

        return binding.root
    }

    private fun setupRecyclerView() {
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = contributionsAdapter
        binding.recyclerView.isNestedScrollingEnabled = false
    }

    private fun setupObservers() {
        mFinanceViewModel.planResultsResponse.observe(viewLifecycleOwner, { res ->
            when (res) {
                is NetworkResult.Success -> {
                    progressDialog.hideLoading()
                    results.addAll(res.data!!)
                }
                is NetworkResult.Loading -> progressDialog.showLoading()
                is NetworkResult.Error -> {
                    progressDialog.hideLoading()
                    declareErrorByStatus(res.message, res.status, requireContext())
                }
            }
        })

        mFinanceViewModel.paymentMethodsResponse.observe(viewLifecycleOwner, { res ->
            when (res) {
                is NetworkResult.Success -> {
                    progressDialog.hideLoading()
                    showPaymentMethodsDialog()
                }
                is NetworkResult.Loading -> progressDialog.showLoading()
                is NetworkResult.Error -> {
                    progressDialog.hideLoading()
                    declareErrorByStatus(res.message, res.status, requireContext())
                }
            }
        })
        mFinanceViewModel.banksResponse.observe(viewLifecycleOwner, { res ->
            when (res) {
                is NetworkResult.Success -> {
                    progressDialog.hideLoading()
                    showBanksDialog()
                }
                is NetworkResult.Loading -> progressDialog.showLoading()
                is NetworkResult.Error -> {
                    progressDialog.hideLoading()
                    declareErrorByStatus(res.message, res.status, requireContext())
                }
            }
        })
        mFinanceViewModel.contributionsResponse.observe(viewLifecycleOwner, { res ->
            when (res) {
                is NetworkResult.Success -> {
                    showLoadingOrNoData(false, res.data.isNullOrEmpty())
                    contributionsAdapter.setData(res.data!!)
                }
                is NetworkResult.Loading -> showLoadingOrNoData(true)
                is NetworkResult.Error -> {
                    showLoadingOrNoData(false, res.data.isNullOrEmpty())
                    checkError(res)
                }
            }
        })
    }

    private fun showLoadingOrNoData(visibility: Boolean, dataIsEmpty: Boolean = true) {
        if (visibility) {
            binding.emptyData = true
            binding.dataReceived = false
        } else {
            binding.emptyData = dataIsEmpty
            binding.dataReceived = true
        }
    }

    private fun <T> checkError(res: NetworkResult.Error<T>) {
        if (!verifyAvailableNetwork(requireContext())) {
            binding.networkDisconnected.root.isVisible = true
        } else {
            declareErrorByStatus(res.message, res.status, requireContext())
        }
    }

    private fun updateResult(
        phoneNumber: String?,
        resultId: Long,
        reasonDescription: String?,
        bankId: Long? = null,
        paymentMethodId: Long? = null,
        collectMoneyAmount: Int? = null
    ) {
        progressDialog.showLoading()
        getCurrentLocation { latitude, longitude ->
            mFinanceViewModel.assignCollectMoney(
                plan.contractId!!,
                ChangePlanResult(
                    phoneNumber,
                    resultId,
                    reasonDescription,
                    bankId,
                    paymentMethodId,
                    latitude,
                    longitude,
                    collectMoneyAmount
                )
            )
        }
    }

    override fun onResultsDialogPositiveClick(dialog: DialogFragment, position: Int) {
        when (results[position].id) {
            2L -> {
                planResult.resultId = results[position].id
                if (mFinanceViewModel.paymentMethodsResponse.value?.data == null) {
                    progressDialog.showLoading()
                    mFinanceViewModel.fetchPaymentMethods()
                } else {
                    showPaymentMethodsDialog()
                }
            }
            else -> {
                planResult.resultId = results[position].id
                showCauseDialog()
            }
        }
    }

    private fun getCurrentLocation(callback: (latitude: Double, longitude: Double) -> Unit) {
        SmartLocation.with(requireContext()).location().oneFix()
            .start {
                callback.invoke(it.latitude, it.longitude)
            }
    }

    private fun showPriceDialog() {
        val dialog = PriceDialogFragment(this, (plan.price!! - plan.totalPaid!!).toInt())
        dialog.show(childFragmentManager, "PriceDialogFragment")
    }

    private fun showBanksDialog() {
        val banks = mFinanceViewModel.banksResponse.value!!.data!!
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
        val dialog = BanksDialogFragment(this, banksByIcon)
        dialog.show(childFragmentManager, "BanksDialogFragment")
    }

    override fun onBanksDialogPositiveClick(dialog: DialogFragment, position: Int) {
        val banks = mFinanceViewModel.banksResponse.value!!.data!!
        dialog.dismiss()
        planResult.bankId = banks[position].id
        showPriceDialog()
    }

    override fun onBanksDialogNegativeClick(dialog: DialogFragment) {}

    private fun showResultsDialog() {
        val resultsByIcon: List<Result> = results.map {
            when (it.id) {
                1L -> Result(it.name, R.drawable.ic_baseline_help_outline_24)
                2L -> Result(it.name, R.drawable.ic_baseline_attach_money_24)
                3L -> Result(it.name, R.drawable.ic_baseline_date_range_24)
                4L -> Result(it.name, R.drawable.ic_baseline_close_24)
                else -> return
            }
        }
        val dialog = ResultsDialogFragment(this, resultsByIcon)
        dialog.show(childFragmentManager, "ResultsDialogFragment")
    }

    private fun showCauseDialog() {
        val dialog = CauseDialogFragment(this)
        dialog.show(childFragmentManager, "CauseDialogFragment")
    }

    private fun showPaymentMethodsDialog() {
        val paymentMethods = mFinanceViewModel.paymentMethodsResponse.value!!.data!!
        val paymentMethodsByIcon: List<Result> = paymentMethods.map {
            when (it.id) {
                1L -> Result(it.name, R.drawable.ic_baseline_money_24)
                2L -> Result(it.name, R.drawable.ic_outline_change_circle_24)
                3L -> Result(it.name, R.drawable.ic_baseline_account_balance_24)
                4L -> Result(it.name, R.drawable.ic_baseline_directions_bike_24)
                else -> Result(it.name, R.drawable.ic_baseline_help_outline_24)
            }
        }
        val dialog = PaymentMethodsDialog(this, paymentMethodsByIcon)
        dialog.show(childFragmentManager, "PaymentMethodsDialog")
    }

    override fun onPriceDialogPositiveClick(dialog: DialogFragment, price: Int) {
        planResult.collectMoneyAmount = price
        showPhoneNumbersDialog()
    }

    override fun onCauseDialogPositiveClick(dialog: DialogFragment, cause: String) {
        updateResult(
            "",
            planResult.resultId!!,
            cause,
            planResult.bankId,
            planResult.paymentMethodId
        )
    }

    override fun onPaymentMethodsDialogPositiveClick(dialog: DialogFragment, position: Int) {
        val paymentMethods = mFinanceViewModel.paymentMethodsResponse.value!!.data!!
        planResult.paymentMethodId = paymentMethods[position].id
        when (paymentMethods[position].id) {
            3L, 2L -> {
                if (mFinanceViewModel.banksResponse.value?.data == null) {
                    mFinanceViewModel.fetchBanks()
                } else showBanksDialog()
            }
            1L, 4L -> {
                showPriceDialog()
            }
        }
    }

    private fun showPhoneNumbersDialog() {
        val dialog = PhoneNumbersDialogFragment(this, plan.customerPhoneNumbers)
        dialog.show(childFragmentManager, "PhoneNumbersDialog")
    }

    companion object {
        private const val ARG_PARAM1 = "plan"

        @JvmStatic
        fun newInstance(plan: Plan) =
            PlanContributionsFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(ARG_PARAM1, plan)
                }
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun selectPhoneNumber(phoneNumber: String?) {
        updateResult(
            phoneNumber,
            planResult.resultId!!,
            planResult.reasonDescription,
            planResult.bankId,
            planResult.paymentMethodId,
            planResult.collectMoneyAmount
        )
    }
}