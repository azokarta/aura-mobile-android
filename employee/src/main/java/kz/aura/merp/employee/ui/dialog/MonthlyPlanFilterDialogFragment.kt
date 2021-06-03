package kz.aura.merp.employee.ui.dialog

import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.chip.Chip
import dagger.hilt.android.AndroidEntryPoint
import kz.aura.merp.employee.R
import kz.aura.merp.employee.databinding.MonthlyPlanFilterBottomSheetBinding
import kz.aura.merp.employee.viewmodel.FinanceViewModel
import kz.aura.merp.employee.viewmodel.PlanFilterViewModel
import kz.aura.merp.employee.viewmodel.SharedViewModel

@AndroidEntryPoint
class MonthlyPlanFilterDialogFragment : BottomSheetDialogFragment() {

    private var _binding: MonthlyPlanFilterBottomSheetBinding? = null
    private val binding get() = _binding!!
    private lateinit var financeViewModel: FinanceViewModel
    private val filterViewModel: PlanFilterViewModel by activityViewModels()
    private lateinit var sharedViewModel: SharedViewModel
    private lateinit var filterSortParams: ArrayList<String>
    private lateinit var searchParams: List<String>
    private var selectedSearchBy: Int = 0
    private var selectedSortFilter: Int = 0
    private var query: String = ""
    private var problematic: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        financeViewModel = ViewModelProvider(this).get(FinanceViewModel::class.java)
        sharedViewModel = ViewModelProvider(this).get(SharedViewModel::class.java)

        _binding = MonthlyPlanFilterBottomSheetBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        val root: View = binding.root

        selectedSearchBy = filterViewModel.filterParams.value?.selectedSearchBy ?: 0
        selectedSortFilter = filterViewModel.filterParams.value?.selectedSortFilter ?: 0
        query = filterViewModel.filterParams.value?.query ?: ""
        problematic = filterViewModel.filterParams.value?.problematic ?: false

        filterSortParams = arrayListOf(
            getString(R.string.payment_date),
            getString(R.string.contract_date),
            getString(R.string.fullname)
        )

        // Initialize search params
        searchParams = listOf(getString(R.string.cn), getString(R.string.fullname))
        val adapter = ArrayAdapter(requireContext(), R.layout.list_item, searchParams)
        binding.searchByEditText.setText(searchParams[selectedSearchBy])
        binding.searchByEditText.setAdapter(adapter)

        for ((idx, value) in filterSortParams.withIndex()) {
            binding.sortChipGroup.addView(createChip(value, idx))
        }

        // Init listeners
        binding.apply.setOnClickListener {
            val query = binding.search.editText?.text.toString()
            val selectedSortFilter = binding.sortChipGroup.checkedChipId
            filterViewModel.apply(
                query,
                selectedSortFilter,
                0,
                selectedSearchBy,
                binding.problematic.isChecked
            )
            this.dismiss()
        }
        binding.search.setEndIconOnClickListener {
            binding.search.editText?.setText("")
        }
        binding.searchByEditText.setOnItemClickListener { _, _, i, _ ->
            selectedSearchBy = i
            when (i) {
                0 -> binding.search.editText?.inputType = InputType.TYPE_CLASS_NUMBER
                1 -> binding.search.editText?.inputType = InputType.TYPE_CLASS_TEXT
            }
        }

        setupFilterParams()

        return root
    }

    private fun setupFilterParams() {
        binding.search.editText?.setText(query)
        binding.problematic.isChecked = problematic
        binding.sortChipGroup.check(selectedSortFilter)
    }

    private fun createChip(title: String, id: Int): Chip {
        val chip = Chip(requireContext())
        chip.text = title
        chip.id = id
        chip.setChipBackgroundColorResource(R.color.gray)
        chip.checkedIcon = ContextCompat.getDrawable(requireContext(), R.drawable.ic_baseline_check_24)
        chip.isCheckable = true
        chip.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
        return chip
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}