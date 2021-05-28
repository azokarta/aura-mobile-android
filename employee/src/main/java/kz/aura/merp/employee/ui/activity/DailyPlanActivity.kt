package kz.aura.merp.employee.ui.activity

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.WindowManager
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
import kz.aura.merp.employee.databinding.ActivityDailyPlanBinding
import kz.aura.merp.employee.model.BusinessProcessStatus
import kz.aura.merp.employee.model.ChangeBusinessProcess
import kz.aura.merp.employee.model.Plan
import kz.aura.merp.employee.util.*
import kz.aura.merp.employee.view.OnSelectPhoneNumber
import kz.aura.merp.employee.viewmodel.FinanceViewModel
import kz.aura.merp.employee.viewmodel.SharedViewModel

@AndroidEntryPoint
class DailyPlanActivity : AppCompatActivity(), StepsAdapter.Companion.CompletedStepListener, OnSelectPhoneNumber, SwipeRefreshLayout.OnRefreshListener {

    private lateinit var binding: ActivityDailyPlanBinding

    private var contractId: Long? = null
    private val stepsAdapter: StepsAdapter by lazy { StepsAdapter(this) }
    private val mFinanceViewModel: FinanceViewModel by viewModels()
    private val sharedViewModel: SharedViewModel by viewModels()
    private lateinit var progressDialog: ProgressDialog
    private val phoneNumbersAdapter: PhoneNumbersAdapter by lazy { PhoneNumbersAdapter(this) }
    private var plan: Plan? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        contractId = intent.getLongExtra("contractId", 0L)
        binding = ActivityDailyPlanBinding.inflate(layoutInflater)
        binding.lifecycleOwner = this
        binding.sharedViewModel = sharedViewModel
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

        binding.swipeRefresh.setOnRefreshListener(this)

        initPhoneNumbers()
        initStepView()

        // Observe MutableLiveData
        setupObservers()

        binding.changeResult.setOnClickListener {
            goToChangeResult()
        }

        callRequests()
    }

    private fun goToChangeResult() {
        plan?.let {
            val intent = Intent(this, ChangeResultActivity::class.java)
            intent.putExtra("contractId", it.contractId)
            intent.putExtra("clientPhoneNumbers", it.customerPhoneNumbers.toTypedArray())
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
        contractId?.let { mFinanceViewModel.fetchPlan(it) }
        mFinanceViewModel.fetchBusinessProcessStatuses()
    }

    private fun setupObservers() {
        mFinanceViewModel.businessProcessStatusesResponse.observe(this, { res ->
            when (res) {
                is NetworkResult.Success -> {
                    stepsAdapter.setData(res.data!!)
                }
                is NetworkResult.Loading -> {}
                is NetworkResult.Error -> {
                    showException(res.message, this)
                }
            }
        })
        mFinanceViewModel.changeBusinessProcessStatusResponse.observe(this, { res ->
            when (res) {
                is NetworkResult.Success -> {
                    progressDialog.hideLoading()
                    contractId?.let { mFinanceViewModel.fetchPlan(it) }
                }
                is NetworkResult.Loading -> progressDialog.showLoading()
                is NetworkResult.Error -> {
                    progressDialog.hideLoading()
                    showException(res.message, this)
                }
            }
        })
        mFinanceViewModel.planResponse.observe(this, { res ->
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

    override fun stepCompleted(businessProcessStatus: BusinessProcessStatus) {
        confirmStepAlertDialog(businessProcessStatus)
    }

    private fun confirmStepAlertDialog(businessProcessStatus: BusinessProcessStatus) {
        val builder = AlertDialog.Builder(this)

        builder.setTitle(getString(R.string.reallyWantToChangeStatusOfPlan))

        builder.setNegativeButton(getString(R.string.no)) { dialog, _ ->
            stepsAdapter.setStep(plan!!.planBusinessProcessId)
            dialog.dismiss()
        }
        builder.setPositiveButton(getString(R.string.yes)) { _, _ ->
            progressDialog.showLoading()
            getCurrentLocation { latitude, longitude ->
                mFinanceViewModel.updateBusinessProcess(
                    plan!!.contractId,
                    ChangeBusinessProcess(businessProcessStatus.id, latitude, longitude)
                )
            }
        }
        builder.setOnCancelListener {
            stepsAdapter.setStep(plan!!.planBusinessProcessId)
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
        private const val callRequestCode = 1000
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                callRequestCode -> {
                    showSnackbar(binding.phoneNumbers)
                }
                changeResultRequestCode -> {
                    showSnackbar(binding.changeResult)
                }
            }
        }
    }

    private fun showSnackbar(view: View) = Snackbar.make(
        view,
        R.string.successfullySaved,
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
}