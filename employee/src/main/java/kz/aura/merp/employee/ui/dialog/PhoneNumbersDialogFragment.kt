package kz.aura.merp.employee.ui.dialog

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import kz.aura.merp.employee.R

class PhoneNumbersDialogFragment(private val listener: PhoneNumbersDialogListener, private val phoneNumbers: List<String?>?) : DialogFragment()  {

    interface PhoneNumbersDialogListener {
        fun selectPhoneNumber(phoneNumber: String?)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            builder.setTitle(R.string.select_phone_number)
                .setItems(phoneNumbers?.toTypedArray()) { _, which ->
                    listener.selectPhoneNumber(phoneNumbers?.get(which))
                }
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }
}