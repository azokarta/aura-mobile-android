package kz.aura.merp.employee.ui.activity

import android.app.AlertDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.viewModels
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import io.nlopez.smartlocation.SmartLocation
import kz.aura.merp.employee.R
import kz.aura.merp.employee.adapter.StepsAdapter
import kz.aura.merp.employee.databinding.ActivityDailyPlanBinding
import kz.aura.merp.employee.model.BusinessProcessStatus
import kz.aura.merp.employee.model.ChangeBusinessProcess
import kz.aura.merp.employee.model.Plan
import kz.aura.merp.employee.ui.fragment.finance.ContractFragment
import kz.aura.merp.employee.util.NetworkResult
import kz.aura.merp.employee.util.ProgressDialog
import kz.aura.merp.employee.util.declareErrorByStatus
import kz.aura.merp.employee.viewmodel.FinanceViewModel

class DailyPlanActivity : AppCompatActivity(), StepsAdapter.Companion.CompletedStepListener {

    private lateinit var binding: ActivityDailyPlanBinding

    private lateinit var plan: Plan
    private val stepsAdapter: StepsAdapter by lazy { StepsAdapter(this) }
    private val mFinanceViewModel: FinanceViewModel by viewModels()
    private lateinit var progressDialog: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        plan = intent.getParcelableExtra("plan")!!
        binding = ActivityDailyPlanBinding.inflate(layoutInflater)
        binding.plan = plan
        setContentView(binding.root)

        // Toolbar
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = getString(R.string.plan)

        // Turn off screenshot
        window.setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE)

        // Initialize Loading Dialog
        progressDialog = ProgressDialog(this)

        initStepView()

        binding.addContribution.setOnClickListener {
            val intent = Intent(this, ChangeResultActivity::class.java)
            intent.putExtra("contractId", plan.contractId)
            intent.putExtra("clientPhoneNumbers", plan.customerPhoneNumbers!!.toTypedArray())
            intent.putExtra("businessProcessId", plan.planBusinessProcessId)
            startActivityForResult(intent, changeResultRequestCode);
        }
    }

    private fun setupObservers() {
        mFinanceViewModel.updatedPlanResponse.observe(this, { res ->
            when (res) {
                is NetworkResult.Success -> {
                    progressDialog.hideLoading()
                    this.plan = res.data!!
                    binding.plan = plan
                }
                is NetworkResult.Loading -> progressDialog.showLoading()
                is NetworkResult.Error -> {
                    progressDialog.hideLoading()
                    declareErrorByStatus(res.message, res.status, this)
                }
            }
        })
        mFinanceViewModel.businessProcessStatusesResponse.observe(this, { res ->
            when (res) {
                is NetworkResult.Success -> {
                    progressDialog.hideLoading()
                    stepsAdapter.setData(res.data!!)
                }
                is NetworkResult.Loading -> progressDialog.showLoading()
                is NetworkResult.Error -> {
                    progressDialog.hideLoading()
                    declareErrorByStatus(res.message, res.status, this)
                }
            }
        })
    }

    private fun initStepView() {
        binding.stepsRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.stepsRecyclerView.adapter = stepsAdapter
        binding.stepsRecyclerView.isNestedScrollingEnabled = false
        plan.planBusinessProcessId?.let { stepsAdapter.setStep(it) }
    }

    override fun stepCompleted(businessProcessStatus: BusinessProcessStatus) {
        confirmStepAlertDialog(businessProcessStatus)
    }

    private fun confirmStepAlertDialog(businessProcessStatus: BusinessProcessStatus) {
        val builder = AlertDialog.Builder(this)

        builder.setTitle(getString(R.string.reallyWantToChangeStatusOfPlan))

        builder.setNegativeButton(getString(R.string.no)) { dialog, _ ->
            stepsAdapter.setStep(plan.planBusinessProcessId)
            dialog.dismiss()
        }
        builder.setPositiveButton(getString(R.string.yes)) { _, _ ->
            progressDialog.showLoading()
            getCurrentLocation { latitude, longitude ->
                mFinanceViewModel.updateBusinessProcess(
                    plan.contractId,
                    ChangeBusinessProcess(businessProcessStatus.id, latitude, longitude)
                )
            }
        }
        builder.setOnCancelListener {
            stepsAdapter.setStep(plan.planBusinessProcessId)
        }

        builder.create().show()
    }

    private fun getCurrentLocation(callback: (latitude: Double, longitude: Double) -> Unit) {
        SmartLocation.with(this).location().oneFix()
            .start {
                callback.invoke(it.latitude, it.longitude)
            }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    companion object {
        const val changeResultRequestCode = 2000
    }
}