package kz.aura.merp.employee.ui.finance.fragment

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import androidx.fragment.app.viewModels
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kz.aura.merp.employee.R
import kz.aura.merp.employee.adapter.PhoneNumbersAdapter
import kz.aura.merp.employee.model.*
import kz.aura.merp.employee.databinding.FragmentContractBinding
import kz.aura.merp.employee.ui.finance.activity.IncomingActivity
import kz.aura.merp.employee.ui.finance.activity.OutgoingActivity
import kz.aura.merp.employee.base.NetworkResult
import kz.aura.merp.employee.util.ProgressDialog
import kz.aura.merp.employee.view.OnSelectPhoneNumber
import kz.aura.merp.employee.viewmodel.SharedViewModel
import kz.aura.merp.employee.viewmodel.finance.ContractViewModel

@AndroidEntryPoint
class ContractFragment : Fragment(R.layout.fragment_contract), OnSelectPhoneNumber {

    private var _binding: FragmentContractBinding? = null
    private val binding get() = _binding!!

    private val sharedViewModel: SharedViewModel by viewModels()
    private val contractViewModel: ContractViewModel by viewModels()
    private var contractId: Long? = null
    private val phoneNumbersAdapter: PhoneNumbersAdapter by lazy { PhoneNumbersAdapter(this) }
    private lateinit var progressDialog: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            contractId = it.getLong("contractId")
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentContractBinding.bind(view)

        with (binding) {
            lifecycleOwner = this@ContractFragment
            sharedViewModel = this@ContractFragment.sharedViewModel
            error.restart.setOnClickListener { callRequests() }
        }

        progressDialog = ProgressDialog(requireContext())

        initPhoneNumbers()

        setObservers()

        callRequests()
    }

    private fun initPhoneNumbers() {
        binding.phoneNumbers.adapter = phoneNumbersAdapter
        binding.phoneNumbers.isNestedScrollingEnabled = false
    }

    private fun callRequests() {
        contractViewModel.fetchPlan(contractId!!)
        contractViewModel.fetchBusinessProcessStatuses()
    }

    private fun setObservers() {
        contractViewModel.planResponse.observe(viewLifecycleOwner, { res ->
            when (res) {
                is NetworkResult.Success -> {
                    sharedViewModel.setResponse(res)
                    val plan = res.data?.data
                    binding.plan = plan

                    // Set phone numbers
                    phoneNumbersAdapter.submitList(plan?.customerPhoneNumbers)
                }
                is NetworkResult.Loading -> sharedViewModel.setResponse(res)
                is NetworkResult.Error -> sharedViewModel.setResponse(res)
            }
        })
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
        R.string.successfully_saved,
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

    companion object {
        private const val callRequestCode = 1000
    }
}