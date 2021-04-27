package kz.aura.merp.employee.ui.dialog

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import kz.aura.merp.employee.R
import kz.aura.merp.employee.adapter.ResultsAdapter
import kz.aura.merp.employee.model.Result
import kz.aura.merp.employee.databinding.ResultsAlertDialogBinding
import kz.aura.merp.employee.view.OnSelectResult

class PaymentMethodsDialog(private val listener: PaymentMethodsDialogListener, val paymentMethods: List<Result>) : DialogFragment(), OnSelectResult {
    private val methodsAdapter: ResultsAdapter by lazy { ResultsAdapter(this) }

    interface PaymentMethodsDialogListener {
        fun onPaymentMethodsDialogPositiveClick(dialog: DialogFragment, position: Int)
        fun onPaymentMethodsDialogNegativeClick(dialog: DialogFragment) {}
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            // Use the Builder class for convenient dialog construction
            val builder = AlertDialog.Builder(it)
            // Get the layout inflater
            val inflater = requireActivity().layoutInflater;
            val view = inflater.inflate(R.layout.results_alert_dialog, null)
            val binding = ResultsAlertDialogBinding.bind(view)

            binding.resultRecyclerView.layoutManager = LinearLayoutManager(requireContext())
            binding.resultRecyclerView.adapter = methodsAdapter
            methodsAdapter.setData(paymentMethods)

            builder
                .setView(binding.root)
                .setTitle(R.string.paymentMethod)
                .setNegativeButton(R.string.cancel) { dialog, id ->
                    listener.onPaymentMethodsDialogNegativeClick(this)
                    dialog.dismiss()
                }
                .setOnCancelListener { dialog ->
                    listener.onPaymentMethodsDialogNegativeClick(this)
                    dialog.dismiss()
                }
            // Create the AlertDialog object and return it
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }

    override fun selectResult(position: Int) {
        listener.onPaymentMethodsDialogPositiveClick(this, position)
        dialog?.dismiss()
    }

}