package kz.aura.merp.employee.ui.finance.fragment

import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import kz.aura.merp.employee.R
import kz.aura.merp.employee.databinding.FragmentProfileBinding
import kz.aura.merp.employee.ui.common.UserAvatarActionsDialogFragment
import kz.aura.merp.employee.viewmodel.finance.ProfileViewModel

@AndroidEntryPoint
class ProfileFragment : Fragment(R.layout.fragment_profile), UserAvatarActionsDialogFragment.UserAvatarActionsDialogListener {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private val profileViewModel: ProfileViewModel by viewModels()
    val getContent = registerForActivityResult(ActivityResultContracts.GetContent()) { result ->
        binding.avatar.setImageURI(result)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentProfileBinding.bind(view)

        with (binding) {
            salary = profileViewModel.preferences.salary
//            executePendingBindings()

            avatar.setOnClickListener {
                openAvatarActions()
            }
        }
    }

    private fun openAvatarActions() {
        val dialog = UserAvatarActionsDialogFragment()
        dialog.show(childFragmentManager, "ProfileFragment")
        dialog.setListener(this)
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
        getContent.launch("image/*")
    }
}