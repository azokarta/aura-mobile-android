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
class AuthorizationActivity : BaseActivity() {

    private lateinit var binding: ActivityAuthorizationBinding
    private val authViewModel: AuthViewModel by viewModels()
    private lateinit var progressDialog: ProgressDialog
    private lateinit var permissions: Permissions
    private var countryCallingCode: String = CountryCode.values()[0].phoneCode

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAuthorizationBinding.inflate(layoutInflater)
        setContentView(binding.root)

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
                showException("Fetching FCM registration token failed ${task.exception}", this)
            }

            // Get new FCM registration token
            val token = task.result
            token?.let { authViewModel.saveFcmToken(it) }
            Log.d("FIREBASE_TOKEN", token.toString())
        }
    }

    private fun observeLiveData() {
        authViewModel.signInResponse.observe(this, { res ->
            when (res) {
                is NetworkResult.Success -> {
                    // Hide loading
                    progressDialog.hideLoading()
                    // Save token
                    saveToken(this, res.data!!.accessToken)
                    // Get info about user
                    authViewModel.getUserInfo()
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
        authViewModel.userInfoResponse.observe(this, { res ->
            when (res) {
                is NetworkResult.Success -> {
                    progressDialog.hideLoading()

                    if (!res.data?.data.isNullOrEmpty()) {
                        val salary: Salary? = defineCorrectSalary(res.data?.data)
                        val position: StaffPosition? = definePosition(salary)

                        if (position == null || salary == null) {
                            showException(getString(R.string.wrong_position), this)
                        } else {
                            authViewModel.saveSalary(salary)
                            getTokenFromFirebase()
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
        authViewModel.saveFcmTokenResponse.observe(this, { res ->
            when (res) {
                is NetworkResult.Success -> {
                    progressDialog.hideLoading()
                    val intent = Intent(this, CreatePasscodeActivity::class.java)
                    startActivity(intent)
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
        authViewModel.saveCountryCallingCode(countryCallingCode)
        authViewModel.signIn(countryCallingCode + phoneNumber, password)
    }


}