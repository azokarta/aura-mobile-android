package kz.aura.merp.employee.ui.fragment.finance

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.get
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import kz.aura.merp.employee.R
import kz.aura.merp.employee.adapter.CallsAdapter
import kz.aura.merp.employee.databinding.FragmentPlanCallsBinding
import kz.aura.merp.employee.util.NetworkResult
import kz.aura.merp.employee.util.declareErrorByStatus
import kz.aura.merp.employee.util.verifyAvailableNetwork
import kz.aura.merp.employee.viewmodel.FinanceViewModel
import kz.aura.merp.employee.viewmodel.SharedViewModel

@AndroidEntryPoint
class PlanCallsFragment : Fragment() {

    private var _binding: FragmentPlanCallsBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private val mFinanceViewModel: FinanceViewModel by activityViewModels()
    private lateinit var sharedViewModel: SharedViewModel
    private val callsAdapter: CallsAdapter by lazy { CallsAdapter() }
    private var contractId: Long? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            contractId = it.getLong("contractId")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        sharedViewModel = ViewModelProvider(this).get(SharedViewModel::class.java)

        _binding = FragmentPlanCallsBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        binding.sharedViewModel = sharedViewModel
        val root: View = binding.root

        setupRecyclerView()

        setupObservers()

        callRequests()

        binding.toggleButton.addOnButtonCheckedListener { _, checkedId, isChecked ->
            val callsHistory = mFinanceViewModel.callsHistoryResponse.value
            val calls = mFinanceViewModel.callsResponse.value
            if (isChecked) {
                when (checkedId) {
                    binding.callsForMonthBtn.id -> {
                        if (calls == null) {
                            callsAdapter.setData(mFinanceViewModel.callsResponse.value!!.data!!)
                        }
                    }
                    binding.callsHistoryBtn.id -> {
                        if (callsHistory == null) {
                            mFinanceViewModel.fetchCallHistory(contractId!!)
                        } else {
                            callsAdapter.setData(mFinanceViewModel.callsHistoryResponse.value!!.data!!)
                        }
                    }
                }
            }
        }

        return root
    }

    private fun callRequests() {
        mFinanceViewModel.fetchLastMonthCallsByContractId(contractId!!)
    }

    private fun setupRecyclerView() {
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = callsAdapter
        binding.recyclerView.isNestedScrollingEnabled = false
    }

    private fun setupObservers() {
        mFinanceViewModel.callsResponse.observe(viewLifecycleOwner, { res ->
            when (res) {
                is NetworkResult.Success -> {
                    sharedViewModel.setResponse(res)
                    callsAdapter.setData(res.data!!)
                }
                is NetworkResult.Loading -> sharedViewModel.setResponse(res)
                is NetworkResult.Error -> sharedViewModel.setResponse(res)
            }
        })
        mFinanceViewModel.callsHistoryResponse.observe(viewLifecycleOwner, { res ->
            when (res) {
                is NetworkResult.Success -> {
                    sharedViewModel.setResponse(res)
                    callsAdapter.setData(res.data!!)
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
}