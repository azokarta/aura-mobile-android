package kz.aura.merp.employee.ui.fragment.finance

import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import kz.aura.merp.employee.R
import kz.aura.merp.employee.adapter.DailyPlanAdapter
import kz.aura.merp.employee.adapter.PlanAdapter
import kz.aura.merp.employee.databinding.FragmentDailyPlanBinding
import kz.aura.merp.employee.ui.activity.SettingsActivity
import kz.aura.merp.employee.util.NetworkResult
import kz.aura.merp.employee.util.declareErrorByStatus
import kz.aura.merp.employee.util.verifyAvailableNetwork
import kz.aura.merp.employee.viewmodel.FinanceViewModel

class DailyPlanFragment : Fragment() {

    private var _binding: FragmentDailyPlanBinding? = null
    private val binding get() = _binding!!

    private val mFinanceViewModel: FinanceViewModel by activityViewModels()
    private val plansAdapter: DailyPlanAdapter by lazy { DailyPlanAdapter() }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDailyPlanBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this

        // Setup RecyclerView
        setupRecyclerView()

        setupObservers()

        callRequests()

        return binding.root
    }

    private fun callRequests() {
        val dailyPlan = mFinanceViewModel.dailyPlanResponse.value?.data
        if (dailyPlan.isNullOrEmpty()) {
            mFinanceViewModel.fetchDailyPlan()
        }
    }

    private fun setupObservers() {
        mFinanceViewModel.dailyPlanResponse.observe(viewLifecycleOwner, { res ->
            when (res) {
                is NetworkResult.Success -> {
                    showLoadingOrNoData(false, res.data.isNullOrEmpty())
                    plansAdapter.setData(res.data!!)
                }
                is NetworkResult.Loading -> showLoadingOrNoData(true)
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
            binding.recyclerView.isVisible = false
        } else {
//            declareErrorByStatus(res.message, res.status, requireContext())
        }
    }

    private fun setupRecyclerView() {
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = plansAdapter
        binding.recyclerView.isNestedScrollingEnabled = false
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}