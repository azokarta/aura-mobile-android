package kz.aura.merp.employee.ui.activity

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.preference.PreferenceManager
import dagger.hilt.android.AndroidEntryPoint
import kz.aura.merp.employee.R
import kz.aura.merp.employee.databinding.ActivityPassCodeBinding
import kz.aura.merp.employee.model.Salary
import kz.aura.merp.employee.util.openActivityByPosition
import kz.aura.merp.employee.util.PassCodeStatus
import kz.aura.merp.employee.util.definePosition
import kz.aura.merp.employee.viewmodel.AuthViewModel

@AndroidEntryPoint
class PassCodeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPassCodeBinding

    private val mAuthViewModel: AuthViewModel by viewModels()
    private val code = arrayListOf<Int>()
    private lateinit var passCodeStatus: PassCodeStatus
    private val firstCreatedCode = arrayListOf<Int>()
    private var salary: Salary? = null

    private lateinit var biometricPrompt: androidx.biometric.BiometricPrompt
    private lateinit var promptInfo: androidx.biometric.BiometricPrompt.PromptInfo
    private lateinit var biometricManager: androidx.biometric.BiometricManager

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPassCodeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Turn off screenshot
        window.setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE)

        passCodeStatus = intent.getSerializableExtra("passCodeStatus") as PassCodeStatus

        setListenersOfNumbers()

        if (passCodeStatus == PassCodeStatus.VERIFY) {
//            changeText(getString(R.string.passCodeSignInTitle),getString(R.string.passCodeSignSubTitle))
        }


        biometricManager = androidx.biometric.BiometricManager.from(this)
        val executor = ContextCompat.getMainExecutor(this)


        biometricPrompt = androidx.biometric.BiometricPrompt(this, executor,
            object : androidx.biometric.BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                    notifyUser("$errString")
                }

                override fun onAuthenticationSucceeded(result: androidx.biometric.BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    goToHome()
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    notifyUser("Auth Failed")
                }
            })
        promptInfo = androidx.biometric.BiometricPrompt.PromptInfo.Builder()
            .setTitle("Title")
            .setSubtitle("Sub Title")
            .setDescription("Description")
            .setNegativeButtonText("use email login")
            .build()

            binding.fingerprint.setOnClickListener() {
            biometricPrompt.authenticate(promptInfo)
        }

        mAuthViewModel.salary.observe(this, { salary ->
            this.salary = salary
        })

        mAuthViewModel.getSalary()
    }

    private fun changeText(title: String = "", subTitle: String = "") {
        binding.passCodeTitle.text = title
        binding.passCodeSubTitle.text = subTitle
    }

    private fun setListenersOfNumbers() {
        binding.number1.setOnClickListener {
            fillLine(1)
        }

        binding.number2.setOnClickListener {
            fillLine(2)
        }

        binding.number3.setOnClickListener {
            fillLine(3)
        }

        binding.number4.setOnClickListener {
            fillLine(4)
        }

        binding.number5.setOnClickListener {
            fillLine(5)
        }

        binding.number6.setOnClickListener {
            fillLine(6)
        }

        binding.number7.setOnClickListener {
            fillLine(7)
        }

        binding.number8.setOnClickListener {
            fillLine(8)
        }

        binding.number9.setOnClickListener {
            fillLine(9)
        }

        binding.number0.setOnClickListener {
            fillLine(0)
        }

        binding.deleteNumber.setOnClickListener {
            removeLastNumber()
        }
    }

    private fun fillLine(num: Int) {
        when (code.size) {
            0 -> binding.typed1.setBackgroundResource(R.color.colorPrimary)
            1 -> binding.typed2.setBackgroundResource(R.color.colorPrimary)
            2 -> binding.typed3.setBackgroundResource(R.color.colorPrimary)
            3 -> binding.typed4.setBackgroundResource(R.color.colorPrimary)
        }
        code.add(num)

        if (code.size == 4) {
            when (passCodeStatus) {
                PassCodeStatus.CREATE -> {
                    if (firstCreatedCode.isEmpty()) {
                        firstCreatedCode.addAll(code)
                        code.clear()
                        paintLinesToBlack()
//                        changeText( getString(R.string.passCodeTitle),getString(R.string.passCodeReEnter))
                    } else if (firstCreatedCode == code) {
                        savePassCode()
                        val position = definePosition(arrayListOf(salary!!))!!
                        openActivityByPosition(this, position)
                    } else {
                        paintLinesToBlack()
                        code.clear()
                    }
                }

                PassCodeStatus.VERIFY -> {
                   // changeText(getString(R.string.passCodeSignInTitle),getString(R.string.passCodeSubTitle))
                    val typedCode = code.joinToString(separator = "")
                    val userCode = receivePassCode()
                    if (userCode == typedCode) {
                        val position = definePosition(arrayListOf(salary!!))!!
                        openActivityByPosition(this, position)
                    } else {
//                        changeText(getString(R.string.passCodeReEnter),getString(R.string.passCodeWrong))
                        paintLinesToBlack()
                        code.clear()
                    }
                }
            }
        }
    }

    private fun removeLastNumber() {
        if (code.size != 0) {
            when (code.size) {
                1 -> binding.typed1.setBackgroundResource(R.color.black)
                2 -> binding.typed2.setBackgroundResource(R.color.black)
                3 -> binding.typed3.setBackgroundResource(R.color.black)
                4 -> binding.typed4.setBackgroundResource(R.color.black)
            }
            code.removeLast()
        }
    }

    private fun savePassCode() {
        PreferenceManager.getDefaultSharedPreferences(this)
            .edit()
            .putString("passCode", code.joinToString(separator = ""))
            .apply()
    }

    private fun receivePassCode() =
        PreferenceManager.getDefaultSharedPreferences(this).getString("passCode", "")

    private fun paintLinesToBlack() {
        binding.typed1.setBackgroundResource(R.color.black)
        binding.typed2.setBackgroundResource(R.color.black)
        binding.typed3.setBackgroundResource(R.color.black)
        binding.typed4.setBackgroundResource(R.color.black)
    }


    private fun notifyUser(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun goToHome() {
        val position = definePosition(arrayListOf(salary!!))!!
        openActivityByPosition(this, position)
    }

}