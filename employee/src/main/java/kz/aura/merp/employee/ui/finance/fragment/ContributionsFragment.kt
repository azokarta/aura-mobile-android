package kz.aura.merp.employee.ui.finance.fragment

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import dagger.hilt.android.AndroidEntryPoint
import kz.aura.merp.employee.R
import kz.aura.merp.employee.adapter.ContributionsAdapter
import kz.aura.merp.employee.databinding.FragmentContributionsBinding
import kz.aura.merp.employee.util.LoadingType
import kz.aura.merp.employee.base.NetworkResult
import kz.aura.merp.employee.viewmodel.SharedViewModel
import kz.aura.merp.employee.viewmodel.finance.ContributionsViewModel

@AndroidEntryPoint
class ContributionsFragment : Fragment(R.layout.fragment_contributions), SwipeRefreshLayout.OnRefreshListener {

    private var _binding: FragmentContributionsBinding? = null
    private val binding get() = _binding!!

    private val sharedViewModel: SharedViewModel by viewModels()
    private val contributionsViewModel: ContributionsViewModel by viewModels()
    private val contributionsAdapter: ContributionsAdapter by lazy { ContributionsAdapter() }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding = FragmentContributionsBinding.bind(view)

        with (binding) {
            lifecycleOwner = this@ContributionsFragment
            sharedViewModel = this@ContributionsFragment.sharedViewModel
            swipeRefresh.setOnRefreshListener(this@ContributionsFragment)
            error.restart.setOnClickListener { callRequests() }
        }

        setupRecyclerView()

        setupObservers()

        callRequests()
    }

    private fun callRequests() {
        contributionsViewModel.fetchContributions()
    }

    private fun setupRecyclerView() {
        binding.recyclerView.adapter = contributionsAdapter
    }

    private fun setupObservers() {
        contributionsViewModel.contributionsResponse.observe(viewLifecycleOwner, { res ->
            when (res) {
                is NetworkResult.Success -> {
                    sharedViewModel.setResponse(res)

                    val contributions = res.data?.data

                    contributionsAdapter.submitList(contributions)
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