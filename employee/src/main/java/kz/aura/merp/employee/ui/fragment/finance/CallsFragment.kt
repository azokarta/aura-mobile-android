package kz.aura.merp.employee.ui.fragment.finance

import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import dagger.hilt.android.AndroidEntryPoint
import kz.aura.merp.employee.R
import kz.aura.merp.employee.adapter.CallsAdapter
import kz.aura.merp.employee.databinding.FragmentCallsBinding
import kz.aura.merp.employee.ui.activity.SettingsActivity
import kz.aura.merp.employee.util.LoadingType
import kz.aura.merp.employee.util.NetworkResult
import kz.aura.merp.employee.util.declareErrorByStatus
import kz.aura.merp.employee.util.verifyAvailableNetwork
import kz.aura.merp.employee.viewmodel.FinanceViewModel
import kz.aura.merp.employee.viewmodel.SharedViewModel

@AndroidEntryPoint
class CallsFragment : Fragment(), SwipeRefreshLayout.OnRefreshListener {

    private var _binding: FragmentCallsBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private lateinit var sharedViewModel: SharedViewModel
    private lateinit var financeViewModel: FinanceViewModel
    private val callsAdapter: CallsAdapter by lazy { CallsAdapter() }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        sharedViewModel = ViewModelProvider(this).get(SharedViewModel::class.java)
        financeViewModel = ViewModelProvider(this).get(FinanceViewModel::class.java)

        _binding = FragmentCallsBinding.inflate(inflater, container, false)
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
        financeViewModel.fetchLastMonthCalls()
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