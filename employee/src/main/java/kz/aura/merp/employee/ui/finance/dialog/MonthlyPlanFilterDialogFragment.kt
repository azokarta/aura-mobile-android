package kz.aura.merp.employee.ui.finance.dialog

import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import kz.aura.merp.employee.R
import kz.aura.merp.employee.databinding.MonthlyPlanFilterBottomSheetBinding
import kz.aura.merp.employee.model.MonthlyPlanFilter
import kz.aura.merp.employee.util.SearchType
import kz.aura.merp.employee.util.SortType
import kz.aura.merp.employee.util.hideKeyboardFrom

@AndroidEntryPoint
class MonthlyPlanFilterDialogFragment(private val listener: Listener? = null, private val monthlyPlanFilter: MonthlyPlanFilter) : BottomSheetDialogFragment() {

    private var _binding: MonthlyPlanFilterBottomSheetBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = MonthlyPlanFilterBottomSheetBinding.inflate(inflater, container, false)
        val root: View = binding.root

        setupSearchBy()

        setAppliedFilter()

        binding.apply.setOnClickListener {
            val query = binding.search.editText?.text.toString()
            val sortType = getSortType()
            val problematic = binding.problematic.isChecked
            val searchType = getSearchType()
            listener?.apply(MonthlyPlanFilter(query, sortType, problematic, searchType))
            this.dismiss()
        }
        binding.searchByEditText.setOnItemClickListener { _, view, position, _ ->
            setInputTypeOfSearch(SearchType.values()[position])
            binding.search.editText?.setText("")
            hideKeyboardFrom(requireContext(), view)
        }

        return root
    }

    private fun getSearchType(): SearchType? {
        return when (binding.searchByEditText.text.toString()) {
            getString(SearchType.CN.value) -> SearchType.CN
            getString(SearchType.FULL_NAME.value) -> SearchType.FULL_NAME
            getString(SearchType.PHONE_NUMBER.value) -> SearchType.PHONE_NUMBER
            getString(SearchType.ADDRESS.value) -> SearchType.ADDRESS
            else -> null
        }
    }

    private fun getSortType(): SortType {
        return when (binding.sortChipGroup.checkedChipId) {
            binding.chipPaymentDate.id -> SortType.PAYMENT_DATE
            binding.chipContractDate.id -> SortType.CONTRACT_DATE
            binding.chipFullname.id -> SortType.FULL_NAME
            else -> SortType.PAYMENT_DATE
        }
    }

    private fun setupSearchBy() {
        val items = SearchType.values().map { getString(it.value) }
        val adapter = ArrayAdapter(requireContext(), R.layout.list_item, items)
        binding.searchByEditText.setAdapter(adapter)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setAppliedFilter() {
        val (query, sortType, problematic, searchType) = monthlyPlanFilter
        binding.search.editText?.setText(query)
        binding.sortChipGroup.check(getChipId(sortType))
        binding.problematic.isChecked = problematic
        searchType?.let {
            binding.searchByEditText.setText(getString(it.value), false)
            setInputTypeOfSearch(it)
        }
    }

    private fun getChipId(sortType: SortType): Int {
        return when (sortType) {
            SortType.FULL_NAME -> binding.chipFullname.id
            SortType.PAYMENT_DATE -> binding.chipPaymentDate.id
            SortType.CONTRACT_DATE -> binding.chipContractDate.id
        }
    }

    private fun setInputTypeOfSearch(searchType: SearchType) {
        when (searchType) {
            SearchType.CN -> binding.search.editText?.inputType = InputType.TYPE_CLASS_NUMBER
            else -> binding.search.editText?.inputType = InputType.TYPE_CLASS_TEXT
        }
    }

    interface Listener {
        fun apply(
            monthlyPlanFilter: MonthlyPlanFilter
        )
    }
}