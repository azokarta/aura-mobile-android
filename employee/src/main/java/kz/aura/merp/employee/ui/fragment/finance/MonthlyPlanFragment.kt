package kz.aura.merp.employee.ui.fragment.finance

import android.os.Bundle
import android.text.Html
import android.text.InputType
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.chip.Chip
import com.google.firebase.messaging.FirebaseMessaging
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import kz.aura.merp.employee.R
import kz.aura.merp.employee.adapter.PlanAdapter
import kz.aura.merp.employee.model.BusinessProcessStatus
import kz.aura.merp.employee.model.Plan
import kz.aura.merp.employee.databinding.FragmentMonthlyPlanBinding
import kz.aura.merp.employee.databinding.PlanFilterBottomSheetBinding
import kz.aura.merp.employee.util.NetworkResult
import kz.aura.merp.employee.util.declareErrorByStatus
import kz.aura.merp.employee.util.verifyAvailableNetwork
import kz.aura.merp.employee.viewmodel.FinanceViewModel
import kz.aura.merp.employee.viewmodel.SharedViewModel
import java.util.*
import kotlin.collections.ArrayList

@AndroidEntryPoint
class MonthlyPlanFragment : Fragment() {

    private val mFinanceViewModel: FinanceViewModel by activityViewModels()
    private val plansAdapter: PlanAdapter by lazy { PlanAdapter() }
    private var _binding: FragmentMonthlyPlanBinding? = null
    private val binding get() = _binding!!
    private lateinit var filterSortParams: ArrayList<String>
    private lateinit var bottomSheetDialog: BottomSheetDialog
    private lateinit var bottomSheetView: View
    private lateinit var bottomSheetBinding: PlanFilterBottomSheetBinding
    private var selectedStatusFilter: Int = 0
    private var selectedSortFilter: Int = 0
    private val businessProcessStatuses: ArrayList<BusinessProcessStatus> = arrayListOf()
    private var selectedSearchBy: Int = 0
    private var searchQuery: String = ""
    private var problematic: Boolean = false
    private var nextTime: Date? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMonthlyPlanBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this

        setupFilterResources()

        // Setup RecyclerView
        setupRecyclerView()

        // If network is disconnected and user clicks restart, get data again
        binding.networkDisconnected.restart.setOnClickListener {
            if (verifyAvailableNetwork(requireContext())) {
                mFinanceViewModel.fetchPlans() // fetch clients
                binding.progressBar.isVisible = true
                binding.recyclerView.isVisible = true
                binding.networkDisconnected.root.isVisible = false
            }
        }

        observeLiveData()

        mFinanceViewModel.fetchPlans()
        mFinanceViewModel.fetchBusinessProcessStatuses()

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

        return binding.root
    }

    private fun setupRecyclerView() {
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = plansAdapter
        binding.recyclerView.isNestedScrollingEnabled = false
    }

    private fun setupFilterResources() {
        filterSortParams = arrayListOf(
            getString(R.string.date),
            getString(R.string.fullName)
        )
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

    private fun observeLiveData() {
        mFinanceViewModel.plansResponse.observe(viewLifecycleOwner, { res ->
            when (res) {
                is NetworkResult.Success -> {
                    showLoadingOrNoData(false, res.data.isNullOrEmpty())
                    filterPlans()
                }
                is NetworkResult.Loading -> showLoadingOrNoData(true)
                is NetworkResult.Error -> {
                    showLoadingOrNoData(false, res.data.isNullOrEmpty())
                    checkError(res)
                }
            }
        })
        mFinanceViewModel.businessProcessStatusesResponse.observe(viewLifecycleOwner, { res ->
            when (res) {
                is NetworkResult.Success -> {
                    this.businessProcessStatuses.add(
                        BusinessProcessStatus(
                            0,
                            getString(R.string._new),
                            "",
                            "",
                            "",
                            ""
                        )
                    )
                    this.businessProcessStatuses.addAll(res.data!!)
                    setupFilterBottomSheet()
                }
                is NetworkResult.Loading -> { }
                is NetworkResult.Error -> checkError(res)
            }
        })
    }

    private fun setupFilterBottomSheet() {
        bottomSheetDialog = BottomSheetDialog(requireContext(), R.style.BottomSheetDialogTheme)
        bottomSheetView = LayoutInflater.from(requireContext())
            .inflate(R.layout.plan_filter_bottom_sheet, view?.findViewById(R.id.bottomSheetContainer))
        bottomSheetBinding = PlanFilterBottomSheetBinding.bind(bottomSheetView)

        // Add chips
        for (process in businessProcessStatuses) {
            bottomSheetBinding.statusesChipGroup.addView(
                createChip(
                    process.name,
                    process.id.toInt()
                )
            )
        }
        for ((idx, value) in filterSortParams.withIndex()) {
            bottomSheetBinding.sortChipGroup.addView(createChip(value, idx))
        }

        changeTextsFilter()

        checkChips()

        // Init listeners
        bottomSheetBinding.apply.setOnClickListener {
            searchQuery = bottomSheetBinding.search.editText?.text.toString()
            selectedSortFilter = bottomSheetBinding.sortChipGroup.checkedChipId
            selectedStatusFilter = bottomSheetBinding.statusesChipGroup.checkedChipId
            binding.problematic.visibility = if (problematic) View.VISIBLE else View.INVISIBLE
            filterPlans()
            changeTextsFilter()
            bottomSheetDialog.dismiss()
        }
        binding.filterList.setOnClickListener {
            checkChips()
            bottomSheetDialog.show()
        }
        bottomSheetBinding.problematic.setOnCheckedChangeListener { _, bool ->
            problematic = bool
        }
        bottomSheetBinding.search.setEndIconOnClickListener {
            bottomSheetBinding.search.editText?.setText("")
        }


        // Initialize search params
        val items = listOf(getString(R.string.cn), getString(R.string.fullName))
        val adapter = ArrayAdapter(requireContext(), R.layout.list_item, items)
        bottomSheetBinding.searchByEditText.setText(items[0])
        bottomSheetBinding.searchByEditText.setAdapter(adapter)

        bottomSheetBinding.searchByEditText.setOnItemClickListener { _, _, i, _ ->
            selectedSearchBy = i
            when (i) {
                0 -> bottomSheetBinding.search.editText?.inputType = InputType.TYPE_CLASS_NUMBER
                1 -> bottomSheetBinding.search.editText?.inputType = InputType.TYPE_CLASS_TEXT
            }
        }

        // Init content view
        bottomSheetDialog.setContentView(bottomSheetBinding.root)

    }

    private fun checkChips() {
        bottomSheetBinding.statusesChipGroup.check(selectedStatusFilter)
        bottomSheetBinding.sortChipGroup.check(selectedSortFilter)
    }

    private fun changeTextsFilter() {
        binding.sort = filterSortParams[selectedSortFilter]
        binding.status =
            businessProcessStatuses.find { it.id == selectedStatusFilter.toLong() }?.name
        binding.searchBySn = when (selectedSearchBy) {
            0 -> if (searchQuery.isNotBlank()) "${getString(R.string.cn)} = $searchQuery" else ""
            1 -> if (searchQuery.isNotBlank()) "${getString(R.string.fullName)} = $searchQuery" else ""
            else -> ""
        }
        binding.executePendingBindings()
    }

    private fun filterPlans() {
        val filteredPlans = arrayListOf<Plan>().apply { addAll(mFinanceViewModel.plansResponse.value!!.data!!) }

        // Filter by search
        when (selectedSearchBy) {
            0 -> {
                val filterByCN =
                    filteredPlans.filter { it.contractNumber.toString().indexOf(searchQuery) >= 0 }
                filteredPlans.clear()
                filteredPlans.addAll(filterByCN)
            }
            1 -> {
                val conditions = ArrayList<(Plan) -> Boolean>()
                searchQuery.toLowerCase(Locale.ROOT).split(" ").map {
                    conditions.add { plan ->
                        (plan.customerLastname + " " + plan.customerFirstname + " " + plan.customerMiddlename).toLowerCase(
                            Locale.ROOT
                        ).indexOf(
                            it
                        ) >= 0
                    }
                }
                val filterByFullName =
                    filteredPlans.filter { candidate -> conditions.all { it(candidate) } }

                filteredPlans.clear()
                filteredPlans.addAll(filterByFullName)
            }
        }

        // Filter by business process status
        when (selectedStatusFilter) {
            0 -> {
                val filterByStatus =
                    filteredPlans.filter { it.planBusinessProcessId == null && it.planResultId == null }
                filteredPlans.clear()
                filteredPlans.addAll(filterByStatus)
            }
            1 -> {
                val filterByStatus =
                    filteredPlans.filter { it.planBusinessProcessId == 1L && it.planResultId == null }
                filteredPlans.clear()
                filteredPlans.addAll(filterByStatus)
            }
            2 -> {
                val filterByStatus =
                    filteredPlans.filter { it.planBusinessProcessId == 2L && it.planResultId == null }
                filteredPlans.clear()
                filteredPlans.addAll(filterByStatus)
            }
        }

        // Sort by selected parameter
        when (selectedSortFilter) {
            0 -> filteredPlans.sortBy { it.contractDate }
            1 -> filteredPlans.sortBy { it.customerLastname + it.customerFirstname + it.customerMiddlename }
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
    }

    override fun onResume() {
        super.onResume()
        val data = getData()
        if (data != null) {
            val changed = mFinanceViewModel.changeData(data)
            if (changed) {
                filterPlans()
            }
            removeData()
        }
    }

    private fun setMinuteForUpdate() {
        val calendar: Calendar = Calendar.getInstance()
        calendar.add(Calendar.MINUTE, 3)
        nextTime = calendar.time
    }

    private fun updatePlans() {
        if (nextTime!!.before(Calendar.getInstance().time)) {
            plansAdapter.clear()
            mFinanceViewModel.fetchPlans()
            setMinuteForUpdate()
        } else {
            val diff: Long = nextTime!!.time - Calendar.getInstance().time.time
            val diffMinutes = diff / (60 * 1000)
            val diffSeconds = diff / 1000

            Toast.makeText(
                requireContext(),
                getString(R.string.left)+" $diffMinutes min. $diffSeconds sec.",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    private fun <T> checkError(res: NetworkResult.Error<T>) {
        if (!verifyAvailableNetwork(requireContext())) {
            binding.networkDisconnected.root.isVisible = true
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

    private fun createChip(title: String, id: Int): Chip {
        val chip = Chip(requireContext())
        chip.text = title
        chip.id = id
        chip.setChipBackgroundColorResource(R.color.gray)
        chip.checkedIcon = ContextCompat.getDrawable(requireContext(), R.drawable.ic_baseline_check_24)
        chip.isCheckable = true
        chip.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
        return chip
    }

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