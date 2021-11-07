package kz.aura.merp.employee.ui.finance.fragment

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import kz.aura.merp.employee.R
import kz.aura.merp.employee.adapter.ContributionsAdapter
import kz.aura.merp.employee.base.NetworkResult
import kz.aura.merp.employee.databinding.FragmentPlanContributionsBinding
import kz.aura.merp.employee.util.*
import kz.aura.merp.employee.viewmodel.SharedViewModel
import kz.aura.merp.employee.viewmodel.finance.PlanContributionsViewModel

@AndroidEntryPoint
class PlanContributionsFragment : Fragment(R.layout.fragment_plan_contributions) {

    private var _binding: FragmentPlanContributionsBinding? = null
    private val binding get() = _binding!!

    private var contractId: Long? = null
    private val sharedViewModel: SharedViewModel by viewModels()
    private val planContributionsViewModel: PlanContributionsViewModel by viewModels()
    private lateinit var progressDialog: ProgressDialog
    private val contributionsAdapter: ContributionsAdapter by lazy { ContributionsAdapter() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            contractId = it.getLong("contractId")
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentPlanContributionsBinding.bind(view)

        with (binding) {
            lifecycleOwner = this@PlanContributionsFragment
            sharedViewModel = this@PlanContributionsFragment.sharedViewModel
            error.restart.setOnClickListener { callRequests() }
        }

        progressDialog = ProgressDialog(requireContext())

        setupRecyclerView()

        setupObservers()

        callRequests()
    }

    private fun callRequests() {
        planContributionsViewModel.fetchContributionsByContractId(contractId!!)
    }

    private fun setupRecyclerView() {
        binding.recyclerView.adapter = contributionsAdapter
    }

    private fun setupObservers() {
        planContributionsViewModel.contributionsByContractIdResponse.observe(viewLifecycleOwner, { res ->
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
}