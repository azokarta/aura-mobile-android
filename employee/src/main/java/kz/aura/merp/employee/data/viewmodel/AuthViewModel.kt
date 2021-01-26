package kz.aura.merp.employee.data.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.preference.PreferenceManager
import kz.aura.merp.employee.data.service.AuthApi
import kz.aura.merp.employee.data.service.ServiceBuilder
import kz.aura.merp.employee.util.Helpers.saveTokenAndStaff
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.lang.Exception

class AuthViewModel(application: Application) : AndroidViewModel(application) {
    private val apiService = ServiceBuilder.buildService(AuthApi::class.java, application)

    val transactionId = MutableLiveData<String>()
    val onErrorToken = MutableLiveData<Boolean>()
    val positionId = MutableLiveData<Int>()
    val error = MutableLiveData<Any>()

    fun fetchTransactionId() = viewModelScope.launch(Dispatchers.IO) {
        try {
            val response = apiService.fetchTransactionId()

            if (response.isSuccessful) {
                transactionId.postValue(response.body()!!.data)
            } else {
                error.postValue(response.errorBody())
            }
        } catch (e: Exception) {
            println(e)
            error.postValue(e)
        }
    }

    fun fetchToken(transactionId: String) = viewModelScope.launch(Dispatchers.IO) {
        try {
            val phoneNumber = getPhoneNumber()
            val response = apiService.fetchToken(phoneNumber, transactionId)

            if (response.isSuccessful && response.body()!!.success) {
                saveTokenAndStaff(getApplication<Application>().applicationContext, response.body()!!.data)
                positionId.postValue(response.body()!!.data.userInfo.salaryDtoList[0].positionId)
            } else {
                onErrorToken.postValue(true)
            }
        } catch (e: Exception) {
            error.postValue(e)
        }
    }

    private fun getPhoneNumber(): String {
        return PreferenceManager
            .getDefaultSharedPreferences(getApplication())
            .getString("phoneNumber", "")!!
    }

}