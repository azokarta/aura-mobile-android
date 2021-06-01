package kz.aura.merp.employee.ui.fragment

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import dagger.hilt.android.AndroidEntryPoint
import kz.aura.merp.employee.R
import kz.aura.merp.employee.databinding.FragmentAuthBinding
import kz.aura.merp.employee.ui.activity.PassCodeActivity
import kz.aura.merp.employee.util.*
import kz.aura.merp.employee.viewmodel.AuthViewModel

@AndroidEntryPoint
class AuthFragment : Fragment() {

    private var _binding: FragmentAuthBinding? = null
    private val binding get() = _binding!!
    private var countryCallingCode: String = CountryCode.values()[0].phoneCode
    private lateinit var progressDialog: ProgressDialog
    private lateinit var permissions: Permissions
    private lateinit var authViewModel: AuthViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        authViewModel = ViewModelProvider(this).get(AuthViewModel::class.java)

        _binding = FragmentAuthBinding.inflate(layoutInflater, container, false)
        val root: View = binding.root

        permissions = Permissions(requireContext(), requireActivity())

        // Request gps permission
        permissions.requestGpsPermission()

        // Initialize Loading Dialog
        progressDialog = ProgressDialog(requireContext())

        observeLiveData()

        val countryCodes = CountryCode.values().map { "${it.name} (${it.phoneCode})" }
        val adapter = ArrayAdapter(
            requireContext(),
            R.layout.list_item,
            countryCodes)
        binding.countryCallingCodeText.setText(countryCodes[0])
        (binding.countryCallingCodeField.editText as? AutoCompleteTextView)?.setAdapter(
            adapter
        )

        binding.countryCallingCodeText.setOnItemClickListener { _, _, i, _ ->
            countryCallingCode = CountryCode.values()[i].phoneCode
            binding.phoneNumberText.mask = CountryCode.values()[i].format
        }

        binding.signInBtn.setOnClickListener(::signIn)

        return root
    }

    private fun observeLiveData() {
        authViewModel.signInResponse.observe(viewLifecycleOwner, { res ->
            when (res) {
                is NetworkResult.Success -> {
                    // Hide loading
                    progressDialog.hideLoading()
                    // Save token
                    saveToken(requireContext(), res.data!!.accessToken)
                    // Get info about user
                    authViewModel.getUserInfo()
                }
                is NetworkResult.Loading -> {
                    progressDialog.showLoading()
                }
                is NetworkResult.Error -> {
                    progressDialog.hideLoading()
//                    declareErrorByStatus(res.message, res.status, this, this)
                }
            }
        })
        authViewModel.userInfoResponse.observe(viewLifecycleOwner, { res ->
            when (res) {
                is NetworkResult.Success -> {
                    progressDialog.hideLoading()
                    if (definePosition(res.data!!) == null) {
                        showException(getString(R.string.wrong_position), requireContext())
                    } else {
                        authViewModel.saveSalary(defineCorrectSalary(res.data)!!)
                        // Open PassCode activity for saving code
                        val intent = Intent(requireContext(), PassCodeActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                        intent.putExtra("passCodeStatus", PasscodeStatus.CREATE)
                        startActivity(intent)
                    }
                }
                is NetworkResult.Loading -> {
                    progressDialog.showLoading()
                }
                is NetworkResult.Error -> {
                    progressDialog.hideLoading()
//                    declareErrorByStatus(res.message, res.status, this, this)
                }
            }
        })
    }

    fun signIn(view: View) {
        val phoneNumber = binding.phoneNumberText.rawText
        val password = binding.passwordText.text.toString()
        authViewModel.saveCountryCallingCode(countryCallingCode)
        authViewModel.signIn(countryCallingCode + phoneNumber, password)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            Permissions.LOCATION_PERMISSION_REQUEST_CODE -> {
                if ((grantResults.isEmpty() && grantResults[0] == PackageManager.PERMISSION_DENIED)) {
                    Toast.makeText(
                        requireContext(),
                        getString(R.string.have_not_allowed_access_to_the_location),
                        Toast.LENGTH_LONG
                    ).show()
                    this.permissions.requestGpsPermission()
                }
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}