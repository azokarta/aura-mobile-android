package kz.aura.merp.employee.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import kz.aura.merp.employee.data.model.AuthResponse
import kz.aura.merp.employee.data.model.Staff
import kz.aura.merp.employee.data.network.AuthApi
import kz.aura.merp.employee.data.network.ServiceBuilder
import kz.aura.merp.employee.util.Link
import kz.aura.merp.employee.util.NetworkResult
import kz.aura.merp.employee.util.receiveError

class AuthViewModel(application: Application) : AndroidViewModel(application) {
    private val authApiService = ServiceBuilder.buildService(
        AuthApi::class.java,
        getApplication(),
        Link.AUTH
    )
    val signInResponse: MutableLiveData<NetworkResult<AuthResponse>> = MutableLiveData()
    val userInfoResponse: MutableLiveData<NetworkResult<Staff>> = MutableLiveData()

    fun signIn(phoneNumber: String, password: String) = viewModelScope.launch {
        signInResponse.postValue(NetworkResult.Loading())
        try {
            val response = authApiService.signin("password", phoneNumber, password)

            if (response.isSuccessful) {
                signInResponse.postValue(NetworkResult.Success(response.body()!!))
            } else {
                signInResponse.postValue(NetworkResult.Error(receiveError(response.errorBody()!!), response.code()))
            }
        } catch (e: Exception) {
            signInResponse.postValue(NetworkResult.Error(e.message))
        }
    }

    fun getUserInfo(phoneNumber: String) {
        val apiService = ServiceBuilder.buildService(AuthApi::class.java, getApplication(), Link.MAIN)
        viewModelScope.launch {
            try {
                val response = apiService.getUserInfo(phoneNumber)

                if (response.isSuccessful) {
                    userInfoResponse.postValue(NetworkResult.Success(response.body()!!.data))
                } else {
                    userInfoResponse.postValue(NetworkResult.Error(receiveError(response.errorBody()!!), response.code()))
                }
            } catch (e: Exception) {
                userInfoResponse.postValue(NetworkResult.Error(e.message))
            }
        }
    }
}