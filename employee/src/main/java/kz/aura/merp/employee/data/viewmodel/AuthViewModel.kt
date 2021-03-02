package kz.aura.merp.employee.data.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.preference.PreferenceManager
import com.google.android.gms.common.util.IOUtils.toByteArray
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kz.aura.merp.employee.data.model.AuthResponse
import kz.aura.merp.employee.data.model.Staff
import kz.aura.merp.employee.data.service.AuthApi
import kz.aura.merp.employee.data.service.ServiceBuilder
import kz.aura.merp.employee.util.Constants
import kz.aura.merp.employee.util.Helpers.saveDataByKey
import kz.aura.merp.employee.util.Helpers.saveStaff
import okhttp3.MediaType
import okhttp3.RequestBody


class AuthViewModel(application: Application) : AndroidViewModel(application) {
    private val authApiService = ServiceBuilder.buildService(
        AuthApi::class.java,
        application,
        Constants.AUTH_URL
    )

    val authResponse = MutableLiveData<AuthResponse>()
    val userInfo = MutableLiveData<Staff>()
    val error = MutableLiveData<Any>()

    fun signIn(phoneNumber: String, password: String) = viewModelScope.launch {
        try {
            val response = authApiService.signin("password", phoneNumber, password)

            if (response.isSuccessful) {
                authResponse.postValue(response.body())
            } else {
                error.postValue(response.errorBody())
            }
        } catch (e: Exception) {
            error.postValue(e)
        }
    }

    fun getUserInfo(phoneNumber: String) {
        println(phoneNumber)
        val apiService = ServiceBuilder.buildService(AuthApi::class.java, getApplication())
        viewModelScope.launch {
            try {
                val response = apiService.getUserInfo(phoneNumber)

                if (response.isSuccessful) {
                    userInfo.postValue(response.body()!!.data)
                } else {
                    error.postValue(response.errorBody())
                }
            } catch (e: Exception) {
                error.postValue(e)
            }
        }
    }

//    fun fetchTransactionId() = viewModelScope.launch {
//        try {
//            val response = apiService.fetchTransactionId()
//
//            if (response.isSuccessful) {
//                transactionId.postValue(response.body()!!.data)
//            } else {
//                error.postValue(response.errorBody())
//            }
//        } catch (e: Exception) {
//            error.postValue(e)
//        }
//    }

//    fun fetchToken(transactionId: String) = viewModelScope.launch {
//        try {
//            val phoneNumber = getPhoneNumber()
//            val response = apiService.fetchToken(phoneNumber, transactionId)
//
//            if (response.isSuccessful && response.body()!!.success) {
//                saveTokenAndStaff(getApplication<Application>().applicationContext, response.body()!!.data)
//                positionId.postValue(response.body()!!.data.userInfo.salaryDtoList[0].positionId)
//            } else {
//                onErrorToken.postValue(true)
//            }
//        } catch (e: Exception) {
//            error.postValue(e)
//        }
//    }

    private fun getPhoneNumber(): String {
        return PreferenceManager
            .getDefaultSharedPreferences(getApplication())
            .getString("phoneNumber", "")!!
    }
}