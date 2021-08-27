package kz.aura.merp.employee.ui.finance.fragment

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import dagger.hilt.android.AndroidEntryPoint
import kz.aura.merp.employee.R
import kz.aura.merp.employee.adapter.CallsAdapter
import kz.aura.merp.employee.databinding.FragmentCallsBinding
import kz.aura.merp.employee.util.LoadingType
import kz.aura.merp.employee.base.NetworkResult
import kz.aura.merp.employee.viewmodel.SharedViewModel
import kz.aura.merp.employee.viewmodel.finance.CallsViewModel

@AndroidEntryPoint
class CallsFragment : Fragment(R.layout.fragment_calls), SwipeRefreshLayout.OnRefreshListener {

    private var _binding: FragmentCallsBinding? = null
    private val binding get() = _binding!!

    private val sharedViewModel: SharedViewModel by viewModels()
    private val callsViewModel: CallsViewModel by viewModels()
    private val callsAdapter: CallsAdapter by lazy { CallsAdapter() }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding = FragmentCallsBinding.bind(view)

        with(binding) {
            lifecycleOwner = this@CallsFragment
            sharedViewModel = this@CallsFragment.sharedViewModel
            swipeRefresh.setOnRefreshListener(this@CallsFragment)
            error.restart.setOnClickListener { callRequests() }
        }

        setupRecyclerView()

        setupObservers()

        callRequests()
    }

    private fun callRequests() {
        callsViewModel.fetchLastMonthCalls()
    }

    private fun setupRecyclerView() {
        binding.recyclerView.adapter = callsAdapter
    }

    private fun setupObservers() {
        callsViewModel.lastMonthCallsResponse.observe(viewLifecycleOwner, { res ->
            when (res) {
                is NetworkResult.Success -> {
                    sharedViewModel.setResponse(res)
                    val calls = res.data?.data
                    callsAdapter.submitList(calls)
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

    override fun onRefresh() {
        sharedViewModel.setLoadingType(LoadingType.SWIPE_REFRESH)
        callRequests()
    }
}