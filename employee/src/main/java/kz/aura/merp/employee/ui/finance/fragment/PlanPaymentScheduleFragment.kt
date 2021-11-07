package kz.aura.merp.employee.ui.finance.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import kz.aura.merp.employee.R
import kz.aura.merp.employee.adapter.PaymentScheduleAdapter
import kz.aura.merp.employee.databinding.FragmentPlanPaymentScheduleBinding
import kz.aura.merp.employee.base.NetworkResult
import kz.aura.merp.employee.viewmodel.SharedViewModel
import kz.aura.merp.employee.viewmodel.finance.PlanPaymentScheduleViewModel

@AndroidEntryPoint
class PlanPaymentScheduleFragment : Fragment(R.layout.fragment_plan_payment_schedule) {

    private var _binding: FragmentPlanPaymentScheduleBinding? = null
    private val binding get() = _binding!!

    private val sharedViewModel: SharedViewModel by viewModels()
    private val planPaymentScheduleViewModel: PlanPaymentScheduleViewModel by viewModels()
    private val paymentScheduleAdapter: PaymentScheduleAdapter by lazy { PaymentScheduleAdapter() }
    private var contractId: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            contractId = it.getLong("contractId")
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentPlanPaymentScheduleBinding.bind(view)

        with (binding) {
            lifecycleOwner = this@PlanPaymentScheduleFragment
            sharedViewModel = this@PlanPaymentScheduleFragment.sharedViewModel
            error.restart.setOnClickListener { callRequests() }
        }

        setupRecyclerView()

        setupObservers()

        callRequests()
    }

    private fun callRequests() {
        planPaymentScheduleViewModel.fetchPaymentSchedule(contractId)
    }

    private fun setupObservers() {
        planPaymentScheduleViewModel.paymentScheduleResponse.observe(viewLifecycleOwner, { res ->
            when (res) {
                is NetworkResult.Success -> {
                    sharedViewModel.setResponse(res)
                    val paymentSchedule = res.data?.data
                    paymentScheduleAdapter.submitList(paymentSchedule)
                }
                is NetworkResult.Loading -> sharedViewModel.setResponse(res)
                is NetworkResult.Error -> sharedViewModel.setResponse(res)
            }
        })
    }

    private fun setupRecyclerView() {
        binding.recyclerView.adapter = paymentScheduleAdapter
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}