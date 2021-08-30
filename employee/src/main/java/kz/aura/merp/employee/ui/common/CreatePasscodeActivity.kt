package kz.aura.merp.employee.ui.common

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.widget.Toolbar
import androidx.core.view.isVisible
import dagger.hilt.android.AndroidEntryPoint
import kz.aura.merp.employee.R
import kz.aura.merp.employee.base.BaseActivity
import kz.aura.merp.employee.databinding.ActivityCreatePasscodeBinding
import kz.aura.merp.employee.util.*
import kz.aura.merp.employee.viewmodel.AuthViewModel
import kz.aura.merp.employee.viewmodel.PasscodeViewModel

@AndroidEntryPoint
class CreatePasscodeActivity : BaseActivity() {

    private lateinit var binding: ActivityCreatePasscodeBinding

    private val passcodeViewModel: PasscodeViewModel by viewModels()
    private val code = arrayListOf<Int>()
    private val newCode = arrayListOf<Int>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreatePasscodeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar.root)
        supportActionBar?.title = getString(R.string.create_passcode)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        setListenersOfNumbers()
    }

    private fun setListenersOfNumbers() {
        with (binding) {
            number1.setOnClickListener { addNumber(1) }
            number2.setOnClickListener { addNumber(2) }
            number3.setOnClickListener { addNumber(3) }
            number4.setOnClickListener { addNumber(4) }
            number5.setOnClickListener { addNumber(5) }
            number6.setOnClickListener { addNumber(6) }
            number7.setOnClickListener { addNumber(7) }
            number8.setOnClickListener { addNumber(8) }
            number9.setOnClickListener { addNumber(9) }
            number0.setOnClickListener { addNumber(0) }
            deleteNumber.setOnClickListener {
                removeLastNumber()
            }
        }
    }

    override fun onDestroy() {
        val savedPasscode = passcodeViewModel.preferences.passcode
        if (savedPasscode.isNullOrBlank()) {
            passcodeViewModel.clearPreferences()
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
                        val passcode = newCode.joinToString()
                        passcodeViewModel.preferences.passcode = passcode
                        val staffPosition = definePosition(passcodeViewModel.preferences.salary)
                        openActivityByPosition(staffPosition)
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
        with (binding) {
            codeContainer1.setBackgroundResource(R.drawable.passcode_error_background)
            codeContainer2.setBackgroundResource(R.drawable.passcode_error_background)
            codeContainer3.setBackgroundResource(R.drawable.passcode_error_background)
            codeContainer4.setBackgroundResource(R.drawable.passcode_error_background)
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

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}