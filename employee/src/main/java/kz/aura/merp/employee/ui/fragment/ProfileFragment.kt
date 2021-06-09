package kz.aura.merp.employee.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import dagger.hilt.android.AndroidEntryPoint
import kz.aura.merp.employee.databinding.FragmentProfileBinding
import kz.aura.merp.employee.ui.dialog.UserAvatarActionsDialogFragment
import kz.aura.merp.employee.viewmodel.FinanceViewModel

@AndroidEntryPoint
class ProfileFragment : Fragment(), UserAvatarActionsDialogFragment.UserAvatarActionsDialogListener {

    private var _binding: FragmentProfileBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private lateinit var financeViewModel: FinanceViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        financeViewModel = ViewModelProvider(this).get(FinanceViewModel::class.java)

        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        val root: View = binding.root

        financeViewModel.salary.observe(viewLifecycleOwner, { salary ->
            binding.salary = salary
        })

        financeViewModel.getSalary()

        binding.avatar.setOnClickListener {
            openAvatarActions()
        }

        return root
    }

    private fun openAvatarActions() {
        val dialog = UserAvatarActionsDialogFragment()
        dialog.show(childFragmentManager, "ProfileFragment")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun selectOpenPhoto() {
        println("OPEN")
    }

    override fun selectRemovePhoto() {
        println("REMOVE")
    }

    override fun selectEditPhoto() {
        println("EDIT")
    }
}