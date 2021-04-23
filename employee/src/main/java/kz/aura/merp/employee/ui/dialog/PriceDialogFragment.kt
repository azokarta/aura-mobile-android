package kz.aura.merp.employee.ui.dialog

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.DialogFragment
import kz.aura.merp.employee.R
import kz.aura.merp.employee.databinding.PriceAlertDialogBinding

class PriceDialogFragment(val remainder: Int) : DialogFragment() {
    private lateinit var listener: PriceDialogListener

    interface PriceDialogListener {
        fun onPriceDialogPositiveClick(dialog: DialogFragment, price: Int)
        fun onPriceDialogNegativeClick(dialog: DialogFragment)
    }

    // Override the Fragment.onAttach() method to instantiate the NoticeDialogListener
    override fun onAttach(context: Context) {
        super.onAttach(context)
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the PriceDialogListener so we can send events to the host
            listener = context as PriceDialogListener
        } catch (e: ClassCastException) {
            // The activity doesn't implement the interface, throw exception
            throw ClassCastException((context.toString() +
                    " must implement PriceDialogListener"))
        }
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