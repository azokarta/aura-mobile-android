package kz.aura.merp.employee.ui.activity

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.widget.Toolbar
import androidx.core.view.isVisible
import dagger.hilt.android.AndroidEntryPoint
import kz.aura.merp.employee.R
import kz.aura.merp.employee.databinding.ActivityCreatePasscodeBinding
import kz.aura.merp.employee.model.Salary
import kz.aura.merp.employee.util.*
import kz.aura.merp.employee.viewmodel.AuthViewModel
import kz.aura.merp.employee.viewmodel.PasscodeViewModel

@AndroidEntryPoint
class CreatePasscodeActivity : BaseActivity() {

    private lateinit var binding: ActivityCreatePasscodeBinding

    private val authViewModel: AuthViewModel by viewModels()
    private val passcodeViewModel: PasscodeViewModel by viewModels()
    private val code = arrayListOf<Int>()
    private var salary: Salary? = null
    private val newCode = arrayListOf<Int>()
    private var savedPasscode: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreatePasscodeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.title = getString(R.string.create_passcode)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        setListenersOfNumbers()

        authViewModel.salary.observe(this, { salary ->
            this.salary = salary
        })
        passcodeViewModel.passcode.observe(this, { passcode ->
            savedPasscode = passcode
            val staffPosition = definePosition(salary)
            openActivityByPosition(this, staffPosition!!)
        })

        authViewModel.getSalary()
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

    override fun onDestroy() {
        if (savedPasscode.isNullOrBlank()) {
            removeToken(this)
            authViewModel.clearSettings()
        }
        super.onDestroy()
    }

    private fun addNumber(num: Int) {
        if (code.size < 4) {
            assignCodeText(num)
            code.add(num)

            if (code.size == 4) {
                if (newCode.isEmpty()) {
                    binding.tryAgain.isVisible = true
                    newCode.addAll(code)
                    clearCode()
                } else {
                    if (newCode == code) {
                        passcodeViewModel.savePasscode(newCode.joinToString())
                        passcodeViewModel.getPasscode()
                    } else {
                        vibrate(this, 500)
                        showBackgroundError()
                    }
                }
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

    private fun showBackgroundError() {
        binding.codeContainer1.setBackgroundResource(R.drawable.passcode_error_background)
        binding.codeContainer2.setBackgroundResource(R.drawable.passcode_error_background)
        binding.codeContainer3.setBackgroundResource(R.drawable.passcode_error_background)
        binding.codeContainer4.setBackgroundResource(R.drawable.passcode_error_background)
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

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}