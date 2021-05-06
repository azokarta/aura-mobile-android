package kz.aura.merp.employee.ui.fragment.finance

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import kz.aura.merp.employee.adapter.CallsAdapter
import kz.aura.merp.employee.databinding.FragmentPlanCallsBinding
import kz.aura.merp.employee.util.NetworkResult
import kz.aura.merp.employee.util.declareErrorByStatus
import kz.aura.merp.employee.util.verifyAvailableNetwork
import kz.aura.merp.employee.viewmodel.FinanceViewModel

@AndroidEntryPoint
class PlanCallsFragment : Fragment() {

    private var _binding: FragmentPlanCallsBinding? = null
    private val binding get() = _binding!!

    private val mFinanceViewModel: FinanceViewModel by activityViewModels()
    private val callsAdapter: CallsAdapter by lazy { CallsAdapter() }
    private var contractId: Long = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            contractId = it.getLong(ARG_PARAM1)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlanCallsBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this

        setupRecyclerView()

        setupObservers()

        // Fetch calls
        mFinanceViewModel.fetchCallHistory(contractId)

        // If network is disconnected and user clicks restart, get data again
        binding.networkDisconnected.restart.setOnClickListener {
            if (verifyAvailableNetwork(requireContext())) {
                mFinanceViewModel.fetchCallHistory(contractId)
                binding.progressBar.isVisible = true
                binding.recyclerView.isVisible = true
                binding.networkDisconnected.root.isVisible = false
            }
        }

        binding.toggleButton.addOnButtonCheckedListener { toggleButton, checkedId, isChecked ->
            when (checkedId) {
                1 -> {
                    callsAdapter.setData(mFinanceViewModel.callsResponse.value!!.data!!)
                }
                2 -> {
                    callsAdapter.setData(mFinanceViewModel.callsHistoryResponse.value!!.data!!)
                }
            }
        }

        return binding.root
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
                    showLoadingOrNoData(false, res.data.isNullOrEmpty())
                    callsAdapter.setData(res.data!!)
                }
                is NetworkResult.Loading -> {
                    showLoadingOrNoData(true)
                }
                is NetworkResult.Error -> {
                    showLoadingOrNoData(false, res.data.isNullOrEmpty())
                    checkError(res)
                }
            }
        })
    }

    private fun showLoadingOrNoData(visibility: Boolean, dataIsEmpty: Boolean = true) {
        if (visibility) {
            binding.emptyData = true
            binding.dataReceived = false
        } else {
            binding.emptyData = dataIsEmpty
            binding.dataReceived = true
        }
    }

    private fun <T> checkError(res: NetworkResult.Error<T>) {
        if (!verifyAvailableNetwork(requireContext())) {
            binding.networkDisconnected.root.isVisible = true
        } else {
            declareErrorByStatus(res.message, res.status, requireContext())
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val ARG_PARAM1 = "contractId"

        @JvmStatic
        fun newInstance(contractId: Long) =
            PlanCallsFragment().apply {
                arguments = Bundle().apply {
                    putLong(ARG_PARAM1, contractId)
                }
            }
    }
}