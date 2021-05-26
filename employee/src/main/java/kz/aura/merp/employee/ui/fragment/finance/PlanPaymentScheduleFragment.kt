package kz.aura.merp.employee.ui.fragment.finance

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import dagger.hilt.android.AndroidEntryPoint
import kz.aura.merp.employee.adapter.PaymentScheduleAdapter
import kz.aura.merp.employee.databinding.FragmentPlanPaymentScheduleBinding
import kz.aura.merp.employee.util.LoadingType
import kz.aura.merp.employee.util.NetworkResult
import kz.aura.merp.employee.util.declareErrorByStatus
import kz.aura.merp.employee.util.verifyAvailableNetwork
import kz.aura.merp.employee.viewmodel.FinanceViewModel
import kz.aura.merp.employee.viewmodel.SharedViewModel

@AndroidEntryPoint
class PlanPaymentScheduleFragment : Fragment(), SwipeRefreshLayout.OnRefreshListener {

    private var _binding: FragmentPlanPaymentScheduleBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private lateinit var sharedViewModel: SharedViewModel
    private val mFinanceViewModel: FinanceViewModel by activityViewModels()
    private val paymentScheduleAdapter: PaymentScheduleAdapter by lazy { PaymentScheduleAdapter() }
    private var contractId: Long = 0
    private var currency: String = ""

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

        _binding = FragmentPlanPaymentScheduleBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        binding.sharedViewModel = sharedViewModel

        setupRecyclerView()

        setupObservers()

        callRequests()

        return binding.root
    }

    private fun callRequests() {
        mFinanceViewModel.fetchPaymentSchedule(contractId)
    }

    private fun setupObservers() {
        mFinanceViewModel.planResponse.observe(viewLifecycleOwner, { res ->
            when (res) {
                is NetworkResult.Success -> {
                    sharedViewModel.setResponse(res)
                    currency = res.data!!.contractCurrencyName
                }
                is NetworkResult.Loading -> {
                    sharedViewModel.setResponse(res)
                }
                is NetworkResult.Error -> {
                    sharedViewModel.setResponse(res)
                }
            }
        })
        mFinanceViewModel.paymentScheduleResponse.observe(viewLifecycleOwner, { res ->
            when (res) {
                is NetworkResult.Success -> {
                    sharedViewModel.setResponse(res)
                    paymentScheduleAdapter.setData(res.data!!, currency)
                }
                is NetworkResult.Loading -> sharedViewModel.setResponse(res)
                is NetworkResult.Error -> {
                    sharedViewModel.setResponse(res)
                }
            }
        })
    }

    private fun setupRecyclerView() {
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = paymentScheduleAdapter
        binding.recyclerView.isNestedScrollingEnabled = false
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