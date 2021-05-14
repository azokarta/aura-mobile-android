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

        setHasOptionsMenu(true)

        // Setup RecyclerView
        setupRecyclerView()

        setupObservers()

        mFinanceViewModel.fetchDailyPlan()

        return binding.root
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
        } else {
            declareErrorByStatus(res.message, res.status, requireContext())
        }
    }

    private fun setupRecyclerView() {
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = plansAdapter
        binding.recyclerView.isNestedScrollingEnabled = false
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_settings -> {
                val intent = Intent(requireContext(), SettingsActivity::class.java)
                startActivity(intent)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}