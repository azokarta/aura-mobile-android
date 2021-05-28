package kz.aura.merp.employee.ui.fragment.finance

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import dagger.hilt.android.AndroidEntryPoint
import kz.aura.merp.employee.adapter.DailyPlanAdapter
import kz.aura.merp.employee.databinding.FragmentDailyPlanBinding
import kz.aura.merp.employee.util.LoadingType
import kz.aura.merp.employee.util.NetworkResult
import kz.aura.merp.employee.viewmodel.FinanceViewModel
import kz.aura.merp.employee.viewmodel.SharedViewModel

@AndroidEntryPoint
class DailyPlanFragment : Fragment(), SwipeRefreshLayout.OnRefreshListener {

    private var _binding: FragmentDailyPlanBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private lateinit var financeViewModel: FinanceViewModel
    private lateinit var sharedViewModel: SharedViewModel
    private val plansAdapter: DailyPlanAdapter by lazy { DailyPlanAdapter() }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        sharedViewModel = ViewModelProvider(this).get(SharedViewModel::class.java)
        financeViewModel = ViewModelProvider(this).get(FinanceViewModel::class.java)

        _binding = FragmentDailyPlanBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        binding.sharedViewModel = sharedViewModel
        val root: View = binding.root

        binding.swipeRefresh.setOnRefreshListener(this)

        setupRecyclerView()

        setupObservers()

        callRequests()

        return root
    }

    private fun callRequests() {
        financeViewModel.fetchDailyPlan()
    }

    private fun setupObservers() {
        financeViewModel.dailyPlanResponse.observe(viewLifecycleOwner, { res ->
            when (res) {
                is NetworkResult.Success -> {
                    sharedViewModel.setResponse(res)
                    plansAdapter.setData(res.data!!)
                }
                is NetworkResult.Loading -> sharedViewModel.setResponse(res)
                is NetworkResult.Error -> sharedViewModel.setResponse(res)
            }
        })
    }

    private fun setupRecyclerView() {
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = plansAdapter
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