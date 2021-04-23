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
import kz.aura.merp.employee.adapter.ContributionsAdapter
import kz.aura.merp.employee.databinding.FragmentContributionsBinding
import kz.aura.merp.employee.util.NetworkResult
import kz.aura.merp.employee.util.declareErrorByStatus
import kz.aura.merp.employee.util.verifyAvailableNetwork
import kz.aura.merp.employee.viewmodel.FinanceViewModel
import kz.aura.merp.employee.viewmodel.SharedViewModel

@AndroidEntryPoint
class ContributionsFragment : Fragment() {

    private var _binding: FragmentContributionsBinding? = null
    private val binding get() = _binding!!

    private val mFinanceViewModel: FinanceViewModel by activityViewModels()
    private val mSharedViewModel: SharedViewModel by activityViewModels()
    private val contributionsAdapter: ContributionsAdapter by lazy { ContributionsAdapter() }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentContributionsBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        binding.mSharedViewModel = mSharedViewModel

        setupRecyclerView()

        setupObservers()

        // Fetch contributions
        mFinanceViewModel.fetchContributions()

        return binding.root
    }

    private fun setupRecyclerView() {
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = contributionsAdapter
        binding.recyclerView.isNestedScrollingEnabled = false
    }

    private fun setupObservers() {
        mFinanceViewModel.contributionsResponse.observe(viewLifecycleOwner, { res ->
            when (res) {
                is NetworkResult.Success -> {
                    mSharedViewModel.hideLoading(res.data.isNullOrEmpty())
                    contributionsAdapter.setData(res.data!!)
                }
                is NetworkResult.Loading -> mSharedViewModel.showLoading()
                is NetworkResult.Error -> {
                    mSharedViewModel.hideLoading(res.data.isNullOrEmpty())
                    checkError(res)
                }
            }
        })
    }

    private fun <T> checkError(res: NetworkResult.Error<T>) {
        if (!verifyAvailableNetwork(requireContext())) {
            binding.networkDisconnected.root.isVisible = true
        } else {
            declareErrorByStatus(res.message, res.status, requireContext())
        }
    }
}