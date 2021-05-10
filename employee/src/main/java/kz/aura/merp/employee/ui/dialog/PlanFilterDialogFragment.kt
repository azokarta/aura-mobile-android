package kz.aura.merp.employee.ui.dialog

import android.content.Context
import android.graphics.Rect
import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import android.widget.EditText
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.chip.Chip
import dagger.hilt.android.AndroidEntryPoint
import kz.aura.merp.employee.R
import kz.aura.merp.employee.databinding.PlanFilterBottomSheetBinding
import kz.aura.merp.employee.model.BusinessProcessStatus
import kz.aura.merp.employee.util.NetworkResult
import kz.aura.merp.employee.viewmodel.FinanceViewModel
import kz.aura.merp.employee.viewmodel.PlanFilterViewModel

@AndroidEntryPoint
class PlanFilterDialogFragment : BottomSheetDialogFragment() {

    private var _binding: PlanFilterBottomSheetBinding? = null
    private val binding get() = _binding!!
    private val mFinanceViewModel: FinanceViewModel by activityViewModels()
    private val mFilterViewModel: PlanFilterViewModel by activityViewModels()
    private lateinit var filterSortParams: ArrayList<String>
    private lateinit var searchParams: List<String>
    private var selectedSearchBy: Int = 0
    private var selectedSortFilter: Int = 0
    private var selectedStatusFilter: Int = 0
    private var query: String = ""
    private var problematic: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = PlanFilterBottomSheetBinding.inflate(inflater, container, false)

        filterSortParams = arrayListOf(
            getString(R.string.date),
            getString(R.string.fullName)
        )

        // Initialize search params
        searchParams = listOf(getString(R.string.cn), getString(R.string.fullName))
        val adapter = ArrayAdapter(requireContext(), R.layout.list_item, searchParams)
        binding.searchByEditText.setText(searchParams[0])
        binding.searchByEditText.setAdapter(adapter)

        for ((idx, value) in filterSortParams.withIndex()) {
            binding.sortChipGroup.addView(createChip(value, idx))
        }

        // Init listeners
        binding.apply.setOnClickListener {
            val query = binding.search.editText?.text.toString()
            val selectedSortFilter = binding.sortChipGroup.checkedChipId
            val selectedStatusFilter = binding.statusesChipGroup.checkedChipId
            mFilterViewModel.apply(
                query,
                selectedSortFilter,
                selectedStatusFilter,
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

        setupObservers()

        return binding.root
    }

    private fun setupFilterParams() {
        binding.searchByEditText.setText(searchParams[selectedSearchBy])
        binding.search.editText?.setText(query)
        binding.problematic.isChecked = problematic
        binding.statusesChipGroup.check(selectedStatusFilter)
        binding.sortChipGroup.check(selectedSortFilter)
    }

    private fun createChip(title: String, id: Int): Chip {
        val chip = Chip(requireContext())
        chip.text = title
        chip.id = id
        chip.setChipBackgroundColorResource(R.color.gray)
        chip.checkedIcon =
            ContextCompat.getDrawable(requireContext(), R.drawable.ic_baseline_check_24)
        chip.isCheckable = true
        chip.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
        return chip
    }

    private fun setupObservers() {
        mFinanceViewModel.businessProcessStatusesResponse.observe(viewLifecycleOwner, { res ->
            when (res) {
                is NetworkResult.Success -> {
                    val businessProcessStatuses = res.data!!
                    businessProcessStatuses.add(
                        BusinessProcessStatus(
                            0,
                            getString(R.string._new),
                            "",
                            "",
                            "",
                            ""
                        )
                    )
                    for (process in businessProcessStatuses) {
                        binding.statusesChipGroup.addView(
                            createChip(
                                process.name,
                                process.id.toInt()
                            )
                        )
                    }
                }
                is NetworkResult.Loading -> {
                }
                is NetworkResult.Error -> {
                }
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}