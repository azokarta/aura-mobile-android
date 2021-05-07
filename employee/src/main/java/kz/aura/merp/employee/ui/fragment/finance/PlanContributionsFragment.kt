package kz.aura.merp.employee.ui.fragment.finance

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import io.nlopez.smartlocation.SmartLocation
import kz.aura.merp.employee.R
import kz.aura.merp.employee.adapter.ContributionsAdapter
import kz.aura.merp.employee.databinding.FragmentPlanContributionsBinding
import kz.aura.merp.employee.model.*
import kz.aura.merp.employee.ui.activity.AddContributionActivity
import kz.aura.merp.employee.ui.dialog.*
import kz.aura.merp.employee.util.NetworkResult
import kz.aura.merp.employee.util.ProgressDialog
import kz.aura.merp.employee.util.declareErrorByStatus
import kz.aura.merp.employee.util.verifyAvailableNetwork
import kz.aura.merp.employee.viewmodel.FinanceViewModel

@AndroidEntryPoint
class PlanContributionsFragment : Fragment() {

    private var _binding: FragmentPlanContributionsBinding? = null
    private val binding get() = _binding!!

    private lateinit var plan: Plan
    private val mFinanceViewModel: FinanceViewModel by activityViewModels()
    private lateinit var progressDialog: ProgressDialog
    private val contributionsAdapter: ContributionsAdapter by lazy { ContributionsAdapter() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            plan = it.getParcelable(ARG_PARAM1)!!
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlanContributionsBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this

        // Initialize Loading Dialog
        progressDialog = ProgressDialog(requireContext())

        setupRecyclerView()

        setupObservers()

        binding.addContribution.setOnClickListener {
            val intent = Intent(requireContext(), AddContributionActivity::class.java)
            intent.putExtra("contractId", plan.contractId!!)
            intent.putExtra("clientPhoneNumbers", plan.customerPhoneNumbers!!.toTypedArray())
            intent.putExtra("businessProcessId", plan.planBusinessProcessId)
            startActivityForResult(intent, 1);
        }

        mFinanceViewModel.fetchPlanContributions(plan.contractId!!)

        // If network is disconnected and user clicks restart, get data again
        binding.networkDisconnected.restart.setOnClickListener {
            if (verifyAvailableNetwork(requireContext())) {
                mFinanceViewModel.fetchPlanContributions(plan.contractId!!)
                binding.progressBar.isVisible = true
                binding.recyclerView.isVisible = true
                binding.networkDisconnected.root.isVisible = false
            }
        }

        return binding.root
    }

    private fun setupRecyclerView() {
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = contributionsAdapter
        binding.recyclerView.isNestedScrollingEnabled = false
    }

    private fun setupObservers() {
        mFinanceViewModel.updatedPlanResponse.observe(viewLifecycleOwner, { res ->
            when (res) {
                is NetworkResult.Success -> {
                    plan = res.data!!
                }
                is NetworkResult.Loading -> {}
                is NetworkResult.Error -> {}
            }
        })
        mFinanceViewModel.contributionsResponse.observe(viewLifecycleOwner, { res ->
            when (res) {
                is NetworkResult.Success -> {
                    showLoadingOrNoData(false, res.data.isNullOrEmpty())
                    contributionsAdapter.setData(res.data!!)
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                val contributions = data?.getParcelableArrayListExtra<Contribution>("contributions")!!.toCollection(ArrayList<Contribution>())
                showLoadingOrNoData(false, contributions.isNullOrEmpty())
                contributionsAdapter.setData(contributions)
                Snackbar.make(binding.addContribution, R.string.successfullySaved, Snackbar.LENGTH_SHORT)
                    .show()
            }
        }
    }



    companion object {
        private const val ARG_PARAM1 = "plan"

        @JvmStatic
        fun newInstance(plan: Plan) =
            PlanContributionsFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(ARG_PARAM1, plan)
                }
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}