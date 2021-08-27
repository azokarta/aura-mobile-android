package kz.aura.merp.employee.ui.common

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import kz.aura.merp.employee.R

class UserAvatarActionsDialogFragment : DialogFragment() {
    // Use this instance of the interface to deliver action events
    private var listener: UserAvatarActionsDialogListener? = null

    interface UserAvatarActionsDialogListener {
        fun selectOpenPhoto()
        fun selectRemovePhoto()
        fun selectEditPhoto()
    }

    fun setListener(listener: UserAvatarActionsDialogListener) {
        this.listener = listener
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            // Use the Builder class for convenient dialog construction
            val builder = AlertDialog.Builder(it)

            builder.setItems(R.array.user_avatar_action_entries) { dialog, which ->
                when (which) {
                    0 -> listener?.selectOpenPhoto()
                    1 -> listener?.selectEditPhoto()
                    2 -> listener?.selectRemovePhoto()
                }
                dialog.dismiss()
            }

            // Create the AlertDialog object and return it
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }

}