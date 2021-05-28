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
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import io.nlopez.smartlocation.SmartLocation
import kz.aura.merp.employee.R
import kz.aura.merp.employee.adapter.PhoneNumbersAdapter
import kz.aura.merp.employee.adapter.StepsAdapter
import kz.aura.merp.employee.model.*
import kz.aura.merp.employee.databinding.FragmentContractBinding
import kz.aura.merp.employee.ui.activity.ChangeResultActivity
import kz.aura.merp.employee.ui.activity.IncomingActivity
import kz.aura.merp.employee.ui.activity.OutgoingActivity
import kz.aura.merp.employee.util.LoadingType
import kz.aura.merp.employee.util.NetworkResult
import kz.aura.merp.employee.util.ProgressDialog
import kz.aura.merp.employee.util.declareErrorByStatus
import kz.aura.merp.employee.view.OnSelectPhoneNumber
import kz.aura.merp.employee.viewmodel.FinanceViewModel
import kz.aura.merp.employee.viewmodel.SharedViewModel

@AndroidEntryPoint
class ContractFragment : Fragment(), OnSelectPhoneNumber {

    private var _binding: FragmentContractBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private lateinit var sharedViewModel: SharedViewModel
    private lateinit var financeViewModel: FinanceViewModel
    private var contractId: Long? = null
    private var plan: Plan? = null
    private val phoneNumbersAdapter: PhoneNumbersAdapter by lazy { PhoneNumbersAdapter(this) }
    private lateinit var progressDialog: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            contractId = it.getLong("contractId")
        }
    }

    override fun onCreateView (
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        sharedViewModel = ViewModelProvider(this).get(SharedViewModel::class.java)
        financeViewModel = ViewModelProvider(this).get(FinanceViewModel::class.java)

        _binding = FragmentContractBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        binding.sharedViewModel = sharedViewModel
        val root: View = binding.root

        // Initialize Loading Dialog
        progressDialog = ProgressDialog(requireContext())

        initPhoneNumbers()

        // Observe MutableLiveData
        setObservers()

        callRequest()

        return root
    }

    private fun initPhoneNumbers() {
        binding.phoneNumbers.layoutManager = LinearLayoutManager(requireContext())
        binding.phoneNumbers.adapter = phoneNumbersAdapter
        binding.phoneNumbers.isNestedScrollingEnabled = false
    }

    private fun callRequest() {
        financeViewModel.fetchPlan(contractId!!)
        financeViewModel.fetchBusinessProcessStatuses()
    }

    private fun setObservers() {
        financeViewModel.planResponse.observe(viewLifecycleOwner, { res ->
            when (res) {
                is NetworkResult.Success -> {
                    sharedViewModel.setResponse(res)
                    this.plan = res.data
                    binding.plan = plan

                    // Set phone numbers
                    phoneNumbersAdapter.setData(plan!!.customerPhoneNumbers)
                }
                is NetworkResult.Loading -> {
                    sharedViewModel.setResponse(res)
                }
                is NetworkResult.Error -> {
                    sharedViewModel.setResponse(res)
                }
            }
        })
    }

    companion object {
        private const val callRequestCode = 1000
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
                    showSnackbar(binding.phoneNumbers)
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
        intent.putExtra("contractId", contractId)
        startActivityForResult(intent, callRequestCode);
    }

    override fun outgoing(phoneNumber: String) {
        val intent = Intent(binding.root.context, OutgoingActivity::class.java)
        intent.putExtra("phoneNumber", phoneNumber)
        intent.putExtra("contractId", contractId)
        startActivityForResult(intent, callRequestCode);
    }
}