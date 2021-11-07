package kz.aura.merp.employee.ui.finance.fragment

import android.os.Bundle
import android.view.*
import androidx.core.text.HtmlCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kz.aura.merp.employee.R
import kz.aura.merp.employee.adapter.PlansAdapter
import kz.aura.merp.employee.base.NetworkResult
import kz.aura.merp.employee.databinding.FragmentMonthlyPlanBinding
import kz.aura.merp.employee.model.Plan
import kz.aura.merp.employee.model.MonthlyPlanFilter
import kz.aura.merp.employee.ui.finance.dialog.MonthlyPlanFilterDialogFragment
import kz.aura.merp.employee.ui.common.TimePickerFragment
import kz.aura.merp.employee.util.*
import kz.aura.merp.employee.viewmodel.SharedViewModel
import kz.aura.merp.employee.viewmodel.finance.MonthlyPlanViewModel
import java.util.*

@AndroidEntryPoint
class MonthlyPlanFragment : Fragment(R.layout.fragment_monthly_plan), PlansAdapter.OnClickListener,SwipeRefreshLayout.OnRefreshListener, MonthlyPlanFilterDialogFragment.Listener {

    private var _binding: FragmentMonthlyPlanBinding? = null
    private val binding get() = _binding!!

    private val monthlyPlanViewModel: MonthlyPlanViewModel by viewModels()
    private val sharedViewModel: SharedViewModel by viewModels()
    private val plansAdapter: PlansAdapter by lazy { PlansAdapter(this) }
    private lateinit var progressDialog: ProgressDialog

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentMonthlyPlanBinding.bind(view)

        // Set Menu
        setHasOptionsMenu(true)

        with (binding) {
            lifecycleOwner = this@MonthlyPlanFragment
            sharedViewModel = this@MonthlyPlanFragment.sharedViewModel
            planFilter = MonthlyPlanFilter()

            swipeRefresh.setOnRefreshListener(this@MonthlyPlanFragment)

            explanationAboutColors.setOnClickListener(::explainAboutColors)

            error.restart.setOnClickListener { callRequests() }
        }

        progressDialog = ProgressDialog(requireContext())

        setupRecyclerView()

        observeLiveData()

        callRequests()
    }

    private fun callRequests() {
        monthlyPlanViewModel.fetchPlans()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.filter_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.filter -> {
                val dialog = MonthlyPlanFilterDialogFragment(this, binding.planFilter ?: MonthlyPlanFilter())
                dialog.show(childFragmentManager, tag)
            }
            R.id.clearFilter -> filterPlans()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun sendToDailyPlan(contractId: Long) {
        val timePicker = TimePickerFragment() { hour, minute ->
            monthlyPlanViewModel.createDailyPlan(contractId, "$hour:$minute")
        }
        timePicker.show(childFragmentManager)
    }

    private fun setupRecyclerView() {
        with (binding) {
            recyclerView.adapter = plansAdapter
            recyclerView.isNestedScrollingEnabled = false
        }
    }

    private fun observeLiveData() {
        monthlyPlanViewModel.plansResponse.observe(viewLifecycleOwner, { res ->
            when (res) {
                is NetworkResult.Success -> {
                    sharedViewModel.setResponse(res)

                    filterPlans(binding.planFilter ?: MonthlyPlanFilter())
                }
                is NetworkResult.Loading -> sharedViewModel.setResponse(res)
                is NetworkResult.Error -> sharedViewModel.setResponse(res)
            }
        })
        monthlyPlanViewModel.createDailyPlanResponse.observe(viewLifecycleOwner, { res ->
            when (res) {
                is NetworkResult.Success -> {
                    monthlyPlanViewModel.createDailyPlanResponse.postValue(null)
                    progressDialog.hideLoading()
                    showSnackbar(binding.recyclerView)
                }
                is NetworkResult.Loading -> progressDialog.showLoading()
                is NetworkResult.Error -> {
                    progressDialog.hideLoading()
                    showException(res.message, requireContext())
                }
            }
        })
    }

    private fun explainAboutColors(view: View) {
        MaterialAlertDialogBuilder(requireActivity())
            .setView(R.layout.explanation_about_colors)
            .setTitle(resources.getString(R.string.explanation_about_colors))
            .show()
    }

    private fun showSnackbar(view: View) = Snackbar.make(
        view,
        R.string.successfully_saved,
        Snackbar.LENGTH_SHORT
    ).show()

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onRefresh() {
        sharedViewModel.setLoadingType(LoadingType.SWIPE_REFRESH)
        callRequests()
    }

    override fun apply(monthlyPlanFilter: MonthlyPlanFilter) {
        filterPlans(monthlyPlanFilter)
    }

    private fun filterPlans(monthlyPlanFilter: MonthlyPlanFilter = MonthlyPlanFilter()) {
        binding.planFilter = monthlyPlanFilter
        monthlyPlanViewModel.filter(monthlyPlanFilter).observe(viewLifecycleOwner, { result ->
            plansAdapter.submitList(result)
            result?.let { binding.sizeOfList = it.size }
            setTypesOfQuantities(result)
        })
    }

    private fun setTypesOfQuantities(plans: List<Plan>?) {
        if (plans != null) {
            val overdue = plans.count { it.paymentOverDueDays!! > 0 }
            val current = plans.count { it.paymentOverDueDays!! == 0 }
            val upcoming = plans.count { it.paymentOverDueDays!! < 0 }
            val text = getString(R.string.types_of_quantities, overdue, current, upcoming)
            binding.typesOfQuantities.text = HtmlCompat.fromHtml(text, HtmlCompat.FROM_HTML_MODE_COMPACT)
        }
    }

}