package kz.aura.merp.employee.ui.fragment.finance

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import kz.aura.merp.employee.adapter.PaymentScheduleAdapter
import kz.aura.merp.employee.databinding.FragmentPlanPaymentScheduleBinding
import kz.aura.merp.employee.util.NetworkResult
import kz.aura.merp.employee.util.declareErrorByStatus
import kz.aura.merp.employee.util.verifyAvailableNetwork
import kz.aura.merp.employee.viewmodel.FinanceViewModel
import kz.aura.merp.employee.viewmodel.SharedViewModel

@AndroidEntryPoint
class PlanPaymentScheduleFragment : Fragment() {

    private var _binding: FragmentPlanPaymentScheduleBinding? = null
    private val binding get() = _binding!!
    private val mFinanceViewModel: FinanceViewModel by activityViewModels()
    private val paymentScheduleAdapter: PaymentScheduleAdapter by lazy { PaymentScheduleAdapter() }
    private var contractId: Long = 0
    private var currency: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            contractId = it.getLong(ARG_PARAM1)
            currency = it.getString(ARG_PARAM2)!!
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlanPaymentScheduleBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this

        setupRecyclerView()

        mFinanceViewModel.paymentScheduleResponse.observe(viewLifecycleOwner, { res ->
            when (res) {
                is NetworkResult.Success -> {
                    showLoadingOrNoData(false, res.data.isNullOrEmpty())
                    paymentScheduleAdapter.setData(res.data!!, currency)
                }
                is NetworkResult.Loading -> showLoadingOrNoData(true)
                is NetworkResult.Error -> {
                    showLoadingOrNoData(false, res.data.isNullOrEmpty())
                    checkError(res)
                }
            }
        })

        mFinanceViewModel.fetchPaymentSchedule(contractId)

        // If network is disconnected and user clicks restart, get data again
        binding.networkDisconnected.restart.setOnClickListener {
            if (verifyAvailableNetwork(requireContext())) {
                mFinanceViewModel.fetchPaymentSchedule(contractId)
                binding.progressBar.isVisible = true
                binding.recyclerView.isVisible = true
                binding.networkDisconnected.root.isVisible = false
            }
        }

        return binding.root
    }

    private fun setupRecyclerView() {
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = paymentScheduleAdapter
        binding.recyclerView.isNestedScrollingEnabled = false
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
            declareErrorByStatus(res.message, res.status, requireContext())
        }
    }

    companion object {
        private const val ARG_PARAM1 = "contractId"
        private const val ARG_PARAM2 = "currency"

        @JvmStatic
        fun newInstance(contractId: Long, currency: String) =
            PlanPaymentScheduleFragment().apply {
                arguments = Bundle().apply {
                    putLong(ARG_PARAM1, contractId)
                    putString(ARG_PARAM2, currency)
                }
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}