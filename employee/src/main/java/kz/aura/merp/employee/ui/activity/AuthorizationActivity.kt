package kz.aura.merp.employee.ui.activity

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.AndroidEntryPoint
import kz.aura.merp.employee.R
import kz.aura.merp.employee.viewmodel.AuthViewModel
import kz.aura.merp.employee.databinding.ActivityAuthorizationBinding
import kz.aura.merp.employee.model.Salary
import kz.aura.merp.employee.util.*

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

//        permissions = Permissions(this, this)
//
//        // Request gps permission
//        permissions.requestGpsPermission()

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

        binding.signInBtn.setOnClickListener(::signIn)
    }

    private fun getTokenFromFirebase() {
        // Receive token of FCM
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (!task.isSuccessful) {
                println("Fetching FCM registration token failed ${task.exception}")
            }

            // Get new FCM registration token
            val token = task.result
            Log.d("FIREBASE_TOKEN", token.toString())
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
                is NetworkResult.Loading -> progressDialog.showLoading()
                is NetworkResult.Error -> {
                    progressDialog.hideLoading()
                    if (res.status == ErrorStatus.UNAUTHORIZED) {
                        showException(getString(R.string.wrong_login_or_password), this)
                    } else {
                        showException(res.message, this)
                    }
                }
            }
        })
        mAuthViewModel.userInfoResponse.observe(this, { res ->
            when (res) {
                is NetworkResult.Success -> {
                    progressDialog.hideLoading()

                    if (!res.data?.data.isNullOrEmpty()) {
                        val salary: Salary? = defineCorrectSalary(res.data?.data)
                        val position: StaffPosition? = definePosition(salary)

                        if (position == null || salary == null) {
                            showException(getString(R.string.wrong_position), this)
                        } else {
                            mAuthViewModel.saveSalary(salary)
                            getTokenFromFirebase()
                            val intent = Intent(this, CreatePasscodeActivity::class.java)
                            startActivity(intent)
                        }
                    } else {
                        showException(getString(R.string.user_does_not_exist), this)
                    }
                }
                is NetworkResult.Loading -> progressDialog.showLoading()
                is NetworkResult.Error -> {
                    progressDialog.hideLoading()
                    showException(res.message, this)
                }
            }
        })
    }

    private fun signIn(view: View) {
        hideKeyboard(this)
        val phoneNumber = binding.phoneNumberText.rawText
        val password = binding.passwordText.text.toString()
        mAuthViewModel.saveCountryCallingCode(countryCallingCode)
        mAuthViewModel.signIn(countryCallingCode + phoneNumber, password)
    }

//    override fun onRequestPermissionsResult(
//        requestCode: Int,
//        permissions: Array<out String>,
//        grantResults: IntArray
//    ) {
//        when (requestCode) {
//            Permissions.LOCATION_PERMISSION_REQUEST_CODE -> {
//                if ((grantResults.isEmpty() && grantResults[0] == PackageManager.PERMISSION_DENIED)) {
//                    Toast.makeText(
//                        this,
//                        getString(R.string.have_not_allowed_access_to_the_location),
//                        Toast.LENGTH_LONG
//                    ).show()
//                    this.permissions.requestGpsPermission()
//                }
//            }
//        }
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
//    }


}