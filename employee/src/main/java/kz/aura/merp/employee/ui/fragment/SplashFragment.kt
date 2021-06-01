package kz.aura.merp.employee.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import kz.aura.merp.employee.R
import kz.aura.merp.employee.databinding.FragmentSplashBinding
import kz.aura.merp.employee.util.getToken
import kz.aura.merp.employee.viewmodel.AuthViewModel

@AndroidEntryPoint
class SplashFragment : Fragment() {

    private var _binding: FragmentSplashBinding? = null
    private val binding get() = _binding!!

    private lateinit var authViewModel: AuthViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        authViewModel = ViewModelProvider(this).get(AuthViewModel::class.java)

        _binding = FragmentSplashBinding.inflate(inflater, container, false)
        val root: View = binding.root

        authViewModel.salary.observe(viewLifecycleOwner, { salary ->
            binding.logo.alpha = 0f
            binding.logo.animate().setDuration(1500).alpha(1f).withEndAction {
                if (!getToken(requireContext()).isNullOrBlank() && salary != null) {
                    findNavController().navigate(R.id.action_splashFragment_to_passcodeFragment)
                } else {
                    findNavController().navigate(R.id.action_splashFragment_to_authFragment)
                }
            }
        })

        authViewModel.getSalary()

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}