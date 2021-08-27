package kz.aura.merp.employee.ui.finance.activity

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import io.nlopez.smartlocation.SmartLocation
import kz.aura.merp.employee.R
import kz.aura.merp.employee.adapter.PhoneNumbersAdapter
import kz.aura.merp.employee.adapter.StepsAdapter
import kz.aura.merp.employee.base.BaseActivity
import kz.aura.merp.employee.base.NetworkResult
import kz.aura.merp.employee.databinding.ActivityDailyPlanBinding
import kz.aura.merp.employee.model.BusinessProcessStatus
import kz.aura.merp.employee.model.ChangeBusinessProcess
import kz.aura.merp.employee.model.DailyPlan
import kz.aura.merp.employee.util.*
import kz.aura.merp.employee.view.OnSelectPhoneNumber
import kz.aura.merp.employee.view.PermissionsListener
import kz.aura.merp.employee.viewmodel.FinanceViewModel
import kz.aura.merp.employee.viewmodel.SharedViewModel

@AndroidEntryPoint
class DailyPlanActivity : BaseActivity(), StepsAdapter.Companion.CompletedStepListener,
    OnSelectPhoneNumber, SwipeRefreshLayout.OnRefreshListener, PermissionsListener {

    private lateinit var binding: ActivityDailyPlanBinding

    private var dailyPlanId: Long? = null
    private val stepsAdapter: StepsAdapter by lazy { StepsAdapter(this) }
    private val mFinanceViewModel: FinanceViewModel by viewModels()
    private val sharedViewModel: SharedViewModel by viewModels()
    private lateinit var progressDialog: ProgressDialog
    private val phoneNumbersAdapter: PhoneNumbersAdapter by lazy { PhoneNumbersAdapter(this) }
    private var plan: DailyPlan? = null
    private lateinit var permissions: Permissions
    private var modifiedResultOrStatus = false
    private var selectedStepId: Long? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dailyPlanId = intent.getLongExtra("dailyPlanId", 0L)
        binding = ActivityDailyPlanBinding.inflate(layoutInflater)
        binding.lifecycleOwner = this
        binding.sharedViewModel = sharedViewModel
        setContentView(binding.root)

        // Toolbar
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = getString(R.string.plan)

        // Initialize Loading Dialog
        progressDialog = ProgressDialog(this)

        permissions = Permissions(this, this, this)
        permissions.setListener(this)

        binding.swipeRefresh.setOnRefreshListener(this)
        binding.createScheduleCall.setOnClickListener(::openCreateScheduledCallScreen)

        initPhoneNumbers()
        initStepView()

        // Observe MutableLiveData
        setupObservers()

        binding.changeResult.setOnClickListener {
            goToChangeResult()
        }

        callRequests()
    }

    private fun openCreateScheduledCallScreen(view: View) {
        plan?.let {
            val intent = Intent(this, CreateScheduledCallActivity::class.java)
            intent.putExtra("contractId", it.contractId)
            startActivityForResult(intent, createScheduledCallRequestCode)
        }
    }

    private fun goToChangeResult() {
        plan?.let {
            val intent = Intent(this, ChangeResultActivity::class.java)
            intent.putExtra("contractId", it.contractId)
            intent.putExtra("clientPhoneNumbers", it.customerPhoneNumbers?.toTypedArray())
            intent.putExtra("businessProcessId", it.planBusinessProcessId)
            startActivityForResult(intent, changeResultRequestCode)
        }
    }

    private fun initPhoneNumbers() {
        binding.phoneNumbers.layoutManager = LinearLayoutManager(this)
        binding.phoneNumbers.adapter = phoneNumbersAdapter
        binding.phoneNumbers.isNestedScrollingEnabled = false
    }

    private fun callRequests() {
        dailyPlanId?.let { mFinanceViewModel.fetchDailyPlan(it) }
        mFinanceViewModel.fetchBusinessProcessStatuses()
    }

    private fun setupObservers() {
        mFinanceViewModel.businessProcessStatusesResponse.observe(this, { res ->
            when (res) {
                is NetworkResult.Success -> {
                    stepsAdapter.setData(res.data!!)
                }
                is NetworkResult.Loading -> {
                }
                is NetworkResult.Error -> {
                    showException(res.message, this)
                }
            }
        })
        mFinanceViewModel.changeBusinessProcessStatusResponse.observe(this, { res ->
            when (res) {
                is NetworkResult.Success -> {
                    modifiedResultOrStatus = true
                    sharedViewModel.setResponse(res)
                    dailyPlanId?.let { mFinanceViewModel.fetchDailyPlan(it) }
                }
                is NetworkResult.Loading -> sharedViewModel.setResponse(res)
                is NetworkResult.Error -> {
                    sharedViewModel.setResponse(res)
                    showException(res.message, this)
                }
            }
        })
        mFinanceViewModel.dailyPlanResponse.observe(this, { res ->
            when (res) {
                is NetworkResult.Success -> {
                    sharedViewModel.setResponse(res)
                    plan = res.data
                    binding.plan = res.data
                    plan?.planBusinessProcessId?.let { stepsAdapter.setStep(it) }
                    phoneNumbersAdapter.setData(plan?.customerPhoneNumbers)
                }
                is NetworkResult.Loading -> sharedViewModel.setResponse(res)
                is NetworkResult.Error -> {
                    sharedViewModel.setResponse(res)
                    showException(res.message, this)
                }
            }
        })
    }

    private fun initStepView() {
        binding.stepsRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.stepsRecyclerView.adapter = stepsAdapter
        binding.stepsRecyclerView.isNestedScrollingEnabled = false
    }

    override fun stepCompleted(businessProcessStatus: BusinessProcessStatus, position: Int) {
        confirmStepAlertDialog(businessProcessStatus)
    }

    private fun confirmStepAlertDialog(businessProcessStatus: BusinessProcessStatus) {
        val builder = AlertDialog.Builder(this)

        builder.setTitle(getString(R.string.really_want_to_change_status_of_plan))

        builder.setNegativeButton(getString(R.string.no)) { dialog, _ ->
            dialog.dismiss()
        }
        builder.setPositiveButton(getString(R.string.yes)) { _, _ ->
            selectedStepId = businessProcessStatus.id
            updateBusinessProcess(businessProcessStatus.id)
        }

        builder.create().show()
    }

    private fun updateBusinessProcess(businessProcessStatusId: Long) {
        if (!permissions.isLocationServicesEnabled()) return

        SmartLocation.with(this).location().oneFix()
            .start {
                mFinanceViewModel.updateBusinessProcess(
                    plan!!.contractId,
                    ChangeBusinessProcess(businessProcessStatusId, it.latitude, it.longitude)
                )
            }
    }

    override fun onBackPressed() {
        if (modifiedResultOrStatus) {
            val intent = Intent();
            setResult(RESULT_OK, intent)
        }
        finish();
        super.onBackPressed()
    }

    override fun onSupportNavigateUp(): Boolean {
        if (modifiedResultOrStatus) {
            val intent = Intent();
            setResult(RESULT_OK, intent)
        }
        finish()
        return true
    }

    companion object {
        const val changeResultRequestCode = 2000
        private const val callRequestCode = 1000
        private const val createScheduledCallRequestCode = 3000
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                callRequestCode -> {
                    showSnackbar(binding.phoneNumbers)
                }
                changeResultRequestCode -> {
                    modifiedResultOrStatus = true
                    showSnackbar(binding.changeResult)
                    dailyPlanId?.let { mFinanceViewModel.fetchDailyPlan(it) }
                }
                createScheduledCallRequestCode -> {
                    showSnackbar(binding.createScheduleCall)
                }
            }
        }
    }

    private fun showSnackbar(view: View) = Snackbar.make(
        view,
        R.string.successfully_saved,
        Snackbar.LENGTH_SHORT
    ).show()

    override fun incoming(phoneNumber: String) {
        val intent = Intent(binding.root.context, IncomingActivity::class.java)
        intent.putExtra("phoneNumber", phoneNumber)
        intent.putExtra("contractId", plan!!.contractId)
        startActivityForResult(intent, callRequestCode);
    }

    override fun outgoing(phoneNumber: String) {
        val intent = Intent(binding.root.context, OutgoingActivity::class.java)
        intent.putExtra("phoneNumber", phoneNumber)
        intent.putExtra("contractId", plan!!.contractId)
        startActivityForResult(intent, callRequestCode);
    }

    override fun onRefresh() {
        sharedViewModel.setLoadingType(LoadingType.SWIPE_REFRESH)
        callRequests()
    }

    override fun sendResultOfRequestLocation(granted: Boolean) {
        if (granted) {
            permissions.enableLocation()
        } else {
            showException(getString(R.string.have_not_allowed_access_to_the_location), this)
        }
    }

    override fun sendResultOfEnableLocation(granted: Boolean) {
        if (granted) {
            selectedStepId?.let { updateBusinessProcess(it) }
        }
    }
}