package kz.aura.merp.employee.activity

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.messaging.FirebaseMessaging
import kz.aura.merp.employee.R
import kz.aura.merp.employee.data.viewmodel.AuthViewModel
import kz.aura.merp.employee.databinding.ActivityAuthorizationBinding
import kz.aura.merp.employee.util.Helpers.exceptionHandler
import kz.aura.merp.employee.util.Helpers.openActivityByPositionId
import kz.aura.merp.employee.util.Helpers.saveDataByKey
import kz.aura.merp.employee.util.Helpers.saveStaff
import kz.aura.merp.employee.util.PassCodeStatus
import kz.aura.merp.employee.util.ProgressDialog
import kz.aura.merp.employee.util.Permissions


class AuthorizationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAuthorizationBinding
    private val mAuthViewModel: AuthViewModel by viewModels()
    private lateinit var progressDialog: ProgressDialog
    private lateinit var permissions: Permissions

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAuthorizationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        permissions = Permissions(this, this)

        // Request camera permission
//        permissions.requestCameraPermission()

        // Initialize Loading Dialog
        progressDialog = ProgressDialog(this)

        // Observe LiveData
        mAuthViewModel.authResponse.observe(this, { data ->
            saveDataByKey(this, data.accessToken, "token")
            // Get info about user
            mAuthViewModel.getUserInfo(binding.ccp.fullNumberWithPlus)
        })
        mAuthViewModel.userInfo.observe(this, { data ->
            saveStaff(this, data)
            // Open PassCode activity for saving code
            val intent = Intent(this, PassCodeActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            intent.putExtra("passCodeStatus", PassCodeStatus.CREATE)
            startActivity(intent)
        })
        mAuthViewModel.error.observe(this, { error ->
            progressDialog.hideLoading() // hide loading
            exceptionHandler(error, this) // Show Error with alert dialog
        })

        // For test
        binding.button2.setOnClickListener {
            goToActivity(ChiefActivity())
        }

        // Phone number formatter
        binding.ccp.registerCarrierNumberEditText(binding.phoneNumber)

        // Receive token of FCM
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (!task.isSuccessful) {
                println("Fetching FCM registration token failed ${task.exception}")
            }

            // Get new FCM registration token
            val token = task.result
//            println(token)
        }
    }

    private fun goToActivity(activity: Activity) {
        val intent = Intent(this, activity::class.java)
        startActivity(intent)
        finish()
    }

    fun signIn(view: View) {
        if (binding.ccp.isValidFullNumber) {
            val phoneNumber = binding.ccp.fullNumberWithPlus
            val password = binding.password.text.toString()
            mAuthViewModel.signIn(phoneNumber, password)
        } else {
            Toast.makeText(this, getString(R.string.enterValidPhoneNumber), Toast.LENGTH_LONG).show()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            Permissions.CAMERA_PERMISSION_REQUEST_CODE -> {
                if ((grantResults.isEmpty() && grantResults[0] == PackageManager.PERMISSION_DENIED)) {
                    Toast.makeText(this, getString(R.string.haveNotAllowedAccessToTheCamera), Toast.LENGTH_LONG).show()
                    this.permissions.requestCameraPermission()
                } else {
                    this.permissions.requestGpsPermission()
                }
            }
            Permissions.LOCATION_PERMISSION_REQUEST_CODE -> {
                if ((grantResults.isEmpty() && grantResults[0] == PackageManager.PERMISSION_DENIED)) {
                    Toast.makeText(this, getString(R.string.haveNotAllowedAccessToTheLocation), Toast.LENGTH_LONG).show()
                    this.permissions.requestGpsPermission()
                }
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }


}