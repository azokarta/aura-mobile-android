package kz.aura.merp.employee.ui.fragment.finance

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import dagger.hilt.android.AndroidEntryPoint
import kz.aura.merp.employee.adapter.ContributionsAdapter
import kz.aura.merp.employee.databinding.FragmentPlanContributionsBinding
import kz.aura.merp.employee.model.*
import kz.aura.merp.employee.ui.dialog.*
import kz.aura.merp.employee.util.*
import kz.aura.merp.employee.viewmodel.FinanceViewModel
import kz.aura.merp.employee.viewmodel.SharedViewModel

@AndroidEntryPoint
class PlanContributionsFragment : Fragment(), SwipeRefreshLayout.OnRefreshListener {

    private var _binding: FragmentPlanContributionsBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private var contractId: Long? = null
    private val mFinanceViewModel: FinanceViewModel by activityViewModels()
    private lateinit var sharedViewModel: SharedViewModel
    private lateinit var progressDialog: ProgressDialog
    private val contributionsAdapter: ContributionsAdapter by lazy { ContributionsAdapter() }

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

        _binding = FragmentPlanContributionsBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        binding.sharedViewModel = sharedViewModel

        // Initialize Loading Dialog
        progressDialog = ProgressDialog(requireContext())

        setupRecyclerView()

        setupObservers()

        callRequests()

        binding.swipeRefresh.setOnRefreshListener(this)

        return binding.root
    }

    private fun callRequests() {
        mFinanceViewModel.fetchContributionsByContractId(contractId!!)
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
                    sharedViewModel.setResponse(res)
                    contributionsAdapter.setData(res.data!!)
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