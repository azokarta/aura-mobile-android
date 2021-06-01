package kz.aura.merp.employee.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.navArgs
import kz.aura.merp.employee.databinding.FragmentPasscodeBinding
import kz.aura.merp.employee.util.PasscodeStatus

class PasscodeFragment : Fragment() {

//    private val args: PasscodeFragmentArgs by navArgs()

    private var _binding: FragmentPasscodeBinding? = null
    private val binding get() = _binding!!

    private lateinit var biometricPrompt: androidx.biometric.BiometricPrompt
    private lateinit var promptInfo: androidx.biometric.BiometricPrompt.PromptInfo
    private lateinit var biometricManager: androidx.biometric.BiometricManager

//    private lateinit var status: PasscodeStatus

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPasscodeBinding.inflate(inflater, container, false)
        val root: View = binding.root

//        val status = args.status

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}