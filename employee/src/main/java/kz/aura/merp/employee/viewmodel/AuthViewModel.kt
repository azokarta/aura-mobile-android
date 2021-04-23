package kz.aura.merp.employee.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import kz.aura.merp.employee.model.AuthResponse
import kz.aura.merp.employee.model.Staff
import kz.aura.merp.employee.data.repository.authRepository.AuthRepository
import kz.aura.merp.employee.util.NetworkResult
import kz.aura.merp.employee.util.receiveErrorMessage
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    application: Application
) : AndroidViewModel(application) {

    val signInResponse: MutableLiveData<NetworkResult<AuthResponse>> = MutableLiveData()
    val userInfoResponse: MutableLiveData<NetworkResult<Staff>> = MutableLiveData()

    fun signIn(phoneNumber: String, password: String) = viewModelScope.launch {
        signInResponse.postValue(NetworkResult.Loading())
        try {
            val response = authRepository.remote.signin(phoneNumber, password)

            if (response.isSuccessful) {
                signInResponse.postValue(NetworkResult.Success(response.body()!!))
            } else {
                signInResponse.postValue(
                    NetworkResult.Error(
                        receiveErrorMessage(response.errorBody()!!),
                        response.code()
                    )
                )
            }
        } catch (e: Exception) {
            signInResponse.postValue(NetworkResult.Error(e.message))
        }
    }

    fun getUserInfo(phoneNumber: String) = viewModelScope.launch {
        try {
            val response = authRepository.remote.getUserInfo(phoneNumber)

            if (response.isSuccessful) {
                userInfoResponse.postValue(NetworkResult.Success(response.body()!!.data))
            } else {
                userInfoResponse.postValue(
                    NetworkResult.Error(
                        receiveErrorMessage(response.errorBody()!!),
                        response.code()
                    )
                )
            }
        } catch (e: Exception) {
            userInfoResponse.postValue(NetworkResult.Error(e.message))
        }
    }
}