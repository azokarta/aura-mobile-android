package kz.aura.merp.employee.ui.finance.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import kz.aura.merp.employee.adapter.CallsAdapter
import kz.aura.merp.employee.databinding.FragmentPlanCallsBinding
import kz.aura.merp.employee.base.NetworkResult
import kz.aura.merp.employee.viewmodel.FinanceViewModel
import kz.aura.merp.employee.viewmodel.SharedViewModel

@AndroidEntryPoint
class PlanCallsFragment : Fragment() {

    private var _binding: FragmentPlanCallsBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private lateinit var sharedViewModel: SharedViewModel
    private lateinit var financeViewModel: FinanceViewModel
    private val callsAdapter: CallsAdapter by lazy { CallsAdapter() }
    private var contractId: Long? = null
    private var checkedButtonId: Int? = null

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
        financeViewModel = ViewModelProvider(this).get(FinanceViewModel::class.java)

        _binding = FragmentPlanCallsBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        binding.sharedViewModel = sharedViewModel
        val root: View = binding.root

        checkedButtonId = binding.toggleButton.checkedButtonId

        setupRecyclerView()

        setupObservers()

        callRequests()

        binding.toggleButton.addOnButtonCheckedListener { _, checkedId, isChecked ->
            val callsHistory = financeViewModel.callsHistoryResponse.value
            val calls = financeViewModel.callsResponse.value
            checkedButtonId = checkedId
            if (isChecked) {
                when (checkedId) {
                    binding.callsForMonthBtn.id -> {
                        if (calls is NetworkResult.Success) {
                            callsAdapter.setData(financeViewModel.callsResponse.value!!.data!!)
                        }
                    }
                    binding.callsHistoryBtn.id -> {
                        if (callsHistory is NetworkResult.Success) {
                            callsAdapter.setData(financeViewModel.callsHistoryResponse.value!!.data!!)
                        } else {
                            financeViewModel.fetchCallHistory(contractId!!)
                        }
                    }
                }
            }
        }

        return root
    }

    private fun callRequests() {
        financeViewModel.fetchCallHistory(contractId!!)
    }

    private fun setupRecyclerView() {
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = callsAdapter
    }

    private fun setupObservers() {
        financeViewModel.callsResponse.observe(viewLifecycleOwner, { res ->
            when (res) {
                is NetworkResult.Success -> {
                    sharedViewModel.setResponse(res)
                    callsAdapter.setData(res.data!!)
                }
                is NetworkResult.Loading -> sharedViewModel.setResponse(res)
                is NetworkResult.Error -> sharedViewModel.setResponse(res)
            }
        })
        financeViewModel.callsHistoryResponse.observe(viewLifecycleOwner, { res ->
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