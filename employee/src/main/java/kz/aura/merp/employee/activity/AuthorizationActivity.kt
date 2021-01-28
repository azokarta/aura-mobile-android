package kz.aura.merp.employee.activity

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import kz.aura.merp.employee.R
import kz.aura.merp.employee.data.viewmodel.AuthViewModel
import kz.aura.merp.employee.util.Helpers.exceptionHandler
import kz.aura.merp.employee.util.Helpers.saveDataByKey
import kz.aura.merp.employee.util.ProgressDialog
import kotlinx.android.synthetic.main.activity_authorization.*
import kz.aura.merp.employee.util.Permissions


class AuthorizationActivity : AppCompatActivity() {

    private val mAuthViewModel: AuthViewModel by viewModels()
    private lateinit var progressDialog: ProgressDialog
    private lateinit var permissions: Permissions

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_authorization)

        permissions = Permissions(this, this)

        // Request camera permission
        permissions.requestCameraPermission()

        // Initialize Loading Dialog
        progressDialog = ProgressDialog(this)

        // Observe LiveData
        mAuthViewModel.transactionId.observe(this, Observer { transactionId ->
            progressDialog.hideLoading() // hide loading
            toOcrWebViewActivity(transactionId) // Go to OcrWebViewActivity
        })
        mAuthViewModel.error.observe(this, Observer { error ->
            progressDialog.hideLoading() // hide loading
            exceptionHandler(error, this) // Show Error with alert dialog
        })

        // PhoneNumber Formatter
        ccp.registerCarrierNumberEditText(phoneNumberEditText)

        // For test
        button2.setOnClickListener {
            goToActivity(ChiefActivity())
        }

    }

    private fun goToActivity(activity: Activity) {
        val intent = Intent(this, activity::class.java)
        startActivity(intent)
        finish()
    }

    fun signIn(view: View) {
        if (ccp.isValidFullNumber) {
            // show loading
            progressDialog.showLoading()

            // Save phoneNumber
            saveDataByKey(this, ccp.fullNumberWithPlus, "phoneNumber")

            // Fetch transactionId for Ocr
            mAuthViewModel.fetchTransactionId()
        } else {
            Toast.makeText(this, "Введите действующий номер телефона", Toast.LENGTH_LONG).show()
        }
    }

    private fun toOcrWebViewActivity(transactionId: String) {
        val intent = Intent(this, OcrWebActivity::class.java)
        intent.putExtra("transactionId", transactionId)
        startActivity(intent)
    }



    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            Permissions.CAMERA_PERMISSION_REQUEST_CODE -> {
                if ((grantResults.isEmpty() && grantResults[0] == PackageManager.PERMISSION_DENIED)) {
                    Toast.makeText(this, "Вы не разрешили доступ к камеру", Toast.LENGTH_LONG)
                        .show()
                    this.permissions.requestCameraPermission()
                } else {
                    this.permissions.requestGpsPermission()
                }
            }
            Permissions.LOCATION_PERMISSION_REQUEST_CODE -> {
                if ((grantResults.isEmpty() && grantResults[0] == PackageManager.PERMISSION_DENIED)) {
                    Toast.makeText(this, "Вы не разрешили доступ к местоположению", Toast.LENGTH_LONG).show()
                    this.permissions.requestGpsPermission()
                }
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }


}