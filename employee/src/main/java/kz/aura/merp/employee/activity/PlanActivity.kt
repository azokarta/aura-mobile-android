package kz.aura.merp.employee.activity

import android.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import androidx.activity.viewModels
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import io.nlopez.smartlocation.SmartLocation
import kz.aura.merp.employee.R
import kz.aura.merp.employee.adapter.StepsAdapter
import kz.aura.merp.employee.data.model.*
import kz.aura.merp.employee.data.viewmodel.FinanceViewModel
import kz.aura.merp.employee.data.viewmodel.ReferenceViewModel
import kz.aura.merp.employee.databinding.ActivityPlanBinding
import kz.aura.merp.employee.databinding.CauseDialogBinding
import kz.aura.merp.employee.util.Helpers

class PlanActivity : AppCompatActivity(), StepsAdapter.Companion.CompletedStepListener {

    private lateinit var binding: ActivityPlanBinding
    private val mFinanceViewModel: FinanceViewModel by viewModels()
    private lateinit var plan: Plan
    var step = 0;
    private val results = arrayListOf<FinanceResult>()
    private var trackStepOrdersBusinessProcesses = arrayListOf<String>()
    private val stepsAdapter: StepsAdapter by lazy { StepsAdapter(this) }
    private lateinit var location: Location
    private var collectorId: Long? = null
    private val mReferenceViewModel: ReferenceViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlanBinding.inflate(layoutInflater)
        plan = intent.getParcelableExtra("plan")!!
        binding.plan = plan
        binding.lifecycleOwner = this
        setContentView(binding.root)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = getString(R.string.plan)

        // Observe MutableLiveData
        setObserve()

        mReferenceViewModel.fetchMaCollectResults()
        mReferenceViewModel.fetchTrackStepOrdersBusinessProcesses(bpId)
        mFinanceViewModel.fetchTrackEmpProcessCollectMoney(plan.maCollectMoneyId)

        // Getting a location
        SmartLocation.with(this).location()
            .start {
                println("Latitude: ${it.latitude}, longitude: ${it.longitude}")
                location = Location(it.latitude, it.longitude)
            }

        // Set collector id
        collectorId = Helpers.getStaffId(this)

        initStepView()
    }


    private fun setObserve() {
        mReferenceViewModel.maCollectResults.observe(this, { data ->
            binding.resultBtn.text = data[plan.maCollectResultId].nameRu
            results.addAll(data)
        })
        mReferenceViewModel.trackStepOrdersBusinessProcesses.observe(this, { data ->
            trackStepOrdersBusinessProcesses.addAll(data.map { it.trackStepNameRu } as ArrayList)
            stepsAdapter.setData(trackStepOrdersBusinessProcesses)
        })
        mFinanceViewModel.updatedPlan.observe(this, { data ->
            println(data)
            plan = data
            binding.plan = data
            binding.executePendingBindings() // Update view
            Snackbar.make(binding.root, getString(R.string.successfullySaved), Snackbar.LENGTH_LONG).show()
        })
        mFinanceViewModel.trackEmpProcessCollectMoney.observe(this, { data ->
            step = data.size
            initBtnListeners()
            initStepView()
            stepsAdapter.setStep(step)
        })
    }


    private fun initStepView() {
        binding.stepsRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.stepsRecyclerView.adapter = stepsAdapter
        binding.stepsRecyclerView.isNestedScrollingEnabled = false
    }

    private fun showResultsAlertDialog() {
        val demoResultsByLang = results.map { it.nameRu } as ArrayList
        val builder = AlertDialog.Builder(this)

        builder.setTitle(getString(R.string.result))

        builder.setItems(demoResultsByLang.toArray(arrayOfNulls<String>(0))) { _, i ->
            plan.maCollectResultId = i
            binding.resultBtn.text = results[i].nameRu
            when (i) {
                2, 3 -> showCauseAlertDialog()
                else -> mFinanceViewModel.updatePlan(plan)
            }
        }

        builder.setNegativeButton(getString(R.string.cancel)) { dialog, _ ->
            dialog.dismiss()
        }

        val dialog = builder.create()
        dialog.show()
    }

    private fun initBtnListeners() {
        binding.resultBtn.setOnClickListener {
            showResultsAlertDialog()
        }
    }

    private fun showCauseAlertDialog() {
        val builder = AlertDialog.Builder(this)
        val layoutInflater = LayoutInflater.from(this).inflate(R.layout.cause_dialog, null)
        val dialogBinding = CauseDialogBinding.bind(layoutInflater)
        dialogBinding.description = plan.description

        builder.setTitle(getString(R.string.cause))

        builder.setNegativeButton(getString(R.string.cancel)) { dialog, _ ->
            dialog.dismiss()
        }
        builder.setPositiveButton(getString(R.string.save)) { _, _ ->
            plan.description = dialogBinding.finBusinessProcessesCause.text.toString()
            mFinanceViewModel.updatePlan(plan)
        }

        builder.setView(dialogBinding.root)
        val dialog = builder.create()
        dialog.show()
    }

    override fun onDestroy() {
        SmartLocation.with(this).location().stop()
        super.onDestroy()
    }

    override fun stepCompleted(position: Int) {
        step+=1
        val longitude = location.longitude.toString()
        val latitude = location.latitude.toString()
        val completed = TrackEmpProcess(null, longitude, latitude, step, null, plan.maCollectMoneyId)
        mFinanceViewModel.updateBusinessProcessStep(completed)
        mReferenceViewModel.createStaffLocation(StaffLocation(collectorId!!, maTbpId = bpId, longitude = longitude, latitude = latitude, maTrackStepId = step))
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    companion object {
        private const val bpId = 4
    }

}