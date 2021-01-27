package kz.aura.merp.employee.fragment

import android.app.Activity
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import kotlinx.android.synthetic.main.fragment_authorization.view.*
import kz.aura.merp.employee.R
import kz.aura.merp.employee.data.viewmodel.AuthViewModel
import kz.aura.merp.employee.util.Helpers
import kz.aura.merp.employee.util.Helpers.hideToolbar
import kz.aura.merp.employee.util.Permissions
import kz.aura.merp.employee.util.ProgressDialog

class AuthorizationFragment : Fragment() {

    private val mAuthViewModel: AuthViewModel by viewModels()
    private lateinit var progressDialog: ProgressDialog
    private lateinit var permissions: Permissions
    private lateinit var mView: View

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Hide toolbar
//        activity?.let { hideToolbar(it) }

        mView = inflater.inflate(R.layout.fragment_authorization, container, false)
        permissions = Permissions(requireContext(), requireActivity())

        // Request camera permission
        permissions.requestCameraPermission()

        // Initialize Loading Dialog
        progressDialog = ProgressDialog(requireContext())

        mAuthViewModel.error.observe(viewLifecycleOwner, { error ->
            progressDialog.hideLoading() // hide loading
            Helpers.exceptionHandler(error, requireContext()) // Show Error with alert dialog
        })

        // PhoneNumber Formatter
        mView.ccp.registerCarrierNumberEditText(mView.phoneNumberEditText)

        // For test
        mView.button2.setOnClickListener {
            findNavController().navigate(R.id.action_authorizationFragment_to_chiefFragment)
        }

        mView.signIn.setOnClickListener(::signIn)

        return mView
    }

    private fun signIn(view: View) {
        if (mView.ccp.isValidFullNumber) {
            // show loading
            progressDialog.showLoading()

            // Save phoneNumber
            Helpers.saveDataByKey(requireContext(), mView.ccp.fullNumberWithPlus, "phoneNumber")

            // Fetch transactionId for Ocr
            mAuthViewModel.fetchTransactionId().observe(this, { transactionId ->
                progressDialog.hideLoading() // hide loading
                val action = AuthorizationFragmentDirections.actionAuthorizationFragmentToOcrWebFragment(transactionId)
                findNavController().navigate(action)
            })
        } else {
            Toast.makeText(requireContext(), "Введите действующий номер телефона", Toast.LENGTH_LONG).show()
        }
    }

}