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
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kz.aura.merp.employee.R
import kz.aura.merp.employee.adapter.PlanAdapter
import kz.aura.merp.employee.databinding.FragmentMonthlyPlanBinding
import kz.aura.merp.employee.model.Plan
import kz.aura.merp.employee.model.PlanFilter
import kz.aura.merp.employee.ui.dialog.MonthlyPlanFilterDialogFragment
import kz.aura.merp.employee.ui.dialog.TimePickerFragment
import kz.aura.merp.employee.util.*
import kz.aura.merp.employee.viewmodel.FinanceViewModel
import kz.aura.merp.employee.viewmodel.PlanFilterViewModel
import kz.aura.merp.employee.viewmodel.SharedViewModel
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import org.joda.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.ArrayList

@AndroidEntryPoint
class MonthlyPlanFragment : Fragment(), PlanAdapter.OnClickListener,
    TimePickerFragment.TimePickerListener, SwipeRefreshLayout.OnRefreshListener {

    private lateinit var financeViewModel: FinanceViewModel
    private lateinit var filterViewModel: PlanFilterViewModel
    private lateinit var sharedViewModel: SharedViewModel
    private val plansAdapter: PlanAdapter by lazy { PlanAdapter(this) }
    private var _binding: FragmentMonthlyPlanBinding? = null
    private val binding get() = _binding!!
    private var updateTime: DateTime? = null
    private lateinit var progressDialog: ProgressDialog
    private var clickedContractId: Long? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        filterViewModel = ViewModelProvider(requireActivity()).get(PlanFilterViewModel::class.java)
        sharedViewModel = ViewModelProvider(this).get(SharedViewModel::class.java)
        financeViewModel = ViewModelProvider(this).get(FinanceViewModel::class.java)

        _binding = FragmentMonthlyPlanBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        binding.sharedViewModel = sharedViewModel
        val root: View = binding.root

        // Initialize Loading Dialog
        progressDialog = ProgressDialog(requireContext())

        // Setup RecyclerView
        setupRecyclerView()

        binding.swipeRefresh.setOnRefreshListener(this)

        binding.filterList.setOnClickListener {
            val dialog = MonthlyPlanFilterDialogFragment()
            dialog.show(childFragmentManager, "PlanFilterBottomSheetDialog")
        }
        binding.clearFilter.setOnClickListener {
            filterViewModel.clearFilter()
        }
        binding.explanationAboutColors.setOnClickListener(::explainAboutColors)

        observeLiveData()

        callRequests()

        setMinuteForUpdate()

        return root
    }

    private fun getTokenFromFirebase() {
        //        // Receive token of FCM
//        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
//            if (!task.isSuccessful) {
//                println("Fetching FCM registration token failed ${task.exception}")
//            }
//
//            // Get new FCM registration token
//            val token = task.result
//            println(token)
//        }
    }

    private fun callRequests() {
        financeViewModel.fetchPlans()
    }

    override fun sendToDailyPlan(contractId: Long) {
        clickedContractId = contractId
        val timePicker = TimePickerFragment(this)
        timePicker.show(childFragmentManager)
    }

    private fun setupRecyclerView() {
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = plansAdapter
        binding.recyclerView.isNestedScrollingEnabled = false
    }

    override fun selectedTime(hour: Int, minute: Int) {
        clickedContractId?.let {
            financeViewModel.createDailyPlan(it, "$hour:$minute")
        }
    }

    private fun observeLiveData() {
        financeViewModel.plansResponse.observe(viewLifecycleOwner, { res ->
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
            val plans = financeViewModel.plansResponse.value?.data
            if (!plans.isNullOrEmpty()) {
                filterPlans(params)
            } else {
                filterViewModel.clearFilter()
            }
        })
        financeViewModel.createDailyPlanResponse.observe(viewLifecycleOwner, { res ->
            when (res) {
                is NetworkResult.Success -> {
                    financeViewModel.createDailyPlanResponse.postValue(null)
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
        var filteredPlans = mutableListOf<Plan>().apply { addAll(financeViewModel.plansResponse.value!!.data!!) }

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

    private fun explainAboutColors(view: View) {
        MaterialAlertDialogBuilder(requireActivity())
            .setView(R.layout.explanation_about_colors)
            .setTitle(resources.getString(R.string.explanation_about_colors))
            .show()
    }

    private fun setMinuteForUpdate() {
        updateTime = DateTime.now().plusMinutes(3)
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