package kz.aura.merp.employee.ui.activity

import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.view.*
import android.widget.ArrayAdapter
import android.widget.Button
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.chip.Chip
import com.google.firebase.messaging.FirebaseMessaging
import com.google.gson.Gson
import kz.aura.merp.employee.R
import kz.aura.merp.employee.adapter.PlanAdapter
import kz.aura.merp.employee.viewmodel.SharedViewModel
import kz.aura.merp.employee.data.model.BusinessProcessStatus
import kz.aura.merp.employee.data.model.Plan
import kz.aura.merp.employee.viewmodel.FinanceViewModel
import kz.aura.merp.employee.databinding.ActivityFinanceAgentBinding
import kz.aura.merp.employee.databinding.PlanFilterBottomSheetBinding
import kz.aura.merp.employee.util.*
import java.util.*
import kotlin.collections.ArrayList


class FinanceAgentActivity : AppCompatActivity() {

    private val mFinanceViewModel: FinanceViewModel by viewModels()
    private val mSharedViewModel: SharedViewModel by viewModels()
    private val finAdapter: PlanAdapter by lazy { PlanAdapter() }
    private lateinit var binding: ActivityFinanceAgentBinding
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        LanguageHelper.updateLanguage(this)

        // Data binding
        binding = ActivityFinanceAgentBinding.inflate(layoutInflater)
        binding.lifecycleOwner = this
        binding.mSharedViewModel = mSharedViewModel
        val view = binding.root
        setContentView(view)

        // Toolbar
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.title = getString(R.string.finAgent)
        supportActionBar?.subtitle = getStaff(this)?.username

        // Turn off screenshot
        window.setFlags(
            WindowManager.LayoutParams.FLAG_SECURE,
            WindowManager.LayoutParams.FLAG_SECURE
        )

        setupFilterResources()

        // Setup RecyclerView
        setupRecyclerView()

        // If network is disconnected and user clicks restart, get data again
        findViewById<Button>(R.id.restart).setOnClickListener {
            if (verifyAvailableNetwork(this)) {
                mFinanceViewModel.fetchPlans() // fetch clients
                binding.progressBar.isVisible = true
                binding.recyclerView.isVisible = true
                findViewById<ConstraintLayout>(R.id.networkDisconnected).visibility = View.GONE
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

//        Intent(this, BackgroundService::class.java).also { intent ->
//            intent.putExtra("link", Link.FINANCE)
//            startService(intent)
//        }
    }

    private fun setupRecyclerView() {
        binding.recyclerView.layoutManager = LinearLayoutManager(applicationContext)
        binding.recyclerView.adapter = finAdapter
        binding.recyclerView.isNestedScrollingEnabled = false
    }

    private fun setupFilterResources() {
        filterSortParams = arrayListOf(
            getString(R.string.date),
            getString(R.string.fullName)
        )
    }

    private fun observeLiveData() {
        mFinanceViewModel.plansResponse.observe(this, { res ->
            when (res) {
                is NetworkResult.Success -> {
                    mSharedViewModel.hideLoading(res.data!!.isEmpty())
                    filterPlans()
                }
                is NetworkResult.Loading -> mSharedViewModel.showLoading()
                is NetworkResult.Error -> {
                    mSharedViewModel.hideLoading(res.data!!.isEmpty())
                    declareErrorByStatus(res.message, res.status, this)
                }
            }
        })

        mFinanceViewModel.businessProcessStatusesResponse.observe(this, { res ->
            when (res) {
                is NetworkResult.Success -> {
                    this.businessProcessStatuses.add(BusinessProcessStatus(0, getString(R.string._new), "", "", "", ""))
                    this.businessProcessStatuses.add(BusinessProcessStatus(3, getString(R.string.completed), "", "", "", ""))
                    this.businessProcessStatuses.addAll(res.data!!)
                    setupFilterBottomSheet()
                }
                is NetworkResult.Loading -> {}
                is NetworkResult.Error -> {
                    declareErrorByStatus(res.message, res.status, this)
                }
            }
        })

        Permissions(this, this).enableLocation()
    }

    private fun setupFilterBottomSheet() {
        bottomSheetDialog = BottomSheetDialog(this, R.style.BottomSheetDialogTheme)
        bottomSheetView = LayoutInflater.from(this)
            .inflate(R.layout.plan_filter_bottom_sheet, findViewById(R.id.bottomSheetContainer))
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
            searchQuery = bottomSheetBinding.search.text.toString()
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

        // Initialize search params
        val items = listOf(getString(R.string.cn), getString(R.string.fullName))
        val adapter = ArrayAdapter(this, R.layout.list_item, items)
        bottomSheetBinding.searchByEditText.setText(items[0])
        bottomSheetBinding.searchByEditText.setAdapter(adapter)

        bottomSheetBinding.searchByEditText.setOnItemClickListener { _, _, i, l ->
            selectedSearchBy = i
            when (i) {
                0 -> bottomSheetBinding.search.inputType = InputType.TYPE_CLASS_NUMBER
                1 -> bottomSheetBinding.search.inputType = InputType.TYPE_CLASS_TEXT
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
            3 -> {
                val filterByStatus = filteredPlans.filter { it.planResultId != null }
                filteredPlans.clear()
                filteredPlans.addAll(filterByStatus)
            }
        }
        // Sort by selected parameter
        when (selectedSortFilter) {
            1 -> filteredPlans.sortBy { it.customerLastname + it.customerFirstname + it.customerMiddlename }
            2 -> filteredPlans.sortBy { it.planBusinessProcessId }
        }

        // Filter problematic plans
        val filterByProblematic = filteredPlans.filter { it.problem == problematic }
        filteredPlans.clear()
        filteredPlans.addAll(filterByProblematic)

        // Change recyclerView
        finAdapter.setData(filteredPlans)

        binding.quantityOfList = filteredPlans.size
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

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val menuInflater = menuInflater
        menuInflater.inflate(R.menu.menu, menu)

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_settings -> {
                val intent = Intent(applicationContext, SettingsActivity::class.java)
                startActivity(intent)
            }
        }

        return super.onOptionsItemSelected(item)
    }

//    private fun checkError(error: Any) {
//        binding.progressBar.visibility = View.INVISIBLE // hide progress bar
//        if (!verifyAvailableNetwork(this)) {
//            findViewById<ConstraintLayout>(R.id.networkDisconnected).visibility = View.VISIBLE
//            binding.recyclerView.visibility = View.INVISIBLE
//            binding.filterLayout.visibility = View.GONE
//        } else {
//            exceptionHandler(error, this) // Show error
//        }
//    }

    private fun getData(): Plan? {
        val pref = PreferenceManager.getDefaultSharedPreferences(this)
        val client = pref.getString("data", "")
        return if (client != "") {
            val obj = Gson().fromJson(client, Plan::class.java)
            if (obj.contractId != 0L) obj else null
        } else {
            null
        }
    }

    private fun createChip(title: String, id: Int): Chip {
        val chip = Chip(this)
        chip.text = title
        chip.id = id
        chip.setChipBackgroundColorResource(R.color.gray)
        chip.checkedIcon = ContextCompat.getDrawable(this, R.drawable.ic_baseline_check_24)
        chip.isCheckable = true
        chip.setTextColor(resources.getColor(R.color.colorBlack))
        return chip
    }

    private fun removeData() {
        val pref = PreferenceManager.getDefaultSharedPreferences(this)
        val editor = pref.edit()
        editor.remove("data")
        editor.apply()
    }

}