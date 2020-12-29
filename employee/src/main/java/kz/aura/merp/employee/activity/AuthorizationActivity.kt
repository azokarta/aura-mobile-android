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

        // Testing
//        PreferenceManager.getDefaultSharedPreferences(this)
//            .edit()
//            .putString("language", "ru")
//            .putString("staff", "{mobile:+77476167191,salaryDtoList:[{begDate:2018-03-01,branchId:28,bukrs:1000,countryId:1,departmentId:1,endDate:2099-01-01,positionDto:{nameEn:Manager,nameRu:Менеджер,nameTr:Şef,positionId:3,spras:ru},positionId:3,salaryId:9720,staffId:1028}],staffDto:{middlename:OSPANULY,mobile:87015517055,sacked:0,staffId:1028},staffId:1028,userId:1177,username:ashat.o}")
//            .putString("token", "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJhdWQiOlsiV0VSUF9DTElFTlRfUkVTT1VSQ0UiXSwidXNlcl9pZCI6MTE3NywidXNlcl9uYW1lIjoiYXNoYXQubyIsInNjb3BlIjpbInRydXN0IiwicmVhZCIsIndyaXRlIl0sImV4cCI6MTYwODc0NTA4MSwiYXV0aG9yaXRpZXMiOlsi0JTQtdC80L7QodC10LrRgNC10YLQsNGA0YwiXSwianRpIjoiY2NkZjQzODQtMDQ4Yy00OGQ3LThlNDktZTYyMmQyNGI1MGU4IiwiZW1haWwiOiJhc2hhdEBnbWFpbC5jb20iLCJjbGllbnRfaWQiOiJXRVJQIn0.KU0S-i4H9Mdv0Lomus_3pjRZOyGg8XzTf0OWs6kij1ozTwr48pEsYKu4VK-vr3s-K7gGqGNS-ZhXKLvTuePpIVFSXa3CRNK41H-R6MRWxgx4rfTmZAFXvgi4VvmqmvLwM3Ne5SljIyQunW-fn2fHaThpSjaYM-ZQUYako7ay1i78SDR1EGEIzSV9uWySLF8aBeNzlPoChsDud5No571fFLFegBDuUWKZ41sXKrp8v07PxtqtsOKbAfz_km6dlDVhkO2n2-BHL6VVJePdee1LgzAJZCuvkIz9ZoWbo7WGnz_oIoqnaXdvD3rVOWrZQUn1CQXkfDk5qF50Dv2uRQxhuA")
//            .apply()

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
            1000 -> {

                if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Вы не разрешили доступ к камеру", Toast.LENGTH_LONG)
                        .show()
                    this.permissions.requestCameraPermission()
                }
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }


}