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
import kz.aura.merp.employee.R
import kz.aura.merp.employee.adapter.ScheduledCallsAdapter
import kz.aura.merp.employee.databinding.FragmentPlanScheduledCallsBinding
import kz.aura.merp.employee.util.NetworkResult
import kz.aura.merp.employee.util.declareErrorByStatus
import kz.aura.merp.employee.util.verifyAvailableNetwork
import kz.aura.merp.employee.viewmodel.FinanceViewModel

@AndroidEntryPoint
class PlanScheduledCallsFragment : Fragment() {

    private var _binding: FragmentPlanScheduledCallsBinding? = null
    private val binding get() = _binding!!

    private val mFinanceViewModel: FinanceViewModel by activityViewModels()
    private val scheduledCallsAdapter: ScheduledCallsAdapter by lazy { ScheduledCallsAdapter() }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlanScheduledCallsBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this

        setupRecyclerView()

        setupObservers()

        // Fetch contributions
        mFinanceViewModel.fetchScheduledCalls()

        // If network is disconnected and user clicks restart, get data again
        binding.networkDisconnected.restart.setOnClickListener {
            if (verifyAvailableNetwork(requireContext())) {
                mFinanceViewModel.fetchContributions()
                binding.progressBar.isVisible = true
                binding.recyclerView.isVisible = true
                binding.networkDisconnected.root.isVisible = false
            }
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun <T> checkError(res: NetworkResult.Error<T>) {
        if (!verifyAvailableNetwork(requireContext())) {
            binding.networkDisconnected.root.isVisible = true
        } else {
            declareErrorByStatus(res.message, res.status, requireContext())
        }
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

    private fun setupRecyclerView() {
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = scheduledCallsAdapter
        binding.recyclerView.isNestedScrollingEnabled = false
    }

    private fun setupObservers() {
        mFinanceViewModel.scheduledCallsResponse.observe(viewLifecycleOwner, { res ->
            when (res) {
                is NetworkResult.Success -> {
                    showLoadingOrNoData(false, res.data.isNullOrEmpty())
                    scheduledCallsAdapter.setData(res.data!!)
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
}