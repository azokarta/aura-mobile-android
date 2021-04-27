package kz.aura.merp.employee.ui.dialog

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.DialogFragment
import kz.aura.merp.employee.R
import kz.aura.merp.employee.databinding.PriceAlertDialogBinding

class PriceDialogFragment(private val listener: PriceDialogListener, val remainder: Int) : DialogFragment() {

    interface PriceDialogListener {
        fun onPriceDialogPositiveClick(dialog: DialogFragment, price: Int)
        fun onPriceDialogNegativeClick(dialog: DialogFragment) {}
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            // Use the Builder class for convenient dialog construction
            val builder = AlertDialog.Builder(it)
            // Get the layout inflater
            val inflater = requireActivity().layoutInflater;
            val view = inflater.inflate(R.layout.price_alert_dialog, null)
            val binding = PriceAlertDialogBinding.bind(view)

            builder
                .setView(binding.root)
                .setTitle(R.string.price)
                .setOnCancelListener { dialog ->
                    listener.onPriceDialogNegativeClick(this)
                    dialog.dismiss()
                }

            binding.saveBtn.setOnClickListener {
                val price = binding.priceEditText.text.toString().toInt()
                if (price <= remainder) {
                    listener.onPriceDialogPositiveClick(this, price)
                    dialog?.dismiss()
                }
            }

            binding.cancelBtn.setOnClickListener {
                listener.onPriceDialogNegativeClick(this)
                dialog?.dismiss()
            }

            binding.priceEditText.addTextChangedListener { editable ->
                if (!editable.isNullOrBlank()) {
                    if (remainder <= editable.toString().toDouble()) {
                        binding.priceInputLayout.error = getString(R.string.error)
                    } else {
                        binding.priceInputLayout.error = null
                    }
                    if (editable.toString() == "0") {
                        binding.priceEditText.text = null
                    }
                }
            }

            // Create the AlertDialog object and return it
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }

}