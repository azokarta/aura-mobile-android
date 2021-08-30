package kz.aura.merp.employee.ui.finance.fragment

import android.os.Bundle
import android.text.Html
import android.view.*
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kz.aura.merp.employee.R
import kz.aura.merp.employee.adapter.PlansAdapter
import kz.aura.merp.employee.base.NetworkResult
import kz.aura.merp.employee.databinding.FragmentMonthlyPlanBinding
import kz.aura.merp.employee.model.Plan
import kz.aura.merp.employee.model.PlanFilter
import kz.aura.merp.employee.ui.finance.dialog.MonthlyPlanFilterDialogFragment
import kz.aura.merp.employee.ui.common.TimePickerFragment
import kz.aura.merp.employee.util.*
import kz.aura.merp.employee.viewmodel.PlanFilterViewModel
import kz.aura.merp.employee.viewmodel.SharedViewModel
import kz.aura.merp.employee.viewmodel.finance.MonthlyPlanViewModel
import org.joda.time.format.DateTimeFormat
import org.joda.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.ArrayList

@AndroidEntryPoint
class MonthlyPlanFragment : Fragment(R.layout.fragment_monthly_plan), PlansAdapter.OnClickListener,
    TimePickerFragment.TimePickerListener, SwipeRefreshLayout.OnRefreshListener {

    private val monthlyPlanViewModel: MonthlyPlanViewModel by viewModels()
    private val filterViewModel: PlanFilterViewModel by activityViewModels()
    private val sharedViewModel: SharedViewModel by viewModels()
    private val plansAdapter: PlansAdapter by lazy { PlansAdapter(this) }
    private var _binding: FragmentMonthlyPlanBinding? = null
    private val binding get() = _binding!!
    private lateinit var progressDialog: ProgressDialog
    private var clickedContractId: Long? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentMonthlyPlanBinding.bind(view)

        with (binding) {
            lifecycleOwner = this@MonthlyPlanFragment
            sharedViewModel = this@MonthlyPlanFragment.sharedViewModel

            swipeRefresh.setOnRefreshListener(this@MonthlyPlanFragment)

            filterList.setOnClickListener {
                val dialog = MonthlyPlanFilterDialogFragment()
                dialog.show(childFragmentManager, "PlanFilterBottomSheetDialog")
            }
            clearFilter.setOnClickListener {
                filterViewModel.clearFilter()
            }
            explanationAboutColors.setOnClickListener(::explainAboutColors)
        }

        progressDialog = ProgressDialog(requireContext())

        setupRecyclerView()

        observeLiveData()

        callRequests()
    }

    private fun callRequests() {
        monthlyPlanViewModel.fetchPlans()
    }

    override fun sendToDailyPlan(contractId: Long) {
        clickedContractId = contractId
        val timePicker = TimePickerFragment(this)
        timePicker.show(childFragmentManager)
    }

    private fun setupRecyclerView() {
        with (binding) {
            recyclerView.adapter = plansAdapter
            recyclerView.isNestedScrollingEnabled = false
        }
    }

    override fun selectedTime(hour: Int, minute: Int) {
        clickedContractId?.let {
            monthlyPlanViewModel.createDailyPlan(it, "$hour:$minute")
        }
    }

    private fun observeLiveData() {
        monthlyPlanViewModel.plansResponse.observe(viewLifecycleOwner, { res ->
            when (res) {
                is NetworkResult.Success -> {
                    sharedViewModel.setResponse(res)
                    filterPlans()
                }
                is NetworkResult.Loading -> sharedViewModel.setResponse(res)
                is NetworkResult.Error -> sharedViewModel.setResponse(res)
            }
        })
        filterViewModel.filterParams.observe(viewLifecycleOwner, { params ->
            val plans = monthlyPlanViewModel.plansResponse.value?.data?.data
            if (!plans.isNullOrEmpty()) {
                filterPlans(params)
            } else {
                filterViewModel.clearFilter()
            }
        })
        monthlyPlanViewModel.createDailyPlanResponse.observe(viewLifecycleOwner, { res ->
            when (res) {
                is NetworkResult.Success -> {
                    monthlyPlanViewModel.createDailyPlanResponse.postValue(null)
                    progressDialog.hideLoading()
                    showSnackbar(binding.recyclerView)
                }
                is NetworkResult.Loading -> progressDialog.showLoading()
                is NetworkResult.Error -> {
                    progressDialog.hideLoading()
                    showException(res.message, requireContext())
                }
            }
        })
    }

    private fun changeTextsFilter(filterParams: PlanFilter) {
        val filterSortParams = arrayListOf(
            getString(R.string.payment_date),
            getString(R.string.contract_date),
            getString(R.string.fullname)
        )
        binding.sort = filterSortParams[filterParams.selectedSortFilter]
        binding.searchBySn = when (filterParams.selectedSearchBy) {
            0 -> if (filterParams.query.isNotBlank()) "${getString(R.string.cn)} = ${filterParams.query}" else ""
            1 -> if (filterParams.query.isNotBlank()) "${getString(R.string.fullname)} = ${filterParams.query}" else ""
            else -> ""
        }
        binding.problematic.isVisible = filterParams.problematic
    }

    private fun filterPlans(
        filterParams: PlanFilter = filterViewModel.getDefault()
    ) {
        changeTextsFilter(filterParams)
        var filteredPlans =
            mutableListOf<Plan>().apply { addAll(monthlyPlanViewModel.plansResponse.value!!.data!!.data) }

        val dtf: DateTimeFormatter = DateTimeFormat.forPattern("dd.MM.yyyy")

        // Filter by search
        when (filterParams.selectedSearchBy) {
            0 -> {
                filteredPlans = filteredPlans.filter {
                    it.contractNumber.toString().indexOf(filterParams.query) >= 0
                }.toMutableList()
            }
            1 -> {
                val conditions = ArrayList<(Plan) -> Boolean>()
                filterParams.query.lowercase(Locale.ROOT).split(" ").map {
                    conditions.add { plan ->
                        (plan.customerLastname + " " + plan.customerFirstname + " " + plan.customerMiddlename).toLowerCase(
                            Locale.ROOT
                        ).indexOf(
                            it
                        ) >= 0
                    }
                }
                filteredPlans =
                    filteredPlans.filter { candidate -> conditions.all { it(candidate) } }
                        .toMutableList()
            }
        }

        // Sort by selected parameter
        when (filterParams.selectedSortFilter) {
            0 -> filteredPlans.sortBy { dtf.parseLocalDate(it.nextPaymentDate) }
            1 -> filteredPlans.sortBy { dtf.parseLocalDate(it.contractDate) }
            2 -> filteredPlans.sortBy { it.customerLastname + " " + it.customerFirstname + " " + it.customerMiddlename }
        }

        // Filter problematic plans
        filteredPlans =
            filteredPlans.filter { it.problem == filterParams.problematic }.toMutableList()

        // Change recyclerView
        plansAdapter.submitList(filteredPlans)

        val allOverdueDays =
            "(<font color=#DF1010>${filteredPlans.count { it.paymentOverDueDays!! > 0 }}</font>, " +
                    "<font color=#FFC107>${filteredPlans.count { it.paymentOverDueDays!! == 0 }}</font>, " +
                    "<font color=#4CAF50>${filteredPlans.count { it.paymentOverDueDays!! < 0 }}</font>)"

        binding.allOverdueDays.text = Html.fromHtml(allOverdueDays)
        binding.quantityOfList = filteredPlans.size
    }

    private fun explainAboutColors(view: View) {
        MaterialAlertDialogBuilder(requireActivity())
            .setView(R.layout.explanation_about_colors)
            .setTitle(resources.getString(R.string.explanation_about_colors))
            .show()
    }

    private fun showSnackbar(view: View) = Snackbar.make(
        view,
        R.string.successfully_saved,
        Snackbar.LENGTH_SHORT
    ).show()

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onRefresh() {
        sharedViewModel.setLoadingType(LoadingType.SWIPE_REFRESH)
        callRequests()
    }
}