package kz.aura.merp.employee.ui.common

import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import dagger.hilt.android.AndroidEntryPoint
import kz.aura.merp.employee.R
import kz.aura.merp.employee.base.BaseActivity
import kz.aura.merp.employee.databinding.ActivityVerifyPasscodeBinding
import kz.aura.merp.employee.util.openActivityByPosition
import kz.aura.merp.employee.util.definePosition
import kz.aura.merp.employee.util.vibrate
import kz.aura.merp.employee.viewmodel.AuthViewModel
import kz.aura.merp.employee.viewmodel.PasscodeViewModel
import java.util.concurrent.Executor

@AndroidEntryPoint
class VerifyPasscodeActivity : BaseActivity() {

    private lateinit var binding: ActivityVerifyPasscodeBinding

    private val authViewModel: AuthViewModel by viewModels()
    private val passcodeViewModel: PasscodeViewModel by viewModels()
    private val code = arrayListOf<Int>()
    private var savedPasscode: String? = null

    private lateinit var executor: Executor
    private lateinit var biometricPrompt: BiometricPrompt
    private lateinit var promptInfo: BiometricPrompt.PromptInfo

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVerifyPasscodeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setListenersOfNumbers()

        executor = ContextCompat.getMainExecutor(this)
        biometricPrompt = BiometricPrompt(this, executor,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                    when (errorCode) {
                        BiometricPrompt.ERROR_CANCELED -> {}
                        BiometricPrompt.ERROR_USER_CANCELED -> {}
                        BiometricPrompt.ERROR_NEGATIVE_BUTTON -> {}
                        else -> Toast.makeText(applicationContext, errString, Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onAuthenticationSucceeded(
                    result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    definePositionAndOpenActivity()
                }
            })

        promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle(getString(R.string.wemob_authentication))
            .setSubtitle(getString(R.string.wemob_using_biometric_authentication))
            .setNegativeButtonText(getString(R.string.cancel))
            .build()

        // Prompt appears when user clicks "Log in".
        // Consider integrating with the keystore to unlock cryptographic operations,
        // if needed by your app.
        binding.fingerprint.setOnClickListener {
            biometricPrompt.authenticate(promptInfo)
        }

        savedPasscode = passcodeViewModel.preferences.passcode
    }

    private fun setListenersOfNumbers() {
        binding.number1.setOnClickListener { addNumber(1) }
        binding.number2.setOnClickListener { addNumber(2) }
        binding.number3.setOnClickListener { addNumber(3) }
        binding.number4.setOnClickListener { addNumber(4) }
        binding.number5.setOnClickListener { addNumber(5) }
        binding.number6.setOnClickListener { addNumber(6) }
        binding.number7.setOnClickListener { addNumber(7) }
        binding.number8.setOnClickListener { addNumber(8) }
        binding.number9.setOnClickListener { addNumber(9) }
        binding.number0.setOnClickListener { addNumber(0) }
        binding.deleteNumber.setOnClickListener {
            removeLastNumber()
        }
    }

    private fun addNumber(num: Int) {
        if (code.size < 4) {
            assignCodeText(num)
            code.add(num)

            if (code.size == 4) {
                if (code.joinToString() == savedPasscode) {
                    definePositionAndOpenActivity()
                } else {
                    vibrate(this, 500)
                    showBackgroundError()
                }
            }
        }
    }

    private fun definePositionAndOpenActivity() {
        val salary = authViewModel.preferences.salary
        val staffPosition = definePosition(salary)
        openActivityByPosition(this, staffPosition!!)
    }


    private fun assignCodeText(num: Int) {
        when (code.size) {
            0 -> binding.code1.text = num.toString()
            1 -> binding.code2.text = num.toString()
            2 -> binding.code3.text = num.toString()
            3 -> binding.code4.text = num.toString()
        }
    }

    private fun showBackgroundError() {
        binding.codeContainer1.setBackgroundResource(R.drawable.passcode_error_background)
        binding.codeContainer2.setBackgroundResource(R.drawable.passcode_error_background)
        binding.codeContainer3.setBackgroundResource(R.drawable.passcode_error_background)
        binding.codeContainer4.setBackgroundResource(R.drawable.passcode_error_background)
    }

    private fun removeLastNumber() {
        if (code.size != 0) {
            when (code.size) {
                1 -> binding.code1.text = ""
                2 -> binding.code2.text = ""
                3 -> binding.code3.text = ""
                4 -> binding.code4.text = ""
            }
            code.removeLast()
        }
    }

}