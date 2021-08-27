package kz.aura.merp.employee.ui.finance.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import kz.aura.merp.employee.adapter.PaymentScheduleAdapter
import kz.aura.merp.employee.databinding.FragmentPlanPaymentScheduleBinding
import kz.aura.merp.employee.base.NetworkResult
import kz.aura.merp.employee.viewmodel.FinanceViewModel
import kz.aura.merp.employee.viewmodel.SharedViewModel

@AndroidEntryPoint
class PlanPaymentScheduleFragment : Fragment() {

    private var _binding: FragmentPlanPaymentScheduleBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private lateinit var sharedViewModel: SharedViewModel
    private lateinit var financeViewModel: FinanceViewModel
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
        financeViewModel = ViewModelProvider(this).get(FinanceViewModel::class.java)

        _binding = FragmentPlanPaymentScheduleBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        binding.sharedViewModel = sharedViewModel
        val root: View = binding.root

        setupRecyclerView()

        setupObservers()

        callRequests()

        return root
    }

    private fun callRequests() {
        financeViewModel.fetchPaymentSchedule(contractId)
    }

    private fun setupObservers() {
        financeViewModel.paymentScheduleResponse.observe(viewLifecycleOwner, { res ->
            when (res) {
                is NetworkResult.Success -> {
                    sharedViewModel.setResponse(res)
                    paymentScheduleAdapter.setData(res.data!!, currency)
                }
                is NetworkResult.Loading -> sharedViewModel.setResponse(res)
                is NetworkResult.Error -> sharedViewModel.setResponse(res)
            }
        })
        financeViewModel.planResponse.observe(viewLifecycleOwner, { res ->
            when (res) {
                is NetworkResult.Success -> {
                    currency = res.data!!.contractCurrencyName
                }
                is NetworkResult.Loading -> {}
                is NetworkResult.Error -> sharedViewModel.setResponse(res)
            }
        })
    }

    private fun setupRecyclerView() {
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = paymentScheduleAdapter
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}