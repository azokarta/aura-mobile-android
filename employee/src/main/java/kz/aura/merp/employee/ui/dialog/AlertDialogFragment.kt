package kz.aura.merp.employee.ui.dialog

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import kz.aura.merp.employee.R

class AlertDialogFragment(private val title: String) : DialogFragment() {
    private lateinit var listener: AlertDialogListener

    interface AlertDialogListener {
        fun onAlertDialogPositiveClick(dialog: DialogFragment)
        fun onAlertDialogNegativeClick(dialog: DialogFragment)
    }

    // Override the Fragment.onAttach() method to instantiate the NoticeDialogListener
    override fun onAttach(context: Context) {
        super.onAttach(context)
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            listener = context as AlertDialogListener
        } catch (e: ClassCastException) {
            // The activity doesn't implement the interface, throw exception
            throw ClassCastException((context.toString() +
                    " must implement AlertDialogListener"))
        }
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