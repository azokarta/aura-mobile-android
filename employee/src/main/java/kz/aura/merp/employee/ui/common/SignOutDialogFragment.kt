package kz.aura.merp.employee.ui.common

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import dagger.hilt.android.AndroidEntryPoint
import kz.aura.merp.employee.R
import kz.aura.merp.employee.util.navigateToActivity
import kz.aura.merp.employee.viewmodel.AuthViewModel

@AndroidEntryPoint
class SignOutDialogFragment : DialogFragment() {

    private val authViewModel: AuthViewModel by viewModels()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            // Use the Builder class for convenient dialog construction
            val builder = AlertDialog.Builder(it)

            builder.setTitle(getString(R.string.really_want_to_sign_out))
                .setPositiveButton(R.string.yes) { dialog, _ ->
                    authViewModel.clearPreferences()
                    navigateToActivity(AuthActivity::class, true)
                    dialog.dismiss()
                }
                .setNegativeButton(R.string.no) { dialog, _ ->
                    dialog.dismiss()
                }

            // Create the AlertDialog object and return it
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }
}