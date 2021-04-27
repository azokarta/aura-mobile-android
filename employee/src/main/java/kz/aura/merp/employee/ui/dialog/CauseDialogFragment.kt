package kz.aura.merp.employee.ui.dialog

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kz.aura.merp.employee.R
import kz.aura.merp.employee.databinding.CauseAlertDialogBinding

class CauseDialogFragment(private val listener: CauseDialogListener) : DialogFragment() {

    interface CauseDialogListener {
        fun onCauseDialogPositiveClick(dialog: DialogFragment, cause: String)
        fun onCauseDialogNegativeClick(dialog: DialogFragment) {}
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            // Get the layout inflater
            val inflater = requireActivity().layoutInflater;
            val view = inflater.inflate(R.layout.cause_alert_dialog, null)
            val binding = CauseAlertDialogBinding.bind(view)

            binding.saveBtn.setOnClickListener {
                val cause = binding.causeEditText.text.toString()
                if (cause.isBlank() || cause.length < 3) {
                    binding.causeInputLayout.error = getString(R.string.error)
                } else {
                    listener.onCauseDialogPositiveClick(this, cause)
                    dialog?.dismiss()
                }
            }

            binding.cancelBtn.setOnClickListener {
                listener.onCauseDialogNegativeClick(this)
                dialog?.dismiss()
            }

            MaterialAlertDialogBuilder(requireContext())
                .setView(binding.root)
                .setTitle(getString(R.string.cause))
                .setOnCancelListener { dialog ->
                    listener.onCauseDialogNegativeClick(this)
                    dialog.dismiss()
                }
                .show()

        } ?: throw IllegalStateException("Activity cannot be null")
    }

}