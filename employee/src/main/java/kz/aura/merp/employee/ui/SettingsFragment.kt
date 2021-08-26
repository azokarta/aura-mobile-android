package kz.aura.merp.employee.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.lifecycle.ViewModelProvider
import dagger.hilt.android.AndroidEntryPoint
import kz.aura.merp.employee.R
import kz.aura.merp.employee.databinding.FragmentSettingsBinding
import kz.aura.merp.employee.model.Salary
import kz.aura.merp.employee.ui.dialog.SignOutDialogFragment
import kz.aura.merp.employee.util.*
import kz.aura.merp.employee.viewmodel.AuthViewModel

@AndroidEntryPoint
class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    private lateinit var authViewModel: AuthViewModel
    private var salary: Salary? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        authViewModel = ViewModelProvider(this).get(AuthViewModel::class.java)

        _binding = FragmentSettingsBinding.inflate(layoutInflater, container, false)
        val root: View = binding.root

        setLanguages()

        authViewModel.salary.observe(viewLifecycleOwner, {
            salary = it
            binding.languagesTextField.isEnabled = true
        })

        authViewModel.getSalary()

        binding.signOutBtn.setOnClickListener {
            openSignOutDialog()
        }

        return root
    }

    private fun openSignOutDialog() {
        val dialog = SignOutDialogFragment()
        dialog.show(childFragmentManager, "SettingsFragment")
    }

    private fun setLanguages() {
        val items = listOf(getString(R.string.en), getString(R.string.ru))
        val savedLanguage = when (getLanguage(requireContext())) {
            Language.EN.value -> items[0]
            Language.RU.value -> items[1]
            else -> items[1]
        }
        binding.languagesTextField.editText?.setText(savedLanguage)
        val adapter = ArrayAdapter(requireContext(), R.layout.list_item, items)
        (binding.languagesTextField.editText as? AutoCompleteTextView)?.setAdapter(adapter)

        (binding.languagesTextField.editText as? AutoCompleteTextView)?.setOnItemClickListener { _, _, position, _ ->
            when (position) {
                0 -> saveLanguage(requireContext(), Language.EN.value)
                1 -> saveLanguage(requireContext(), Language.RU.value)
            }
            val staffPosition = definePosition(salary)
            openActivityByPosition(requireContext(), staffPosition!!)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}