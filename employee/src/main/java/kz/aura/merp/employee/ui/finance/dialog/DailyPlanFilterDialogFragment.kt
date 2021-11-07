package kz.aura.merp.employee.ui.finance.dialog

import dagger.hilt.android.AndroidEntryPoint
import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.core.content.ContextCompat
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.chip.Chip
import kz.aura.merp.employee.R
import kz.aura.merp.employee.databinding.DailyPlanFilterBottomSheetBinding
import kz.aura.merp.employee.model.BusinessProcessStatus
import kz.aura.merp.employee.model.DailyPlanFilter
import kz.aura.merp.employee.model.MonthlyPlanFilter
import kz.aura.merp.employee.util.SearchType
import kz.aura.merp.employee.util.SortType
import kz.aura.merp.employee.util.hideKeyboardFrom

@AndroidEntryPoint
class DailyPlanFilterDialogFragment(
    private val listener: Listener? = null,
    private val dailyPlanFilter: DailyPlanFilter,
    private val statuses: List<BusinessProcessStatus>
) : BottomSheetDialogFragment() {

    private var _binding: DailyPlanFilterBottomSheetBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DailyPlanFilterBottomSheetBinding.inflate(inflater, container, false)
        val root: View = binding.root

        setupSearchBy()

        setStatuses()

        setAppliedFilter()

        binding.apply.setOnClickListener {
            val query = binding.search.editText?.text.toString()
            val sortType = getSortType()
            val problematic = binding.problematic.isChecked
            val searchType = getSearchType()
            val status = binding.statusesChipGroup.checkedChipId.toLong()
            listener?.apply(DailyPlanFilter(query, sortType, problematic, searchType, status))
            this.dismiss()
        }
        binding.searchByEditText.setOnItemClickListener { _, view, position, _ ->
            setInputTypeOfSearch(SearchType.values()[position])
            binding.search.editText?.setText("")
            hideKeyboardFrom(requireContext(), view)
        }

        return root
    }

    private fun setStatuses() {
        binding.statusesChipGroup.addView(createChip(getString(R.string.all), 4040))
        for (status in statuses) {
            binding.statusesChipGroup.addView(createChip(status.name, status.id))
        }
    }

    private fun createChip(title: String, id: Long): Chip {
        val chip = Chip(requireContext(), null, R.style.Widget_MaterialComponents_Chip_Choice).apply {
            text = title
            this.id = id.toInt()
            isCheckable = true
            isClickable = true
        }
        return chip
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

    private fun setAppliedFilter() {
        val (query, sortType, problematic, searchType, statusId) = dailyPlanFilter
        binding.search.editText?.setText(query)
        binding.sortChipGroup.check(getChipIdOfSortType(sortType))
        binding.statusesChipGroup.check(statusId.toInt())
        binding.problematic.isChecked = problematic
        searchType?.let {
            binding.searchByEditText.setText(getString(it.value), false)
            setInputTypeOfSearch(it)
        }
    }

    private fun getChipIdOfSortType(sortType: SortType): Int {
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
            dailyPlanFilter: DailyPlanFilter
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}