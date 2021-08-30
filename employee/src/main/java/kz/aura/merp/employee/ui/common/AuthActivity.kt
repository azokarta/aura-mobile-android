package kz.aura.merp.employee.ui.common

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.activity.viewModels
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.AndroidEntryPoint
import kz.aura.merp.employee.R
import kz.aura.merp.employee.base.BaseActivity
import kz.aura.merp.employee.base.NetworkResult
import kz.aura.merp.employee.viewmodel.AuthViewModel
import kz.aura.merp.employee.databinding.ActivityAuthBinding
import kz.aura.merp.employee.model.Salary
import kz.aura.merp.employee.util.*
import timber.log.Timber

@AndroidEntryPoint
class AuthActivity : BaseActivity() {

    private lateinit var binding: ActivityAuthBinding

    private val authViewModel: AuthViewModel by viewModels()
    private lateinit var progressDialog: ProgressDialog
    private var countryCallingCode: String = CountryCode.values()[0].phoneCode

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)

        progressDialog = ProgressDialog(this)

        observeLiveData()

        setupCountryCallingCodeDropdown()

        with (binding) {
            countryCallingCodeText.setOnItemClickListener { _, _, i, _ ->
                countryCallingCode = CountryCode.values()[i].phoneCode
            }
            signInBtn.setOnClickListener { signIn() }
        }
    }

    private fun setupCountryCallingCodeDropdown() {
        val countryCodes = CountryCode.values().map { "${it.name} (${it.phoneCode})" }
        val adapter = ArrayAdapter(
            this,
            R.layout.list_item,
            countryCodes)
        binding.countryCallingCodeText.setText(countryCodes[0])
        (binding.countryCallingCodeField.editText as? AutoCompleteTextView)?.setAdapter(
            adapter
        )
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

            Timber.tag("FIREBASE").d("Token: ${token.toString()}")
        }
    }

    private fun observeLiveData() {
        authViewModel.signInResponse.observe(this, { res ->
            when (res) {
                is NetworkResult.Success -> {
                    progressDialog.hideLoading()

                    val token = res.data?.accessToken

                    // Saving the token to get a info about user
                    authViewModel.preferences.token = token

                    // Calling getUserInfo to get a info about user
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

                    val salaries = res.data?.data

                    if (!salaries.isNullOrEmpty()) {
                        val salary: Salary? = defineCorrectSalary(salaries)
                        val position: StaffPosition? = definePosition(salary)

                        if (position == null || salary == null) {
                            showException(getString(R.string.wrong_position), this)
                        } else {
                            authViewModel.preferences.countryCallingCode = countryCallingCode
                            authViewModel.preferences.salary = salary

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
                    navigateToActivity(CreatePasscodeActivity::class)
                }
                is NetworkResult.Loading -> progressDialog.showLoading()
                is NetworkResult.Error -> {
                    progressDialog.hideLoading()
                    showException(res.message, this)
                }
            }
        })
    }

    private fun signIn() {
        hideKeyboard(this)
        val phoneNumber = countryCallingCode + binding.phoneNumberText.text.toString()
        val password = binding.passwordText.text.toString()
        authViewModel.signIn(phoneNumber, password)
    }


}