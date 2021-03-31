package kz.aura.merp.employee.ui.alertDialog

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import kz.aura.merp.employee.R
import kz.aura.merp.employee.adapter.ResultsAdapter
import kz.aura.merp.employee.data.model.Bank
import kz.aura.merp.employee.data.model.Result
import kz.aura.merp.employee.databinding.ResultsAlertDialogBinding
import kz.aura.merp.employee.view.OnSelectResult

class BanksDialogFragment(private val banks: List<Result>) : DialogFragment(), OnSelectResult {
    private lateinit var listener: BanksDialogListener
    private val banksAdapter: ResultsAdapter by lazy { ResultsAdapter(this) }

    interface BanksDialogListener {
        fun onBanksDialogPositiveClick(dialog: DialogFragment, position: Int)
        fun onBanksDialogNegativeClick(dialog: DialogFragment)
    }

    // Override the Fragment.onAttach() method to instantiate the NoticeDialogListener
    override fun onAttach(context: Context) {
        super.onAttach(context)
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            listener = context as BanksDialogListener
        } catch (e: ClassCastException) {
            // The activity doesn't implement the interface, throw exception
            throw ClassCastException((context.toString() +
                    " must implement NoticeDialogListener"))
        }
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
            binding.resultRecyclerView.adapter = banksAdapter
            banksAdapter.setData(banks)

            builder
                .setView(binding.root)
                .setTitle(R.string.paymentMethod)
                .setNegativeButton(R.string.cancel) { dialog, id ->
                    dialog.dismiss()
                }
                .setOnCancelListener {
                    listener.onBanksDialogNegativeClick(this)
                }
            // Create the AlertDialog object and return it
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }

    override fun selectResult(position: Int) {
        listener.onBanksDialogPositiveClick(this, position)
        dialog?.dismiss()
    }

}