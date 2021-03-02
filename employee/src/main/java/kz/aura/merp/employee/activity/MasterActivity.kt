package kz.aura.merp.employee.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import androidx.activity.viewModels
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.chip.Chip
import kz.aura.merp.employee.R
import kz.aura.merp.employee.adapter.ServiceApplicationAdapter
import kz.aura.merp.employee.data.SharedViewModel
import kz.aura.merp.employee.data.model.ServiceApplication
import kz.aura.merp.employee.data.viewmodel.MasterViewModel
import kz.aura.merp.employee.databinding.ActivityMasterBinding
import kz.aura.merp.employee.util.Helpers
import kz.aura.merp.employee.util.LanguageHelper
import com.google.gson.Gson
import kz.aura.merp.employee.data.viewmodel.ReferenceViewModel
import kz.aura.merp.employee.databinding.FilterBottomSheetBinding
import kz.aura.merp.employee.util.Permissions

class MasterActivity : AppCompatActivity() {

    private val mMasterViewModel: MasterViewModel by viewModels()
    private val mSharedViewModel: SharedViewModel by viewModels()
    private val mReferenceViewModel: ReferenceViewModel by viewModels()
    private val serviceApplicationAdapter: ServiceApplicationAdapter by lazy { ServiceApplicationAdapter() }
    private var masterId: Long? = null
    private lateinit var binding: ActivityMasterBinding
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
        binding = ActivityMasterBinding.inflate(layoutInflater)
        binding.lifecycleOwner = this
        binding.mSharedViewModel = mSharedViewModel
        val view = binding.root
        setContentView(view)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.title = getString(R.string.master)
        supportActionBar?.subtitle = Helpers.getStaff(this)?.username

        setupFilterResources()

        Permissions(this, this).enableLocation()

        // Get master id
        masterId = Helpers.getStaffId(this)

        // Setup RecyclerView
        binding.recyclerView.layoutManager = LinearLayoutManager(applicationContext)
        binding.recyclerView.adapter = serviceApplicationAdapter

        // Observe MutableLiveData
        mMasterViewModel.applications.observe(this, { data ->
            mSharedViewModel.checkData(data)
            serviceApplicationAdapter.setData(data)
        })

        // Observe errors
        mMasterViewModel.error.observe(this, { error ->
            checkError(error)
        })
        mReferenceViewModel.error.observe(this, { error ->
            checkError(error)
        })

        // If network is disconnected and user clicks restart, get data again
        findViewById<Button>(R.id.restart).setOnClickListener {
            if (Helpers.verifyAvailableNetwork(this)) {
                mMasterViewModel.fetchServiceApplications(masterId!!) // fetch serviceApplications
                binding.progressBar.visibility = View.VISIBLE
                binding.recyclerView.visibility = View.VISIBLE
                findViewById<ConstraintLayout>(R.id.networkDisconnected).visibility = View.GONE
            }
        }

        // Fetch serviceApplications
        mMasterViewModel.fetchServiceApplications(masterId!!)

        setupFilterBottomSheet()
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
        if (!Helpers.verifyAvailableNetwork(this)) {
            findViewById<ConstraintLayout>(R.id.networkDisconnected).visibility = View.VISIBLE
            binding.recyclerView.visibility = View.INVISIBLE
        } else {
            Helpers.exceptionHandler(error, this) // Show error
        }
    }

    private fun getData(): ServiceApplication? {
        val pref = PreferenceManager.getDefaultSharedPreferences(this)
        val serviceApplication = pref.getString("data", "")
        return if (serviceApplication != "") {
            val obj = Gson().fromJson(serviceApplication, ServiceApplication::class.java)
            if (obj.id != 0L) obj else null
        } else {
            null
        }
    }

    private fun setupFilterResources() {
        filterStatuses = arrayListOf(
            getString(R.string.all), getString(R.string.leaving), getString(
                R.string.gotToTheAddress
            ), getString(R.string.serviceProcessStarted), getString(R.string.serviceProcessFinished)
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
            filterServiceApplications()
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

    private fun filterServiceApplications() {
        when (selectedSortFilter) {
            0 -> mMasterViewModel.applications.value?.let { serviceApplicationAdapter.setData(it) }
            1 -> serviceApplicationAdapter.setData(mMasterViewModel.applications.value!!.sortedBy { it.applicantName }.toCollection(ArrayList()))
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