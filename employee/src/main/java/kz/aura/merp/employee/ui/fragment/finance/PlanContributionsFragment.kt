package kz.aura.merp.employee.ui.fragment.finance

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import kz.aura.merp.employee.R
import kz.aura.merp.employee.model.ChangePlanResult
import kz.aura.merp.employee.model.Result
import kz.aura.merp.employee.ui.dialog.*
import kz.aura.merp.employee.util.NetworkResult
import kz.aura.merp.employee.util.declareErrorByStatus

class PlanContributionsFragment : Fragment() {
//    // TODO: Rename and change types of parameters
//    private var param1: String? = null
//    private var param2: String? = null
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        arguments?.let {
//            param1 = it.getString(ARG_PARAM1)
//            param2 = it.getString(ARG_PARAM2)
//        }
//    }

//    ResultsDialogFragment.ResultsDialogListener,
//    BanksDialogFragment.BanksDialogListener,
//    PriceDialogFragment.PriceDialogListener,
//    CauseDialogFragment.CauseDialogListener,
//    PaymentMethodsDialog.PaymentMethodsDialogListener
//        private val planResult: ChangePlanResult = ChangePlanResult(0, "", 0, 0, 0.0, 0.0, 0)

//    binding.resultBtn.setOnClickListener {
//        if (results.isNotEmpty()) {
//            showResultsDialog()
//        }
//    }

//    mFinanceViewModel.planResultsResponse.observe(viewLifecycleOwner, { res ->
//        when (res) {
//            is NetworkResult.Success -> {
//                progressDialog.hideLoading()
//                res.data!!.find { it.id == plan.planResultId }?.let { binding.resultBtn.text = it.name }
//                results.addAll(res.data)
//            }
//            is NetworkResult.Loading -> progressDialog.showLoading()
//            is NetworkResult.Error -> {
//                progressDialog.hideLoading()
//                declareErrorByStatus(res.message, res.status, requireContext())
//            }
//        }
//    })

//    mFinanceViewModel.paymentMethodsResponse.observe(viewLifecycleOwner, { res ->
//        when (res) {
//            is NetworkResult.Success -> {
//                progressDialog.hideLoading()
//                showPaymentMethodsDialog()
//            }
//            is NetworkResult.Loading -> progressDialog.showLoading()
//            is NetworkResult.Error -> {
//                progressDialog.hideLoading()
//                declareErrorByStatus(res.message, res.status, requireContext())
//            }
//        }
//    })
//    mFinanceViewModel.banksResponse.observe(viewLifecycleOwner, { res ->
//        when (res) {
//            is NetworkResult.Success -> {
//                progressDialog.hideLoading()
//                showBanksDialog()
//            }
//            is NetworkResult.Loading -> progressDialog.showLoading()
//            is NetworkResult.Error -> {
//                progressDialog.hideLoading()
//                declareErrorByStatus(res.message, res.status, requireContext())
//            }
//        }
//    })

//    private fun updateResult(
//        resultId: Long,
//        reasonDescription: String?,
//        bankId: Long? = null,
//        paymentMethodId: Long? = null,
//        collectMoneyAmount: Int? = null
//    ) {
//        progressDialog.showLoading()
//        getCurrentLocation { latitude, longitude ->
//            mFinanceViewModel.updatePlanResult(
//                plan.contractId!!,
//                ChangePlanResult(
//                    resultId,
//                    reasonDescription,
//                    bankId,
//                    paymentMethodId,
//                    latitude,
//                    longitude,
//                    collectMoneyAmount
//                )
//            )
//        }
//    }
//
//    override fun onResultsDialogPositiveClick(dialog: DialogFragment, position: Int) {
//        binding.resultBtn.text = results[position].name
//        when (results[position].id) {
//            2L -> {
//                planResult.resultId = results[position].id
//                if (mFinanceViewModel.paymentMethodsResponse.value?.data == null) {
//                    progressDialog.showLoading()
//                    mFinanceViewModel.fetchPaymentMethods()
//                } else {
//                    showPaymentMethodsDialog()
//                }
//            }
//            else -> {
//                planResult.resultId = results[position].id
//                showCauseDialog()
//            }
//        }
//    }
//
//    private fun displayPlanResultIcons() {
//        val planResultId: Long? = plan.planResultId
//        val paymentMethodId = plan.planPaymentMethodId
//        val bankDrawable = when (plan.planPaymentBankId) {
//            2L -> R.drawable.ic_forte_bank
//            4L -> R.drawable.ic_halyk_bank
//            5L -> R.drawable.ic_center_credit_bank
//            6L -> R.drawable.ic_atf_bank
//            7L -> R.drawable.ic_kaspi_bank
//            else -> R.drawable.ic_baseline_account_balance_24
//        }
//        when (planResultId) {
//            1L -> {
//                binding.resultBtn.setCompoundDrawablesWithIntrinsicBounds(
//                    ContextCompat.getDrawable(requireContext(), R.drawable.ic_baseline_help_outline_24),
//                    null,
//                    null,
//                    null
//                )
//            }
//            2L -> {
//                when (paymentMethodId) {
//                    1L -> {
//                        binding.resultBtn.setCompoundDrawablesWithIntrinsicBounds(
//                            ContextCompat.getDrawable(requireContext(), R.drawable.ic_baseline_money_24),
//                            null,
//                            null,
//                            null
//                        )
//                    }
//                    2L -> {
//                        binding.resultBtn.setCompoundDrawablesWithIntrinsicBounds(
//                            ContextCompat.getDrawable(requireContext(), R.drawable.ic_outline_change_circle_24),
//                            null,
//                            ContextCompat.getDrawable(requireContext(), bankDrawable),
//                            null
//                        )
//                    }
//                    3L -> {
//                        binding.resultBtn.setCompoundDrawablesWithIntrinsicBounds(
//                            ContextCompat.getDrawable(requireContext(), R.drawable.ic_baseline_account_balance_24),
//                            null,
//                            ContextCompat.getDrawable(requireContext(), bankDrawable),
//                            null
//                        )
//                    }
//                }
//            }
//            3L -> {
//                binding.resultBtn.setCompoundDrawablesWithIntrinsicBounds(
//                    ContextCompat.getDrawable(requireContext(), R.drawable.ic_baseline_date_range_24),
//                    null,
//                    null,
//                    null
//                )
//            }
//            4L -> {
//                binding.resultBtn.setCompoundDrawablesWithIntrinsicBounds(
//                    ContextCompat.getDrawable(requireContext(), R.drawable.ic_baseline_close_24),
//                    null,
//                    null,
//                    null
//                )
//            }
//        }
//
//    }
//
//    private fun changeResultBtnText() {
//        binding.resultBtn.text = if (plan.planResultId != null) {
//            results.find { it.id == plan.planResultId }?.name
//        } else getString(R.string.result)
//    }
//
//    private fun showPriceDialog() {
//        val dialog = PriceDialogFragment((plan.price!! - plan.totalPaid!!).toInt())
//        dialog.show(childFragmentManager, "PriceDialogFragment")
//    }
//
//    private fun showBanksDialog() {
//        val banks = mFinanceViewModel.banksResponse.value!!.data!!
//        val banksByIcon: List<Result> = banks.map {
//            when (it.id) {
//                2L -> Result(it.name, R.drawable.ic_forte_bank)
//                4L -> Result(it.name, R.drawable.ic_halyk_bank)
//                5L -> Result(it.name, R.drawable.ic_center_credit_bank)
//                6L -> Result(it.name, R.drawable.ic_atf_bank)
//                7L -> Result(it.name, R.drawable.ic_kaspi_bank)
//                else -> return
//            }
//        }
//        val dialog = BanksDialogFragment(banksByIcon)
//        dialog.show(childFragmentManager, "BanksDialogFragment")
//    }
//
//    override fun onBanksDialogPositiveClick(dialog: DialogFragment, position: Int) {
//        val banks = mFinanceViewModel.banksResponse.value!!.data!!
//        dialog.dismiss()
//        planResult.bankId = banks[position].id
//        showPriceDialog()
//    }
//
//    override fun onBanksDialogNegativeClick(dialog: DialogFragment) {
//        changeResultBtnText()
//    }
//
//    private fun showResultsDialog() {
//        val resultsByIcon: List<Result> = results.map {
//            when (it.id) {
//                1L -> Result(it.name, R.drawable.ic_baseline_help_outline_24)
//                2L -> Result(it.name, R.drawable.ic_baseline_attach_money_24)
//                3L -> Result(it.name, R.drawable.ic_baseline_date_range_24)
//                4L -> Result(it.name, R.drawable.ic_baseline_close_24)
//                else -> return
//            }
//        }
//        val dialog = ResultsDialogFragment(resultsByIcon)
//        dialog.show(childFragmentManager, "ResultsDialogFragment")
//    }
//
//    private fun showCauseDialog() {
//        val dialog = CauseDialogFragment()
//        dialog.show(childFragmentManager, "CauseDialogFragment")
//    }
//
//    private fun showPaymentMethodsDialog() {
//        val paymentMethods = mFinanceViewModel.paymentMethodsResponse.value!!.data!!
//        val paymentMethodsByIcon: List<Result> = paymentMethods.map {
//            when (it.id) {
//                1L -> Result(it.name, R.drawable.ic_baseline_money_24)
//                2L -> Result(it.name, R.drawable.ic_outline_change_circle_24)
//                3L -> Result(it.name, R.drawable.ic_baseline_account_balance_24)
//                else -> return
//            }
//        }
//        val dialog = PaymentMethodsDialog(paymentMethodsByIcon)
//        dialog.show(childFragmentManager, "PaymentMethodsDialog")
//    }

//    override fun onPriceDialogPositiveClick(dialog: DialogFragment, price: Int) {
//        updateResult(
//            planResult.resultId,
//            planResult.reasonDescription,
//            planResult.bankId,
//            planResult.paymentMethodId,
//            price
//        )
//    }
//
//    override fun onPriceDialogNegativeClick(dialog: DialogFragment) {
//        changeResultBtnText()
//    }
//
//    override fun onCauseDialogNegativeClick(dialog: DialogFragment) {
//        changeResultBtnText()
//    }
//
//    override fun onCauseDialogPositiveClick(dialog: DialogFragment, cause: String) {
//        updateResult(
//            planResult.resultId,
//            cause,
//            planResult.bankId,
//            planResult.paymentMethodId
//        )
//    }
//
//    override fun onPaymentMethodsDialogNegativeClick(dialog: DialogFragment) {
//        changeResultBtnText()
//    }
//
//    override fun onPaymentMethodsDialogPositiveClick(dialog: DialogFragment, position: Int) {
//        val paymentMethods = mFinanceViewModel.paymentMethodsResponse.value!!.data!!
//        planResult.paymentMethodId = paymentMethods[position].id
//        when (paymentMethods[position].id) {
//            3L, 2L -> {
//                if (mFinanceViewModel.banksResponse.value?.data == null) {
//                    mFinanceViewModel.fetchBanks()
//                } else showBanksDialog()
//            }
//            1L -> {
//                showPriceDialog()
//            }
//            else -> updateResult(
//                planResult.resultId,
//                planResult.reasonDescription,
//                planResult.bankId,
//                paymentMethods[position].id
//            )
//        }
//    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_plan_contributions, container, false)
    }

//    companion object {
//        /**
//         * Use this factory method to create a new instance of
//         * this fragment using the provided parameters.
//         *
//         * @param param1 Parameter 1.
//         * @param param2 Parameter 2.
//         * @return A new instance of fragment PlanContributionsFragment.
//         */
//        // TODO: Rename and change types and number of parameters
//        @JvmStatic
//        fun newInstance(param1: String, param2: String) =
//            PlanContributionsFragment().apply {
//                arguments = Bundle().apply {
//                    putString(ARG_PARAM1, param1)
//                    putString(ARG_PARAM2, param2)
//                }
//            }
//    }
}