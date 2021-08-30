package kz.aura.merp.employee.ui.finance.fragment

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import dagger.hilt.android.AndroidEntryPoint
import kz.aura.merp.employee.R
import kz.aura.merp.employee.adapter.ScheduledCallsAdapter
import kz.aura.merp.employee.databinding.FragmentScheduledCallsBinding
import kz.aura.merp.employee.util.LoadingType
import kz.aura.merp.employee.base.NetworkResult
import kz.aura.merp.employee.viewmodel.SharedViewModel
import kz.aura.merp.employee.viewmodel.finance.ScheduledCallsViewModel
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

@AndroidEntryPoint
class ScheduledCallsFragment : Fragment(R.layout.fragment_scheduled_calls), SwipeRefreshLayout.OnRefreshListener {

    private var _binding: FragmentScheduledCallsBinding? = null
    private val binding get() = _binding!!

    private val sharedViewModel: SharedViewModel by viewModels()
    private val scheduledCallsViewModel: ScheduledCallsViewModel by viewModels()
    private val scheduledCallsAdapter: ScheduledCallsAdapter by lazy { ScheduledCallsAdapter() }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentScheduledCallsBinding.bind(view)

        with (binding) {
            lifecycleOwner = this@ScheduledCallsFragment
            sharedViewModel = this@ScheduledCallsFragment.sharedViewModel

            swipeRefresh.setOnRefreshListener(this@ScheduledCallsFragment)
        }

        setupRecyclerView()

        setupObservers()

        callRequests()
    }

    private fun callRequests() {
        scheduledCallsViewModel.fetchLastMonthScheduledCalls()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupRecyclerView() {
        binding.recyclerView.adapter = scheduledCallsAdapter
    }

    private fun setupObservers() {
        scheduledCallsViewModel.lastMonthScheduledCallsResponse.observe(viewLifecycleOwner, { res ->
            when (res) {
                is NetworkResult.Success -> {
                    sharedViewModel.setResponse(res)
                    val lastMonthScheduledCalls = res.data?.data
                    scheduledCallsAdapter.submitList(lastMonthScheduledCalls)
                }
                is NetworkResult.Loading -> sharedViewModel.setResponse(res)
                is NetworkResult.Error -> sharedViewModel.setResponse(res)
            }
        })
    }

    override fun onRefresh() {
        sharedViewModel.setLoadingType(LoadingType.SWIPE_REFRESH)
        callRequests()
    }
}