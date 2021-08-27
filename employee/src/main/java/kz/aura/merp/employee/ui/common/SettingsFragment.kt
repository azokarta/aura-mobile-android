package kz.aura.merp.employee.ui.common

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import kz.aura.merp.employee.R
import kz.aura.merp.employee.databinding.FragmentSettingsBinding
import kz.aura.merp.employee.util.*
import kz.aura.merp.employee.viewmodel.AuthViewModel

@AndroidEntryPoint
class SettingsFragment : Fragment(R.layout.fragment_settings) {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    private val authViewModel: AuthViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentSettingsBinding.bind(view)

        setLanguages()

        binding.signOutBtn.setOnClickListener {
            openSignOutDialog()
        }
    }

    private fun openSignOutDialog() {
        val dialog = SignOutDialogFragment()
        dialog.show(childFragmentManager, "SettingsFragment")
    }

    private fun setLanguages() {
        val items = listOf(getString(R.string.en), getString(R.string.ru))
        val salary = authViewModel.preferences.salary
        val saveLanguage = { lan: String -> authViewModel.preferences.language = lan }

        val savedLanguage = when (authViewModel.preferences.language) {
            Language.EN.value -> items[0]
            Language.RU.value -> items[1]
            else -> items[1]
        }
        binding.languagesTextField.editText?.setText(savedLanguage)
        val adapter = ArrayAdapter(requireContext(), R.layout.list_item, items)
        (binding.languagesTextField.editText as? AutoCompleteTextView)?.setAdapter(adapter)

        (binding.languagesTextField.editText as? AutoCompleteTextView)?.setOnItemClickListener { _, _, position, _ ->
            when (position) {
                0 -> saveLanguage(Language.EN.value)
                1 -> saveLanguage(Language.RU.value)
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