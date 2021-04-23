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

class ResultsDialogFragment(private val results: List<Result>) : DialogFragment(), OnSelectResult {
    private lateinit var listener: ResultsDialogListener
    private val resultsAdapter: ResultsAdapter by lazy { ResultsAdapter(this) }

    interface ResultsDialogListener {
        fun onResultsDialogPositiveClick(dialog: DialogFragment, position: Int)
    }

    // Override the Fragment.onAttach() method to instantiate the NoticeDialogListener
    override fun onAttach(context: Context) {
        super.onAttach(context)
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            listener = context as ResultsDialogListener
        } catch (e: ClassCastException) {
            // The activity doesn't implement the interface, throw exception
            throw ClassCastException((context.toString() +
                    " must implement ResultsDialogListener"))
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
            binding.resultRecyclerView.adapter = resultsAdapter
            resultsAdapter.setData(results)

            builder
                .setView(binding.root)
                .setTitle(R.string.result)
                .setNegativeButton(R.string.cancel) { dialog, id ->
                    dialog.dismiss()
                }
            // Create the AlertDialog object and return it
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }

    override fun selectResult(position: Int) {
        listener.onResultsDialogPositiveClick(this, position)
        dialog?.dismiss()
    }

}