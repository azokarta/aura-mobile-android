package kz.aura.merp.employee.ui.fragment.finance

import android.os.Bundle
import android.text.Html
import android.view.*
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import kz.aura.merp.employee.R
import kz.aura.merp.employee.adapter.DailyPlanAdapter
import kz.aura.merp.employee.databinding.FragmentDailyPlanBinding
import kz.aura.merp.employee.model.Plan
import kz.aura.merp.employee.model.PlanFilter
import kz.aura.merp.employee.ui.dialog.DailyPlanFilterDialogFragment
import kz.aura.merp.employee.util.LoadingType
import kz.aura.merp.employee.util.NetworkResult
import kz.aura.merp.employee.viewmodel.FinanceViewModel
import kz.aura.merp.employee.viewmodel.PlanFilterViewModel
import kz.aura.merp.employee.viewmodel.SharedViewModel
import org.joda.time.format.DateTimeFormat
import org.joda.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.ArrayList

@AndroidEntryPoint
class DailyPlanFragment : Fragment(), SwipeRefreshLayout.OnRefreshListener {

    private var _binding: FragmentDailyPlanBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private lateinit var financeViewModel: FinanceViewModel
    private lateinit var filterViewModel: PlanFilterViewModel
    private lateinit var sharedViewModel: SharedViewModel
    private val plansAdapter: DailyPlanAdapter by lazy { DailyPlanAdapter() }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        filterViewModel = ViewModelProvider(requireActivity()).get(PlanFilterViewModel::class.java)
        sharedViewModel = ViewModelProvider(this).get(SharedViewModel::class.java)
        financeViewModel = ViewModelProvider(this).get(FinanceViewModel::class.java)

        _binding = FragmentDailyPlanBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        binding.sharedViewModel = sharedViewModel
        val root: View = binding.root

        binding.swipeRefresh.setOnRefreshListener(this)
        binding.explanationAboutColors.setOnClickListener(::explainAboutColors)
        binding.clearFilter.setOnClickListener {
            filterViewModel.clearFilter()
        }
        binding.filterList.setOnClickListener {
            val dialog = DailyPlanFilterDialogFragment()
            dialog.show(childFragmentManager, "PlanFilterBottomSheetDialog")
        }

        setupRecyclerView()

        setupObservers()

        callRequests()

        return root
    }

    private fun callRequests() {
        financeViewModel.fetchDailyPlan()
        financeViewModel.fetchBusinessProcessStatuses()
    }

    private fun setupObservers() {
        financeViewModel.dailyPlanResponse.observe(viewLifecycleOwner, { res ->
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
            val plans = financeViewModel.dailyPlanResponse.value?.data
            if (!plans.isNullOrEmpty()) {
                filterPlans(params)
            } else {
                filterViewModel.clearFilter()
            }
        })
    }

    private fun filterPlans(
        filterParams: PlanFilter = filterViewModel.getDefault()
    ) {
        changeTextsFilter(filterParams)
        var filteredPlans = mutableListOf<Plan>().apply { addAll(financeViewModel.dailyPlanResponse.value!!.data!!) }

        val dtf: DateTimeFormatter = DateTimeFormat.forPattern("dd.MM.yyyy")

        // Filter by search
        when (filterParams.selectedSearchBy) {
            0 -> {
                filteredPlans = filteredPlans.filter { it.contractNumber.toString().indexOf(filterParams.query) >= 0 }.toMutableList()
            }
            1 -> {
                val conditions = ArrayList<(Plan) -> Boolean>()
                filterParams.query.toLowerCase(Locale.ROOT).split(" ").map {
                    conditions.add { plan ->
                        (plan.customerLastname + " " + plan.customerFirstname + " " + plan.customerMiddlename).toLowerCase(
                            Locale.ROOT
                        ).indexOf(
                            it
                        ) >= 0
                    }
                }
                filteredPlans = filteredPlans.filter { candidate -> conditions.all { it(candidate) } }.toMutableList()
            }
        }

        // Filter by business process status
        filteredPlans = when (filterParams.selectedStatusFilter) {
            1 -> filteredPlans.filter { it.planBusinessProcessId == 1L }.toMutableList()
            2 -> filteredPlans.filter { it.planBusinessProcessId == 2L }.toMutableList()
            else -> filteredPlans
        }

        // Sort by selected parameter
        when (filterParams.selectedSortFilter) {
            0 -> filteredPlans.sortBy { dtf.parseLocalDate(it.nextPaymentDate) }
            1 -> filteredPlans.sortBy { dtf.parseLocalDate(it.contractDate) }
            2 -> filteredPlans.sortBy { it.customerLastname + " " + it.customerFirstname + " " + it.customerMiddlename }
        }

        // Filter problematic plans
        filteredPlans = filteredPlans.filter { it.problem == filterParams.problematic }.toMutableList()

        // Change recyclerView
        plansAdapter.setData(filteredPlans)

        val allOverdueDays =
            "(<font color=#DF1010>${filteredPlans.count { it.paymentOverDueDays!! > 0 }}</font>, " +
                    "<font color=#FFC107>${filteredPlans.count { it.paymentOverDueDays!! == 0 }}</font>, " +
                    "<font color=#4CAF50>${filteredPlans.count { it.paymentOverDueDays!! < 0 }}</font>)"

        binding.allOverdueDays.text = Html.fromHtml(allOverdueDays)
        binding.quantityOfList = filteredPlans.size
    }

    private fun changeTextsFilter(filterParams: PlanFilter) {
        val selectedStatusFilter = filterParams.selectedStatusFilter.toLong()
        val filterSortParams = arrayListOf(
            getString(R.string.payment_date),
            getString(R.string.contract_date),
            getString(R.string.fullname)
        )
        binding.sort = filterSortParams[filterParams.selectedSortFilter]
        binding.status = if (selectedStatusFilter == 0L) {
            getString(R.string.all)
        } else {
            financeViewModel.businessProcessStatusesResponse.value?.data?.find { it.id == selectedStatusFilter }?.name
        }
        binding.searchBySn = when (filterParams.selectedSearchBy) {
            0 -> if (filterParams.query.isNotBlank()) "${getString(R.string.cn)} = ${filterParams.query}" else ""
            1 -> if (filterParams.query.isNotBlank()) "${getString(R.string.fullname)} = ${filterParams.query}" else ""
            else -> ""
        }
        binding.problematic.isVisible = filterParams.problematic
    }

    private fun explainAboutColors(view: View) {
        MaterialAlertDialogBuilder(requireActivity())
            .setView(R.layout.explanation_about_colors)
            .setTitle(resources.getString(R.string.explanation_about_colors))
            .show()
    }

    private fun setupRecyclerView() {
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = plansAdapter
        binding.recyclerView.isNestedScrollingEnabled = false
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onRefresh() {
        sharedViewModel.setLoadingType(LoadingType.SWIPE_REFRESH)
        callRequests()
    }
}