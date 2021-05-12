package kz.aura.merp.employee.ui.activity

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import dagger.hilt.android.AndroidEntryPoint
import kz.aura.merp.employee.R
import kz.aura.merp.employee.viewmodel.AuthViewModel
import kz.aura.merp.employee.databinding.ActivityAuthorizationBinding
import kz.aura.merp.employee.util.*
import javax.inject.Inject

@AndroidEntryPoint
class AuthorizationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAuthorizationBinding
    private val mAuthViewModel: AuthViewModel by viewModels()
    private lateinit var progressDialog: ProgressDialog
    private lateinit var permissions: Permissions
    private var countryCallingCode: String = CountryCode.values()[0].phoneCode

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAuthorizationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Turn off screenshot
        window.setFlags(
            WindowManager.LayoutParams.FLAG_SECURE,
            WindowManager.LayoutParams.FLAG_SECURE
        )

        permissions = Permissions(this, this)

        // Request gps permission
        permissions.requestGpsPermission()

        // Initialize Loading Dialog
        progressDialog = ProgressDialog(this)

        observeLiveData()

        val countryCodes = CountryCode.values().map { "${it.name} (${it.phoneCode})" }
        val adapter = ArrayAdapter(
            this,
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
    }

    private fun observeLiveData() {
        mAuthViewModel.signInResponse.observe(this, { res ->
            when (res) {
                is NetworkResult.Success -> {
                    // Hide loading
                    progressDialog.hideLoading()
                    // Save token
                    saveToken(this, res.data!!.accessToken)
                    // Get info about user
                    mAuthViewModel.getUserInfo()
                }
                is NetworkResult.Loading -> {
                    progressDialog.showLoading()
                }
                is NetworkResult.Error -> {
                    progressDialog.hideLoading()
                    declareErrorByStatus(res.message, res.status, this, this)
                }
            }
        })
        mAuthViewModel.userInfoResponse.observe(this, { res ->
            when (res) {
                is NetworkResult.Success -> {
                    progressDialog.hideLoading()
                    if (definePosition(res.data!!) == null) {
                        showException(getString(R.string.wrongPosition), this)
                    } else {
                        mAuthViewModel.saveSalary(defineCorrectSalary(res.data)!!)
                        // Open PassCode activity for saving code
                        val intent = Intent(this, PassCodeActivity::class.java)
                        intent.flags =
                            Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                        intent.putExtra("passCodeStatus", PassCodeStatus.CREATE)
                        startActivity(intent)
                    }
                }
                is NetworkResult.Loading -> {
                    progressDialog.showLoading()
                }
                is NetworkResult.Error -> {
                    progressDialog.hideLoading()
                    declareErrorByStatus(res.message, res.status, this, this)
                }
            }
        })
    }

    fun signIn(view: View) {
        val phoneNumber = binding.phoneNumberText.rawText
        val password = binding.passwordText.text.toString()
        mAuthViewModel.saveCountryCallingCode(countryCallingCode)
        mAuthViewModel.signIn(countryCallingCode + phoneNumber, password)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            Permissions.CAMERA_PERMISSION_REQUEST_CODE -> {
                if ((grantResults.isEmpty() && grantResults[0] == PackageManager.PERMISSION_DENIED)) {
                    Toast.makeText(
                        this,
                        getString(R.string.haveNotAllowedAccessToTheCamera),
                        Toast.LENGTH_LONG
                    ).show()
                    this.permissions.requestCameraPermission()
                } else {
                    this.permissions.requestGpsPermission()
                }
            }
            Permissions.LOCATION_PERMISSION_REQUEST_CODE -> {
                if ((grantResults.isEmpty() && grantResults[0] == PackageManager.PERMISSION_DENIED)) {
                    Toast.makeText(
                        this,
                        getString(R.string.haveNotAllowedAccessToTheLocation),
                        Toast.LENGTH_LONG
                    ).show()
                    this.permissions.requestGpsPermission()
                }
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }


}