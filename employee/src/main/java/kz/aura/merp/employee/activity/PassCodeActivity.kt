package kz.aura.merp.employee.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.preference.PreferenceManager
import kotlinx.android.synthetic.main.activity_pass_code.*
import kz.aura.merp.employee.R
import kz.aura.merp.employee.util.Helpers.showToast

class PassCodeActivity : AppCompatActivity() {

    private val code = arrayListOf<Int>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pass_code)

        setListenersOfNumbers()
    }

    private fun setListenersOfNumbers() {
        number1.setOnClickListener {
            fillLine(1)
        }

        number2.setOnClickListener {
            fillLine(2)
        }

        number3.setOnClickListener {
            fillLine(3)
        }

        number4.setOnClickListener {
            fillLine(4)
        }

        number5.setOnClickListener {
            fillLine(5)
        }

        number6.setOnClickListener {
            fillLine(6)
        }

        number7.setOnClickListener {
            fillLine(7)
        }

        number8.setOnClickListener {
            fillLine(8)
        }

        number9.setOnClickListener {
            fillLine(9)
        }

        number0.setOnClickListener {
            fillLine(0)
        }

        deleteNumber.setOnClickListener {
            removeLastNumber()
        }
    }

    private fun fillLine(num: Int) {
        when (code.size) {
            0 -> typed1.setBackgroundResource(R.color.colorPrimary)
            1 -> typed2.setBackgroundResource(R.color.colorPrimary)
            2 -> typed3.setBackgroundResource(R.color.colorPrimary)
            3 -> typed4.setBackgroundResource(R.color.colorPrimary)
        }
        code.add(num)

        if (code.size == 4) {
            val typedCode = code.joinToString(separator = "")
            val userCode = receivePassCode()
            if (userCode == typedCode) {
                showToast(this, "Success")
            } else {
                showToast(this, "WRONG")
                paintLinesToBlack()
                code.clear()
            }
        }
    }

    private fun removeLastNumber() {
        if (code.size != 0) {
            when (code.size) {
                1 -> typed1.setBackgroundResource(R.color.colorBlack)
                2 -> typed2.setBackgroundResource(R.color.colorBlack)
                3 -> typed3.setBackgroundResource(R.color.colorBlack)
                4 -> typed4.setBackgroundResource(R.color.colorBlack)
            }
            code.removeLast()
        }
    }

    private fun receivePassCode() = PreferenceManager.getDefaultSharedPreferences(this).getString("passCode", "")

    private fun paintLinesToBlack() {
        typed1.setBackgroundResource(R.color.colorBlack)
        typed2.setBackgroundResource(R.color.colorBlack)
        typed3.setBackgroundResource(R.color.colorBlack)
        typed4.setBackgroundResource(R.color.colorBlack)
    }
}