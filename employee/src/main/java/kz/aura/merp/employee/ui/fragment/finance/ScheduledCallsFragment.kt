package kz.aura.merp.employee.ui.fragment.finance

import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import kz.aura.merp.employee.R
import kz.aura.merp.employee.adapter.CallsAdapter
import kz.aura.merp.employee.adapter.ScheduledCallsAdapter
import kz.aura.merp.employee.databinding.FragmentScheduledCallsBinding
import kz.aura.merp.employee.ui.activity.SettingsActivity
import kz.aura.merp.employee.util.NetworkResult
import kz.aura.merp.employee.util.declareErrorByStatus
import kz.aura.merp.employee.util.verifyAvailableNetwork
import kz.aura.merp.employee.view.OnSelectPhoneNumber
import kz.aura.merp.employee.viewmodel.FinanceViewModel

@AndroidEntryPoint
class ScheduledCallsFragment : Fragment() {

    private var _binding: FragmentScheduledCallsBinding? = null
    private val binding get() = _binding!!

    private lateinit var mFinanceViewModel: FinanceViewModel
    private val scheduledCallsAdapter: ScheduledCallsAdapter by lazy { ScheduledCallsAdapter() }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mFinanceViewModel = ViewModelProvider(this).get(FinanceViewModel::class.java)

        _binding = FragmentScheduledCallsBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        val root: View = binding.root

        setupRecyclerView()

        setupObservers()

        callRequests()

        // If network is disconnected and user clicks restart, get data again
        binding.networkDisconnected.restart.setOnClickListener {
            if (verifyAvailableNetwork(requireContext())) {
                mFinanceViewModel.fetchLastMonthScheduledCalls()
                binding.progressBar.isVisible = true
                binding.recyclerView.isVisible = true
                binding.networkDisconnected.root.isVisible = false
            }
        }

        return root
    }

    private fun callRequests() {
        val scheduledCalls = mFinanceViewModel.scheduledCallsResponse.value?.data
        if (scheduledCalls.isNullOrEmpty()) {
            mFinanceViewModel.fetchLastMonthScheduledCalls()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun <T> checkError(res: NetworkResult.Error<T>) {
        if (!verifyAvailableNetwork(requireContext())) {
            binding.networkDisconnected.root.isVisible = true
            binding.recyclerView.isVisible = false
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