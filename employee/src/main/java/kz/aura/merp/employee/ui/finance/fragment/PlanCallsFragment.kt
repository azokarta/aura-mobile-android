package kz.aura.merp.employee.ui.finance.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import kz.aura.merp.employee.R
import kz.aura.merp.employee.adapter.CallsAdapter
import kz.aura.merp.employee.databinding.FragmentPlanCallsBinding
import kz.aura.merp.employee.base.NetworkResult
import kz.aura.merp.employee.viewmodel.SharedViewModel
import kz.aura.merp.employee.viewmodel.finance.PlanCallsViewModel

@AndroidEntryPoint
class PlanCallsFragment : Fragment(R.layout.fragment_plan_calls) {

    private var _binding: FragmentPlanCallsBinding? = null
    private val binding get() = _binding!!

    private val sharedViewModel: SharedViewModel by viewModels()
    private val planCallsViewModel: PlanCallsViewModel by viewModels()
    private val callsAdapter: CallsAdapter by lazy { CallsAdapter() }
    private var contractId: Long? = null
    private var checkedButtonId: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            contractId = it.getLong("contractId")
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentPlanCallsBinding.bind(view)

        with (binding) {
            lifecycleOwner = this@PlanCallsFragment
            sharedViewModel = this@PlanCallsFragment.sharedViewModel

            toggleButton.addOnButtonCheckedListener { _, checkedId, isChecked ->
                val callHistoryRes = planCallsViewModel.callHistoryResponse.value
                val lastMonthCallsRes = planCallsViewModel.lastMonthCallsByContractIdResponse.value

                checkedButtonId = checkedId
                if (isChecked) {
                    when (checkedId) {
                        binding.callsForMonthBtn.id -> {
                            if (lastMonthCallsRes is NetworkResult.Success) {
                                callsAdapter.submitList(lastMonthCallsRes.data?.data)
                            }
                        }
                        binding.callsHistoryBtn.id -> {
                            if (callHistoryRes is NetworkResult.Success) {
                                callsAdapter.submitList(callHistoryRes.data?.data)
                            } else {
                                planCallsViewModel.fetchCallHistory(contractId!!)
                            }
                        }
                    }
                }
            }
        }

        checkedButtonId = binding.toggleButton.checkedButtonId

        setupRecyclerView()

        setupObservers()

        callRequests()
    }

    private fun callRequests() {
        planCallsViewModel.fetchLastMonthCallsByContractId(contractId!!)
    }

    private fun setupRecyclerView() {
        binding.recyclerView.adapter = callsAdapter
    }

    private fun setupObservers() {
        planCallsViewModel.lastMonthCallsByContractIdResponse.observe(viewLifecycleOwner, { res ->
            when (res) {
                is NetworkResult.Success -> {
                    sharedViewModel.setResponse(res)
                    val lastMonthCalls = res.data?.data
                    callsAdapter.submitList(lastMonthCalls)
                }
                is NetworkResult.Loading -> sharedViewModel.setResponse(res)
                is NetworkResult.Error -> sharedViewModel.setResponse(res)
            }
        })
        planCallsViewModel.callHistoryResponse.observe(viewLifecycleOwner, { res ->
            when (res) {
                is NetworkResult.Success -> {
                    sharedViewModel.setResponse(res)
                    val callHistory = res.data?.data
                    callsAdapter.submitList(callHistory)
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

//    override fun onRefresh() {
//        sharedViewModel.setLoadingType(LoadingType.SWIPE_REFRESH)
//        println(checkedButtonId)
//        when (checkedButtonId) {
//            binding.callsForMonthBtn.id -> {
//                callRequests()
//            }
//            binding.callsHistoryBtn.id -> {
//                mFinanceViewModel.fetchCallHistory(contractId!!)
//            }
//        }
//    }
}