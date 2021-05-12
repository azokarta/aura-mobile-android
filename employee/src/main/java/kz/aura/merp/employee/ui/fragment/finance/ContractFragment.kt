package kz.aura.merp.employee.ui.fragment.finance

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import io.nlopez.smartlocation.SmartLocation
import kz.aura.merp.employee.R
import kz.aura.merp.employee.adapter.PhoneNumbersAdapter
import kz.aura.merp.employee.adapter.StepsAdapter
import kz.aura.merp.employee.model.*
import kz.aura.merp.employee.databinding.FragmentContractBinding
import kz.aura.merp.employee.ui.activity.AddContributionActivity
import kz.aura.merp.employee.ui.activity.IncomingActivity
import kz.aura.merp.employee.ui.activity.OutgoingActivity
import kz.aura.merp.employee.util.NetworkResult
import kz.aura.merp.employee.util.ProgressDialog
import kz.aura.merp.employee.util.declareErrorByStatus
import kz.aura.merp.employee.view.OnSelectPhoneNumber
import kz.aura.merp.employee.viewmodel.FinanceViewModel

@AndroidEntryPoint
class ContractFragment : Fragment(), StepsAdapter.Companion.CompletedStepListener, OnSelectPhoneNumber {

    private var _binding: FragmentContractBinding? = null
    private val binding get() = _binding!!
    private val mFinanceViewModel: FinanceViewModel by activityViewModels()
    private lateinit var plan: Plan
    private val stepsAdapter: StepsAdapter by lazy { StepsAdapter(this) }
    private val phoneNumbersAdapter: PhoneNumbersAdapter by lazy { PhoneNumbersAdapter(this) }
    private lateinit var progressDialog: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            plan = it.getParcelable(ARG_PARAM1)!!
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentContractBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        binding.plan = plan

        // Initialize Loading Dialog
        progressDialog = ProgressDialog(requireContext())

        initStepView()
        initPhoneNumbers()

        binding.addContribution.setOnClickListener {
            val intent = Intent(requireContext(), AddContributionActivity::class.java)
            intent.putExtra("contractId", plan.contractId!!)
            intent.putExtra("clientPhoneNumbers", plan.customerPhoneNumbers!!.toTypedArray())
            intent.putExtra("businessProcessId", plan.planBusinessProcessId)
            startActivityForResult(intent, contributionRequestCode);
        }

        // Observe MutableLiveData
        setObserve()

        mFinanceViewModel.fetchBusinessProcessStatuses()

        return binding.root
    }

    private fun initPhoneNumbers() {
        binding.phoneNumbers.layoutManager = LinearLayoutManager(requireContext())
        binding.phoneNumbers.adapter = phoneNumbersAdapter
        phoneNumbersAdapter.setData(plan.customerPhoneNumbers)
    }

    private fun setObserve() {
        mFinanceViewModel.updatedPlanResponse.observe(viewLifecycleOwner, { res ->
            when (res) {
                is NetworkResult.Success -> {
                    progressDialog.hideLoading()
                    this.plan = res.data!!
                    binding.plan = plan
                }
                is NetworkResult.Loading -> progressDialog.showLoading()
                is NetworkResult.Error -> {
                    progressDialog.hideLoading()
                    declareErrorByStatus(res.message, res.status, requireContext())
                }
            }
        })
        mFinanceViewModel.businessProcessStatusesResponse.observe(viewLifecycleOwner, { res ->
            when (res) {
                is NetworkResult.Success -> {
                    progressDialog.hideLoading()
                    stepsAdapter.setData(res.data!!)
                }
                is NetworkResult.Loading -> progressDialog.showLoading()
                is NetworkResult.Error -> {
                    progressDialog.hideLoading()
                    declareErrorByStatus(res.message, res.status, requireContext())
                }
            }
        })
    }

    private fun initStepView() {
        binding.stepsRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.stepsRecyclerView.adapter = stepsAdapter
        binding.stepsRecyclerView.isNestedScrollingEnabled = false
        plan.planBusinessProcessId?.let { stepsAdapter.setStep(it) }
    }

    override fun stepCompleted(businessProcessStatus: BusinessProcessStatus) {
        confirmStepAlertDialog(businessProcessStatus)
    }

    private fun confirmStepAlertDialog(businessProcessStatus: BusinessProcessStatus) {
        val builder = AlertDialog.Builder(requireContext())

        builder.setTitle(getString(R.string.reallyWantToChangeStatusOfPlan))

        builder.setNegativeButton(getString(R.string.no)) { dialog, _ ->
            stepsAdapter.setStep(plan.planBusinessProcessId)
            dialog.dismiss()
        }
        builder.setPositiveButton(getString(R.string.yes)) { _, _ ->
            progressDialog.showLoading()
            getCurrentLocation { latitude, longitude ->
                mFinanceViewModel.updateBusinessProcessStep(
                    plan.contractId!!,
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
        SmartLocation.with(requireContext()).location().oneFix()
            .start {
                callback.invoke(it.latitude, it.longitude)
            }
    }

    companion object {
        private const val ARG_PARAM1 = "plan"
        private const val callRequestCode = 1000
        private const val contributionRequestCode = 2000

        @JvmStatic
        fun newInstance(plan: Plan) =
            ContractFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(ARG_PARAM1, plan)
                }
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                callRequestCode -> {
                    val calls = data?.getParcelableArrayListExtra<Call>("calls")!!
                        .toCollection(ArrayList<Call>())
                    mFinanceViewModel.callsResponse.postValue(NetworkResult.Success(calls))
                }
                contributionRequestCode -> {
                    val contributions = data?.getParcelableArrayListExtra<Contribution>("contributions")!!.toCollection(ArrayList<Contribution>())
                    mFinanceViewModel.contributionsResponse.postValue(NetworkResult.Success(contributions))
                    Snackbar.make(
                        binding.addContribution,
                        R.string.successfullySaved,
                        Snackbar.LENGTH_SHORT
                    )
                        .show()
                }
            }
        }
    }

    override fun incoming(phoneNumber: String) {
        val intent = Intent(binding.root.context, IncomingActivity::class.java)
        intent.putExtra("phoneNumber", phoneNumber)
        intent.putExtra("contractId", plan.contractId)
        startActivityForResult(intent, callRequestCode);
    }

    override fun outgoing(phoneNumber: String) {
        val intent = Intent(binding.root.context, OutgoingActivity::class.java)
        intent.putExtra("phoneNumber", phoneNumber)
        intent.putExtra("contractId", plan.contractId)
        startActivityForResult(intent, callRequestCode);
    }
}