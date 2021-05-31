package kz.aura.merp.employee.ui.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.InputType
import android.view.*
import android.widget.ArrayAdapter
import android.widget.Button
import androidx.activity.viewModels
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.chip.Chip
import kz.aura.merp.employee.R
import kz.aura.merp.employee.adapter.ServiceApplicationAdapter
import kz.aura.merp.employee.viewmodel.SharedViewModel
import kz.aura.merp.employee.model.ServiceApplication
import kz.aura.merp.employee.viewmodel.MasterViewModel
import kz.aura.merp.employee.databinding.ActivityMasterBinding
import com.google.gson.Gson
import kz.aura.merp.employee.viewmodel.ReferenceViewModel
import kz.aura.merp.employee.databinding.ServiceApplicationFilterBottomSheetBinding
import kz.aura.merp.employee.util.*
import kotlin.collections.ArrayList

class MasterActivity : AppCompatActivity() {

    private val mMasterViewModel: MasterViewModel by viewModels()
    private val mSharedViewModel: SharedViewModel by viewModels()
    private val mReferenceViewModel: ReferenceViewModel by viewModels()
    private val serviceApplicationAdapter: ServiceApplicationAdapter by lazy { ServiceApplicationAdapter() }
    private lateinit var binding: ActivityMasterBinding
    private lateinit var filterSortParams: ArrayList<String>
    private lateinit var bottomSheetDialog: BottomSheetDialog
    private lateinit var bottomSheetView: View
    private lateinit var bottomSheetBinding: ServiceApplicationFilterBottomSheetBinding
    private var selectedStatusFilter: Int = 0
    private var selectedSortFilter: Int = 0
    private var selectedSearchBy: Int = 0
    private var searchQuery: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        LanguageHelper.updateLanguage(this)

        // Data binding
        binding = ActivityMasterBinding.inflate(layoutInflater)
        binding.lifecycleOwner = this
        binding.mSharedViewModel = mSharedViewModel
        val view = binding.root
        setContentView(view)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.title = getString(R.string.master)
//        supportActionBar?.subtitle = getStaff(this)?.username

        // Turn off screenshot
        window.setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE)

        setupFilterResources()

        Permissions(this, this).enableLocation()

        // Setup RecyclerView
        binding.recyclerView.layoutManager = LinearLayoutManager(applicationContext)
        binding.recyclerView.adapter = serviceApplicationAdapter


        observeLiveData()

        mMasterViewModel.fetchServiceApplications()

        setupFilterBottomSheet()
    }

    private fun observeLiveData() {
        mMasterViewModel.applicationsResponse.observe(this, { res ->
            when (res) {
                is NetworkResult.Success -> {
                    mSharedViewModel.hideLoading(res.data!!.isEmpty())
                    binding.filterLayout.visibility = View.VISIBLE
                    filterServiceApplications()
                }
                is NetworkResult.Loading -> mSharedViewModel.showLoading()
                is NetworkResult.Error -> {
                    mSharedViewModel.hideLoading(res.data!!.isEmpty())
//                    declareErrorByStatus(res.message, res.status, this)
                }
            }
        })
    }

    private fun setupFilterResources() {
        filterSortParams = arrayListOf(
            getString(R.string.date),
            getString(R.string.fullname)
        )
    }

    private fun setupFilterBottomSheet() {
        bottomSheetDialog = BottomSheetDialog(this, R.style.BottomSheetDialogTheme)
        bottomSheetView = LayoutInflater.from(this)
            .inflate(R.layout.service_application_filter_bottom_sheet, findViewById(R.id.bottomSheetContainer))
        bottomSheetBinding = ServiceApplicationFilterBottomSheetBinding.bind(bottomSheetView)

        // Add chips
//        for (process in businessProcessStatuses) {
//            bottomSheetBinding.statusesChipGroup.addView(
//                createChip(
//                    process.name,
//                    process.id.toInt()
//                )
//            )
//        }
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
            filterServiceApplications()
            changeTextsFilter()
            bottomSheetDialog.dismiss()
        }
        binding.filterList.setOnClickListener {
            checkChips()
            bottomSheetDialog.show()
        }

        // Initialize search params
        val items = listOf(getString(R.string.cn), getString(R.string.fullname), getString(R.string.serial_number))
        val adapter = ArrayAdapter(this, R.layout.list_item, items)
        bottomSheetBinding.searchByEditText.setText(items[0])
        bottomSheetBinding.searchByEditText.setAdapter(adapter)

        bottomSheetBinding.searchByEditText.setOnItemClickListener { _, _, i, _ ->
            selectedSearchBy = i
            when (i) {
                0 -> bottomSheetBinding.search.inputType = InputType.TYPE_CLASS_NUMBER
                1 -> bottomSheetBinding.search.inputType = InputType.TYPE_CLASS_TEXT
                2 -> bottomSheetBinding.search.inputType = InputType.TYPE_CLASS_NUMBER
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
//        binding.status = businessProcessStatuses.find { it.id == selectedStatusFilter.toLong() }?.name
        binding.searchBySn = when (selectedSearchBy) {
            0 -> if (searchQuery.isNotBlank()) "${getString(R.string.cn)} = $searchQuery" else ""
            1 -> if (searchQuery.isNotBlank()) "${getString(R.string.fullname)} = $searchQuery" else ""
            2 -> if (searchQuery.isNotBlank()) "${getString(R.string.serial_number)} = $searchQuery" else ""
            else -> ""
        }
        binding.executePendingBindings()
    }

    private fun filterServiceApplications() {
        val filteredApplications = arrayListOf<ServiceApplication>().apply { addAll(mMasterViewModel.applicationsResponse.value!!.data!!) }

        // Filter by search
        when (selectedSearchBy) {
            0 -> {
                val filterByCN = filteredApplications.filter { it.contractNumber.toString().indexOf(searchQuery) >= 0 }
                filteredApplications.clear()
                filteredApplications.addAll(filterByCN)
            }
            1 -> {
//                val conditions = ArrayList<(Plan) -> Boolean>()
//                searchQuery.toLowerCase(Locale.ROOT).split(" ").map {
//                    conditions.add { plan ->
//                        (plan.customerLastname + " " + plan.customerFirstname + " " + plan.customerMiddlename).toLowerCase(
//                            Locale.ROOT).indexOf(
//                            it
//                        ) >= 0
//                    }
//                }
//                val filterByFullName = filteredApplications.filter { candidate -> conditions.all { it(candidate) } }
//
//                filteredApplications.clear()
//                filteredApplications.addAll(filterByFullName)
            }
        }

        // Filter by business process status
        when (selectedStatusFilter) {
            0 -> {
//                val filterByStatus = filteredApplications.filter { it.planBusinessProcessId == null && it.planResultId == null }
//                filteredApplications.clear()
//                filteredApplications.addAll(filterByStatus)
            }
            1 -> {
//                val filterByStatus = filteredApplications.filter { it.planBusinessProcessId == 1L && it.planResultId == null }
//                filteredApplications.clear()
//                filteredApplications.addAll(filterByStatus)
            }
            2 -> {
//                val filterByStatus = filteredApplications.filter { it.planBusinessProcessId == 2L && it.planResultId == null }
//                filteredApplications.clear()
//                filteredApplications.addAll(filterByStatus)
            }
            3 -> {
//                val filterByStatus = filteredApplications.filter { it.planResultId != null }
//                filteredApplications.clear()
//                filteredApplications.addAll(filterByStatus)
            }
        }
        // Sort by selected parameter
        when (selectedSortFilter) {
            1 -> filteredApplications.sortBy { it.customerLastname + it.customerFirstname + it.customerMiddlename }
            2 -> filteredApplications.sortBy { it.applicationBusinessProcessId }
        }

        // Change recyclerView
        serviceApplicationAdapter.setData(filteredApplications)

        binding.quantityOfList = filteredApplications.size
        binding.executePendingBindings()
    }

    override fun onResume() {
        super.onResume()
        val data = getData()
        if (data != null) {
            val list = mMasterViewModel.changeData(data)
            list?.let { serviceApplicationAdapter.setData(it) }
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

    private fun checkError(error: Any) {
        binding.progressBar.visibility = View.INVISIBLE // hide progress bar
        if (!verifyAvailableNetwork(this)) {
//            findViewById<ConstraintLayout>(R.id.networkDisconnected).visibility = View.VISIBLE
            binding.recyclerView.visibility = View.INVISIBLE
            binding.filterLayout.visibility = View.GONE
        } else {
//            exceptionHandler(error, this) // Show error
        }
    }

    private fun getData(): ServiceApplication? {
        val pref = PreferenceManager.getDefaultSharedPreferences(this)
        val serviceApplication = pref.getString("data", "")
        return if (serviceApplication != "") {
            val obj = Gson().fromJson(serviceApplication, ServiceApplication::class.java)
            if (obj.applicationNumber != 0L) obj else null
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
        chip.setTextColor(resources.getColor(R.color.black))
        return chip
    }

    private fun removeData() {
        val pref = PreferenceManager.getDefaultSharedPreferences(this)
        val editor = pref.edit()
        editor.remove("data")
        editor.apply()
    }
}