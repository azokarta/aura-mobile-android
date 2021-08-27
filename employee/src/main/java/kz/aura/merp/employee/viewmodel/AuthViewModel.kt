package kz.aura.merp.employee.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kz.aura.merp.employee.base.AppPreferences
import kz.aura.merp.employee.base.NetworkResult
import kz.aura.merp.employee.data.auth.AuthRepository
import kz.aura.merp.employee.data.core.CoreRepository
import kz.aura.merp.employee.model.AuthResponse
import kz.aura.merp.employee.model.Salary
import kz.aura.merp.employee.model.ResponseHelper
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val coreRepository: CoreRepository,
    val preferences: AppPreferences,
    application: Application
) : AndroidViewModel(application) {

    private val scope: CoroutineScope = CoroutineScope(Job() + Dispatchers.IO)

    val signInResponse = MutableLiveData<NetworkResult<AuthResponse>>()
    val userInfoResponse = MutableLiveData<NetworkResult<ResponseHelper<List<Salary>>>>()
    val saveFcmTokenResponse = MutableLiveData<NetworkResult<ResponseHelper<Nothing>>>()

    fun signIn(phoneNumber: String, password: String) = scope.launch {
        val response = authRepository.signin(phoneNumber, password)
        signInResponse.postValue(response)
    }

    fun getUserInfo() = scope.launch {
        val response = coreRepository.getUserInfo()
        userInfoResponse.postValue(response)
    }

    fun saveFcmToken(token: String) = scope.launch {
        val response = coreRepository.saveFcmToken(token)
        saveFcmTokenResponse.postValue(response)
    }

    fun clearPreferences() = preferences.clear()

}