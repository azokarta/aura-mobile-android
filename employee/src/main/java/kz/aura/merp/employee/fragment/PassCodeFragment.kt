package kz.aura.merp.employee.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.preference.PreferenceManager
import kotlinx.android.synthetic.main.fragment_pass_code.*
import kotlinx.android.synthetic.main.fragment_pass_code.view.*
import kz.aura.merp.employee.R
import kz.aura.merp.employee.util.Helpers.showToast

class PassCodeFragment : Fragment() {
    private val code = arrayListOf<Int>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_pass_code, container, false)
        setListenersNumber(view)
        return view
    }

    private fun setListenersNumber(view : View) {
        view.number1.setOnClickListener {
            fillLine(1)
        }

        view.number2.setOnClickListener {
            fillLine(2)
        }

        view.number3.setOnClickListener {
            fillLine(3)
        }

        view.number4.setOnClickListener {
            fillLine(4)
        }

        view.number5.setOnClickListener {
            fillLine(5)
        }

        view.number6.setOnClickListener {
            fillLine(6)
        }

        view.number7.setOnClickListener {
            fillLine(7)
        }

        view.number8.setOnClickListener {
            fillLine(8)
        }

        view.number9.setOnClickListener {
            fillLine(9)
        }

        view.number0.setOnClickListener {
            fillLine(0)
        }

        view.deleteNumber.setOnClickListener {
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
               showToast(requireContext(), "Success")
           } else {
               showToast(requireContext(), "WRONG")
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

    private fun receivePassCode() = PreferenceManager.getDefaultSharedPreferences(requireContext()).getString("passCode", "")

    private fun paintLinesToBlack() {
        typed1.setBackgroundResource(R.color.colorBlack)
        typed2.setBackgroundResource(R.color.colorBlack)
        typed3.setBackgroundResource(R.color.colorBlack)
        typed4.setBackgroundResource(R.color.colorBlack)
    }
}
