package kz.aura.merp.employee.ui.dialog

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.MotionEvent
import android.widget.EditText
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.DialogFragment
import kz.aura.merp.employee.R


class AlertDialogFragment(private val listener: AlertDialogListener, private val title: String) : DialogFragment() {

    interface AlertDialogListener {
        fun onAlertDialogPositiveClick(dialog: DialogFragment)
        fun onAlertDialogNegativeClick(dialog: DialogFragment) {}
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            // Use the Builder class for convenient dialog construction
            val builder = AlertDialog.Builder(it)

            builder
                .setTitle(title)
                .setPositiveButton(R.string.yes) { dialog, _ ->
                    listener.onAlertDialogPositiveClick(this)
                    dialog.dismiss()
                }
                .setNegativeButton(R.string.no) { dialog, _ ->
                    listener.onAlertDialogNegativeClick(this)
                    dialog.dismiss()
                }
                .setOnCancelListener { dialog ->
                    listener.onAlertDialogNegativeClick(this)
                    dialog.dismiss()
                }
            // Create the AlertDialog object and return it
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }
}