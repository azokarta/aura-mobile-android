package kz.aura.merp.employee.ui.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.viewModels
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import kz.aura.merp.employee.R
import kz.aura.merp.employee.data.DataStoreRepository
import kz.aura.merp.employee.databinding.ActivityVerifyPassCodeBinding
import kz.aura.merp.employee.model.Salary
import kz.aura.merp.employee.util.openActivityByPosition
import kz.aura.merp.employee.util.PasscodeStatus
import kz.aura.merp.employee.util.definePosition
import kz.aura.merp.employee.util.vibrate
import kz.aura.merp.employee.viewmodel.AuthViewModel
import kz.aura.merp.employee.viewmodel.PasscodeViewModel
import javax.inject.Inject

@AndroidEntryPoint
class VerifyPasscodeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityVerifyPassCodeBinding

    @Inject lateinit var dataStoreRepository: DataStoreRepository
    private val authViewModel: AuthViewModel by viewModels()
    private val passcodeViewModel: PasscodeViewModel by viewModels()
    private val code = arrayListOf<Int>()
    private var salary: Salary? = null
    private val newCode = arrayListOf<Int>()
    private var savedPasscode: String? = null

//    private lateinit var biometricPrompt: androidx.biometric.BiometricPrompt
//    private lateinit var promptInfo: androidx.biometric.BiometricPrompt.PromptInfo
//    private lateinit var biometricManager: androidx.biometric.BiometricManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVerifyPassCodeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Turn off screenshot
        window.setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE)

        setListenersOfNumbers()

//        biometricManager = androidx.biometric.BiometricManager.from(this)
//        val executor = ContextCompat.getMainExecutor(this)


//        biometricPrompt = androidx.biometric.BiometricPrompt(this, executor,
//            object : androidx.biometric.BiometricPrompt.AuthenticationCallback() {
//                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
//                    super.onAuthenticationError(errorCode, errString)
//                    notifyUser("$errString")
//                }
//
//                override fun onAuthenticationSucceeded(result: androidx.biometric.BiometricPrompt.AuthenticationResult) {
//                    super.onAuthenticationSucceeded(result)
//                    goToHome()
//                }
//
//                override fun onAuthenticationFailed() {
//                    super.onAuthenticationFailed()
//                    notifyUser("Auth Failed")
//                }
//            })
//        promptInfo = androidx.biometric.BiometricPrompt.PromptInfo.Builder()
//            .setTitle("Title")
//            .setSubtitle("Sub Title")
//            .setDescription("Description")
//            .setNegativeButtonText("use email login")
//            .build()
//
//            binding.fingerprint.setOnClickListener() {
//            biometricPrompt.authenticate(promptInfo)
//        }

        authViewModel.salary.observe(this, { salary ->
            this.salary = salary
        })
        passcodeViewModel.passcode.observe(this, { passcode ->
            savedPasscode = passcode
        })

        authViewModel.getSalary()
        passcodeViewModel.getPasscode()
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
                    openActivityByPosition(this, definePosition(arrayListOf(salary!!))!!)
                } else {
                    vibrate(this, 500)
                    isSuccessfullyEntered(false)
                }
//                when (passcodeStatus) {
//                    PasscodeStatus.CREATE -> {
//                        if (newCode.isEmpty()) {
//                            newCode.addAll(code)
//                            clearCode()
//                        } else {
//                            if (newCode == code) {
//                                openActivityByPosition(this, definePosition(arrayListOf(salary!!))!!)
//                            } else {
//                                vibrate(this, 500)
//                                isSuccessfullyEntered(false)
//                            }
//                        }
//                    }
//                    PasscodeStatus.VERIFY -> {
//
//                    }
//                }
            }
        }
    }

    private fun assignCodeText(num: Int) {
        when (code.size) {
            0 -> binding.code1.text = num.toString()
            1 -> binding.code2.text = num.toString()
            2 -> binding.code3.text = num.toString()
            3 -> binding.code4.text = num.toString()
        }
    }

    private fun isSuccessfullyEntered(bool: Boolean) {
        if (bool) {
            binding.codeContainer1.setBackgroundResource(R.drawable.passcode_background)
            binding.codeContainer2.setBackgroundResource(R.drawable.passcode_background)
            binding.codeContainer3.setBackgroundResource(R.drawable.passcode_background)
            binding.codeContainer4.setBackgroundResource(R.drawable.passcode_background)
        } else {
            binding.codeContainer1.setBackgroundResource(R.drawable.passcode_error_background)
            binding.codeContainer2.setBackgroundResource(R.drawable.passcode_error_background)
            binding.codeContainer3.setBackgroundResource(R.drawable.passcode_error_background)
            binding.codeContainer4.setBackgroundResource(R.drawable.passcode_error_background)
        }
    }

    private fun clearCode() {
        code.clear()
        binding.code1.text = ""
        binding.code2.text = ""
        binding.code3.text = ""
        binding.code4.text = ""
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