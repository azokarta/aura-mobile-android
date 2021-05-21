package kz.aura.merp.employee.ui.fragment.finance

import android.content.Intent
import android.os.Bundle
import android.text.Html
import android.view.*
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.messaging.FirebaseMessaging
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import kz.aura.merp.employee.R
import kz.aura.merp.employee.adapter.PlanAdapter
import kz.aura.merp.employee.databinding.FragmentMonthlyPlanBinding
import kz.aura.merp.employee.model.Plan
import kz.aura.merp.employee.ui.activity.SettingsActivity
import kz.aura.merp.employee.ui.dialog.PlanFilterDialogFragment
import kz.aura.merp.employee.ui.dialog.TimePickerFragment
import kz.aura.merp.employee.util.NetworkResult
import kz.aura.merp.employee.util.ProgressDialog
import kz.aura.merp.employee.util.declareErrorByStatus
import kz.aura.merp.employee.util.verifyAvailableNetwork
import kz.aura.merp.employee.viewmodel.FinanceViewModel
import kz.aura.merp.employee.viewmodel.PlanFilterViewModel
import kz.aura.merp.employee.viewmodel.SharedViewModel
import org.joda.time.DateTime
import org.joda.time.DateTimeComparator
import org.joda.time.Duration
import org.joda.time.format.DateTimeFormat
import org.joda.time.format.DateTimeFormatter
import org.joda.time.format.DateTimeParser
import java.util.*
import kotlin.collections.ArrayList

@AndroidEntryPoint
class MonthlyPlanFragment : Fragment(), PlanAdapter.OnClickListener, TimePickerFragment.TimePickerListener {

    private val mFinanceViewModel: FinanceViewModel by activityViewModels()
    private val mFilterViewModel: PlanFilterViewModel by activityViewModels()
    private lateinit var mSharedViewModel: SharedViewModel
    private val plansAdapter: PlanAdapter by lazy { PlanAdapter(this) }
    private var _binding: FragmentMonthlyPlanBinding? = null
    private val binding get() = _binding!!
    private var updateTime: DateTime? = null
    private lateinit var progressDialog: ProgressDialog
    private var clickedContractId: Long? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mSharedViewModel = ViewModelProvider(requireActivity()).get(SharedViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMonthlyPlanBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        binding.mSharedViewModel = mSharedViewModel

        // Initialize Loading Dialog
        progressDialog = ProgressDialog(requireContext())

        // Setup RecyclerView
        setupRecyclerView()

        // If network is disconnected and user clicks restart, get data again
        binding.networkDisconnected.restart.setOnClickListener {
            if (verifyAvailableNetwork(requireContext())) {
                mFinanceViewModel.fetchPlans() // fetch clients
                binding.networkDisconnected.root.isVisible = false
            }
        }

        binding.filterList.setOnClickListener {
            val dialog = PlanFilterDialogFragment()
            dialog.show(childFragmentManager, "PlanFilterBottomSheetDialog")
        }
        binding.clearFilter.setOnClickListener {
            mFilterViewModel.clearFilter()
        }
        binding.explanationAboutColors.setOnClickListener(::explainAboutColors)

        observeLiveData()

        callRequests()

        // Receive token of FCM
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (!task.isSuccessful) {
                println("Fetching FCM registration token failed ${task.exception}")
            }

            // Get new FCM registration token
            val token = task.result
            println(token)
        }

        setMinuteForUpdate()

        binding.swiperefresh.setOnRefreshListener {
            updatePlans()
        }

        return binding.root
    }

    private fun callRequests() {
        val plans = mFinanceViewModel.plansResponse.value?.data
        val businessProcesses = mFinanceViewModel.plansResponse.value?.data

        if (plans.isNullOrEmpty()) {
            mFinanceViewModel.fetchPlans()
        }
        if (businessProcesses.isNullOrEmpty()) {
            mFinanceViewModel.fetchBusinessProcessStatuses()
        }
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
            mFinanceViewModel.createDailyPlan(it, "$hour:$minute")
        }
    }

    private fun observeLiveData() {
        mFinanceViewModel.plansResponse.observe(viewLifecycleOwner, { res ->
            when (res) {
                is NetworkResult.Success -> {
                    mSharedViewModel.hideLoading(res.data.isNullOrEmpty())
                    filterPlans()
                }
                is NetworkResult.Loading -> {
                    mSharedViewModel.showLoading()
                }
                is NetworkResult.Error -> {
                    mSharedViewModel.hideLoading(res.data.isNullOrEmpty())
                    checkError(res)
                }
            }
        })
        mFilterViewModel.filterParams.observe(viewLifecycleOwner, { params ->
            filterPlans(
                params.query,
                params.selectedSearchBy,
                params.selectedStatusFilter,
                params.selectedSortFilter,
                params.problematic
            )
        })
        mFinanceViewModel.createDailyPlanResponse.observe(viewLifecycleOwner, { res ->
            when (res) {
                is NetworkResult.Success -> {
                    if (res.data == true) {
                        mFinanceViewModel.createDailyPlanResponse.value = NetworkResult.Success(false)
                        progressDialog.hideLoading()
                        showSnackbar(binding.recyclerView)
                        mFinanceViewModel.fetchDailyPlan()
                    }
                }
                is NetworkResult.Loading -> progressDialog.showLoading()
                is NetworkResult.Error -> {
                    progressDialog.hideLoading()
                    declareErrorByStatus(res.message, res.status, requireContext())
                }
            }
        })
    }

    private fun changeTextsFilter(selectedSortFilter: Int, selectedStatusFilter: Int, query: String, selectedSearchBy: Int) {
        val filterSortParams = arrayListOf(
            getString(R.string.paymentDate),
            getString(R.string.contract_date),
            getString(R.string.fullName)
        )
        binding.sort = filterSortParams[selectedSortFilter]
        binding.status = mFinanceViewModel.businessProcessStatusesResponse.value?.data?.find { it.id == selectedStatusFilter.toLong() }?.name
        binding.searchBySn = when (selectedSearchBy) {
            0 -> if (query.isNotBlank()) "${getString(R.string.cn)} = $query" else ""
            1 -> if (query.isNotBlank()) "${getString(R.string.fullName)} = $query" else ""
            else -> ""
        }
        binding.executePendingBindings()
    }

    private fun filterPlans(query: String = "", selectedSearchBy: Int = 0, selectedStatusFilter: Int = 0, selectedSortFilter: Int = 0, problematic: Boolean = false) {
        changeTextsFilter(selectedSortFilter, selectedStatusFilter, query, selectedSearchBy)
        val filteredPlans = arrayListOf<Plan>().apply { addAll(mFinanceViewModel.plansResponse.value!!.data!!) }

        val dtf: DateTimeFormatter = DateTimeFormat.forPattern("dd.MM.yyyy")

        // Filter by search
        when (selectedSearchBy) {
            0 -> {
                val filterByCN =
                    filteredPlans.filter { it.contractNumber.toString().indexOf(query) >= 0 }
                filteredPlans.clear()
                filteredPlans.addAll(filterByCN)
            }
            1 -> {
                val conditions = ArrayList<(Plan) -> Boolean>()
                query.toLowerCase(Locale.ROOT).split(" ").map {
                    conditions.add { plan ->
                        (plan.customerLastname + " " + plan.customerFirstname + " " + plan.customerMiddlename).toLowerCase(
                            Locale.ROOT
                        ).indexOf(
                            it
                        ) >= 0
                    }
                }
                val filterByFullName = filteredPlans.filter { candidate -> conditions.all { it(candidate) } }

                filteredPlans.clear()
                filteredPlans.addAll(filterByFullName)
            }
        }

        // Filter by business process status
        when (selectedStatusFilter) {
            0 -> {
                val filterByStatus = filteredPlans.filter { it.planBusinessProcessId == null && it.planResultId == null }
                filteredPlans.clear()
                filteredPlans.addAll(filterByStatus)
            }
            1 -> {
                val filterByStatus = filteredPlans.filter { it.planBusinessProcessId == 1L && it.planResultId == null }
                filteredPlans.clear()
                filteredPlans.addAll(filterByStatus)
            }
            2 -> {
                val filterByStatus = filteredPlans.filter { it.planBusinessProcessId == 2L && it.planResultId == null }
                filteredPlans.clear()
                filteredPlans.addAll(filterByStatus)
            }
        }

        // Sort by selected parameter
        when (selectedSortFilter) {
            0 -> filteredPlans.sortBy { dtf.parseLocalDate(it.nextPaymentDate) }
            1 -> filteredPlans.sortBy { dtf.parseLocalDate(it.contractDate) }
            2 -> filteredPlans.sortBy { it.customerLastname + " " + it.customerFirstname + " " + it.customerMiddlename }
        }

        // Filter problematic plans
        val filterByProblematic = filteredPlans.filter { it.problem == problematic }
        filteredPlans.clear()
        filteredPlans.addAll(filterByProblematic)

        val allOverdueDays = "(<font color=#DF1010>${filteredPlans.count { it.paymentOverDueDays!! > 0 }}</font>, " +
                "<font color=#FFC107>${filteredPlans.count { it.paymentOverDueDays!! == 0 }}</font>, " +
                "<font color=#4CAF50>${filteredPlans.count { it.paymentOverDueDays!! < 0 }}</font>)"

        // Change recyclerView
        plansAdapter.setData(filteredPlans)

        binding.quantityOfList = filteredPlans.size
        binding.allOverdueDays.text = Html.fromHtml(allOverdueDays)
        binding.problematic.isVisible = problematic
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

    private fun updatePlans() {
        if (updateTime!!.isBeforeNow) {
            mFinanceViewModel.fetchPlans()
            setMinuteForUpdate()
        } else {
            binding.swiperefresh.isRefreshing = false
            val currentTime = DateTime.now()
            val leftTime = updateTime!!.minusMinutes(currentTime.minuteOfHour).minusSeconds(currentTime.secondOfMinute)
            println(leftTime)
//            println("UpdateTime: 1) Minute: ${updateTime!!.minuteOfHour} 2) Second: ${updateTime!!.secondOfMinute}")
//            println("leftTime: 1) Minute: ${leftTime.minuteOfHour} 2) Second: ${leftTime.secondOfMinute}")
            Toast.makeText(
                requireContext(),
                getString(R.string.left)+" ${leftTime.minuteOfHour} min. ${leftTime.secondOfMinute} sec.",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    private fun <T> checkError(res: NetworkResult.Error<T>) {
        if (!verifyAvailableNetwork(requireContext())) {
            binding.networkDisconnected.root.isVisible = true
            binding.recyclerView.isVisible = false
        } else {
            declareErrorByStatus(res.message, res.status, requireContext())
        }
    }

    private fun getData(): Plan? {
        val pref = PreferenceManager.getDefaultSharedPreferences(requireContext())
        val client = pref.getString("data", "")
        return if (client != "") {
            val obj = Gson().fromJson(client, Plan::class.java)
            if (obj.contractId != 0L) obj else null
        } else {
            null
        }
    }

    private fun showSnackbar(view: View) = Snackbar.make(
        view,
        R.string.successfullySaved,
        Snackbar.LENGTH_SHORT
    ).show()

    private fun removeData() {
        val pref = PreferenceManager.getDefaultSharedPreferences(requireContext())
        val editor = pref.edit()
        editor.remove("data")
        editor.apply()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}