package kz.aura.merp.employee.activity

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.chip.Chip
import com.google.gson.Gson
import kz.aura.merp.employee.R
import kz.aura.merp.employee.adapter.PlanAdapter
import kz.aura.merp.employee.data.SharedViewModel
import kz.aura.merp.employee.data.model.Plan
import kz.aura.merp.employee.data.viewmodel.FinanceViewModel
import kz.aura.merp.employee.data.viewmodel.ReferenceViewModel
import kz.aura.merp.employee.databinding.ActivityFinanceAgentBinding
import kz.aura.merp.employee.databinding.FilterBottomSheetBinding
import kz.aura.merp.employee.util.Helpers
import kz.aura.merp.employee.util.Helpers.exceptionHandler
import kz.aura.merp.employee.util.Helpers.verifyAvailableNetwork
import kz.aura.merp.employee.util.LanguageHelper
import kz.aura.merp.employee.util.Permissions


class FinanceAgentActivity : AppCompatActivity() {

    private val mFinanceViewModel: FinanceViewModel by viewModels()
    private val mSharedViewModel: SharedViewModel by viewModels()
    private val mReferenceViewModel: ReferenceViewModel by viewModels()
    private val finAdapter: PlanAdapter by lazy { PlanAdapter() }
    private var collectorId: Long? = null
    private lateinit var binding: ActivityFinanceAgentBinding
    private lateinit var filterStatuses: ArrayList<String>
    private lateinit var filterSortParams: ArrayList<String>
    private lateinit var bottomSheetDialog: BottomSheetDialog
    private lateinit var bottomSheetView: View
    private lateinit var bottomSheetBinding: FilterBottomSheetBinding
    private var selectedStatusFilter: Int = 0
    private var selectedSortFilter: Int = 0

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
        supportActionBar?.subtitle = Helpers.getStaff(this)?.username

        setupFilterResources()

        // Get collector id
        collectorId = Helpers.getStaffId(this)

        // Setup RecyclerView
        binding.recyclerView.layoutManager = LinearLayoutManager(applicationContext)
        binding.recyclerView.adapter = finAdapter
        binding.recyclerView.isNestedScrollingEnabled = false

        // Observe MutableLiveData
        mFinanceViewModel.plans.observe(this, { data ->
            binding.quantityOfList = data.size
            binding.executePendingBindings()
            mSharedViewModel.checkData(data)
            finAdapter.setData(data)
            binding.filterLayout.visibility = View.VISIBLE
        })

        Permissions(this, this).enableLocation()

        // Observe errors
        mFinanceViewModel.error.observe(this, { error ->
            checkError(error)
        })
        mReferenceViewModel.error.observe(this, { error ->
            checkError(error)
        })

        // If network is disconnected and user clicks restart, get data again
        findViewById<Button>(R.id.restart).setOnClickListener {
            if (verifyAvailableNetwork(this)) {
                mFinanceViewModel.fetchClients(collectorId!!) // fetch clients
                binding.progressBar.visibility = View.VISIBLE
                binding.recyclerView.visibility = View.VISIBLE
                findViewById<ConstraintLayout>(R.id.networkDisconnected).visibility = View.GONE
            }
        }

        // Fetch clients
        mFinanceViewModel.fetchClients(collectorId!!)

        setupFilterBottomSheet()
    }

    private fun setupFilterResources() {
        filterStatuses = arrayListOf(
            getString(R.string.all), getString(R.string.leaving), getString(
                R.string.gotToTheAddress
            ), getString(R.string.completed)
        )
        filterSortParams = arrayListOf(
            getString(R.string.date),
            getString(R.string.fullName),
            getString(R.string.status)
        )
    }

    private fun setupFilterBottomSheet() {
        bottomSheetDialog = BottomSheetDialog(this, R.style.BottomSheetDialogTheme)
        bottomSheetView = LayoutInflater.from(this).inflate(R.layout.filter_bottom_sheet, findViewById(R.id.bottomSheetContainer))
        bottomSheetBinding = FilterBottomSheetBinding.bind(bottomSheetView)

        // Add chips
        for ((idx, value) in filterStatuses.withIndex()) {
            bottomSheetBinding.statusesChipGroup.addView(createChip(value, idx))
        }
        for ((idx, value) in filterSortParams.withIndex()) {
            bottomSheetBinding.sortChipGroup.addView(createChip(value, idx))
        }

        changeTextsFilter()

        checkChips()

        // Init listeners
        bottomSheetBinding.apply.setOnClickListener {
            selectedSortFilter = bottomSheetBinding.sortChipGroup.checkedChipId
            selectedStatusFilter = bottomSheetBinding.statusesChipGroup.checkedChipId
            filterPlans()
            changeTextsFilter()
            bottomSheetDialog.dismiss()
        }
        binding.filterList.setOnClickListener {
            checkChips()
            bottomSheetDialog.show()
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
        binding.status = filterStatuses[selectedStatusFilter]
        binding.executePendingBindings()
    }

    private fun filterPlans() {
        when (selectedSortFilter) {
            0 -> mFinanceViewModel.plans.value?.let { finAdapter.setData(it) }
            1 -> finAdapter.setData(mFinanceViewModel.plans.value!!.sortedBy { it.firstname+it.middlename+it.lastname }.toCollection(ArrayList()))
        }
    }

    override fun onResume() {
        super.onResume()
        val data = getData()
        if (data != null) {
            val list = mFinanceViewModel.changeData(data)
            list?.let { finAdapter.setData(it) }
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
            findViewById<ConstraintLayout>(R.id.networkDisconnected).visibility = View.VISIBLE
            binding.recyclerView.visibility = View.INVISIBLE
            binding.filterLayout.visibility = View.GONE
        } else {
            exceptionHandler(error, this) // Show error
        }
    }

    private fun getData(): Plan? {
        val pref = PreferenceManager.getDefaultSharedPreferences(this)
        val client = pref.getString("data", "")
        return if (client != "") {
            val obj = Gson().fromJson(client, Plan::class.java)
            if (obj.maCollectMoneyId != 0L) obj else null
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