package kz.aura.merp.employee.ui.finance.fragment

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.text.HtmlCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import kz.aura.merp.employee.R
import kz.aura.merp.employee.adapter.DailyPlanAdapter
import kz.aura.merp.employee.databinding.FragmentDailyPlansBinding
import kz.aura.merp.employee.ui.finance.activity.DailyPlanActivity
import kz.aura.merp.employee.util.LoadingType
import kz.aura.merp.employee.base.NetworkResult
import kz.aura.merp.employee.model.DailyPlan
import kz.aura.merp.employee.model.DailyPlanFilter
import kz.aura.merp.employee.ui.finance.dialog.DailyPlanFilterDialogFragment
import kz.aura.merp.employee.viewmodel.SharedViewModel
import kz.aura.merp.employee.viewmodel.finance.DailyPlansViewModel
import java.util.*

@AndroidEntryPoint
class DailyPlansFragment : Fragment(R.layout.fragment_daily_plans), SwipeRefreshLayout.OnRefreshListener, DailyPlanAdapter.DailyPlanListener, DailyPlanFilterDialogFragment.Listener {

    private var _binding: FragmentDailyPlansBinding? = null
    private val binding get() = _binding!!

    private val dailyPlansViewModel: DailyPlansViewModel by viewModels()
    private val sharedViewModel: SharedViewModel by viewModels()
    private val plansAdapter: DailyPlanAdapter by lazy { DailyPlanAdapter(this) }
    private var resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            dailyPlansViewModel.fetchDailyPlans()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentDailyPlansBinding.bind(view)

        // Set Menu
        setHasOptionsMenu(true)

        with (binding) {
            lifecycleOwner = this@DailyPlansFragment
            sharedViewModel = this@DailyPlansFragment.sharedViewModel
            dailyPlanFilter = DailyPlanFilter()

            swipeRefresh.setOnRefreshListener(this@DailyPlansFragment)
            explanationAboutColors.setOnClickListener(::explainAboutColors)
            error.restart.setOnClickListener { callRequests() }
        }

        setupRecyclerView()

        setupObservers()

        callRequests()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.filter_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.filter -> {
                val statuses = dailyPlansViewModel.businessProcessStatusesResponse.value?.data?.data ?: arrayListOf()
                val dialog = DailyPlanFilterDialogFragment(this, binding.dailyPlanFilter ?: DailyPlanFilter(), statuses)
                dialog.show(childFragmentManager, tag)
            }
            R.id.clearFilter -> filterPlans()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun selectDailyPlan(id: Long) {
        val intent = Intent(requireContext(), DailyPlanActivity::class.java)
        intent.putExtra("dailyPlanId", id)
        resultLauncher.launch(intent)
    }

    private fun callRequests() {
        dailyPlansViewModel.fetchDailyPlans()
        dailyPlansViewModel.fetchBusinessProcessStatuses()
    }

    private fun setupObservers() {
        dailyPlansViewModel.dailyPlansResponse.observe(viewLifecycleOwner, { res ->
            when (res) {
                is NetworkResult.Success -> {
                    sharedViewModel.setResponse(res)
                    filterPlans(binding.dailyPlanFilter ?: DailyPlanFilter())
                }
                is NetworkResult.Loading -> sharedViewModel.setResponse(res)
                is NetworkResult.Error -> sharedViewModel.setResponse(res)
            }
        })
    }

    private fun explainAboutColors(view: View) {
        MaterialAlertDialogBuilder(requireActivity())
            .setView(R.layout.explanation_about_colors)
            .setTitle(resources.getString(R.string.explanation_about_colors))
            .show()
    }

    private fun setupRecyclerView() {
        with (binding) {
            recyclerView.adapter = plansAdapter
            recyclerView.isNestedScrollingEnabled = false
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onRefresh() {
        sharedViewModel.setLoadingType(LoadingType.SWIPE_REFRESH)
        callRequests()
    }

    override fun apply(dailyPlanFilter: DailyPlanFilter) {
        filterPlans(dailyPlanFilter)
    }

    private fun filterPlans(dailyPlanFilter: DailyPlanFilter = DailyPlanFilter()) {
        binding.dailyPlanFilter = dailyPlanFilter
        dailyPlansViewModel.filter(dailyPlanFilter).observe(viewLifecycleOwner, { result ->
            plansAdapter.submitList(result)
            result?.let { binding.sizeOfList = it.size }
            setTypesOfQuantities(result)
        })
    }

    private fun setTypesOfQuantities(plans: List<DailyPlan>?) {
        if (plans != null) {
            val overdue = plans.count { it.paymentOverDueDays!! > 0 }
            val current = plans.count { it.paymentOverDueDays!! == 0 }
            val upcoming = plans.count { it.paymentOverDueDays!! < 0 }
            val text = getString(R.string.types_of_quantities, overdue, current, upcoming)
            binding.typesOfQuantities.text = HtmlCompat.fromHtml(text, HtmlCompat.FROM_HTML_MODE_COMPACT)
        }
    }
}