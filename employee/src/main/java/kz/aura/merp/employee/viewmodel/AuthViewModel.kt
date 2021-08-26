package kz.aura.merp.employee.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kz.aura.merp.employee.R
import kz.aura.merp.employee.base.AppPreferences
import kz.aura.merp.employee.model.AuthResponse
import kz.aura.merp.employee.model.Salary
import kz.aura.merp.employee.data.repository.authRepository.AuthRepository
import kz.aura.merp.employee.data.repository.coreRepository.CoreRepository
import kz.aura.merp.employee.model.ResponseHelper
import kz.aura.merp.employee.util.*
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val coreRepository: CoreRepository,
    val preferences: AppPreferences,
    application: Application
) : AndroidViewModel(application) {

    private val scope: CoroutineScope = CoroutineScope(Job() + Dispatchers.IO)

    val signInResponse: MutableLiveData<NetworkResult<AuthResponse>> = MutableLiveData()
    val userInfoResponse: MutableLiveData<NetworkResult<ResponseHelper<ArrayList<Salary>>>> = MutableLiveData()
    val saveFcmTokenResponse: MutableLiveData<NetworkResult<ResponseHelper<Nothing>>> = MutableLiveData()

    fun signIn(phoneNumber: String, password: String) = scope.launch {
        executeRequest(signInResponse) {
            val response = authRepository.remote.signin(phoneNumber, password)

            if (response.isSuccessful) {
                signInResponse.postValue(NetworkResult.Success(response.body()))
            } else {
                signInResponse.postValue(handleError(response))
            }
        }
    }

    fun getUserInfo() = scope.launch {
        executeRequest(userInfoResponse) {
            val response = coreRepository.remote.getUserInfo()

            if (response.isSuccessful) {
                userInfoResponse.postValue(NetworkResult.Success(response.body()))
            } else {
                userInfoResponse.postValue(handleError(response))
            }
        }
    }

    fun saveFcmToken(token: String) = scope.launch {
        executeRequest(saveFcmTokenResponse) {
            val response = coreRepository.remote.saveFcmToken(token)

            if (response.isSuccessful) {
                saveFcmTokenResponse.postValue(NetworkResult.Success())
            } else {
                saveFcmTokenResponse.postValue(handleError(response))
            }
        }
    }

    fun clearSettings() = preferences.clear()

    private inline fun <T> executeRequest(responseLiveData: MutableLiveData<NetworkResult<T>>, body: () -> Unit) {
        if (isInternetAvailable(getApplication())) {
            responseLiveData.postValue(NetworkResult.Loading())
            try {
                body()
            } catch (e: Exception) {
                responseLiveData.postValue(NetworkResult.Error(e.message))
            }
        } else {
            responseLiveData.postValue(NetworkResult.Error(
                receiveMessage(R.string.internet_is_not_connected),
                ErrorStatus.INTERNET_IS_NOT_AVAILABLE
            ))
        }
    }

    private fun <T> handleError(response: Response<T>): NetworkResult<T> {
        return when (response.code()) {
            404 -> NetworkResult.Error(receiveMessage(R.string.not_found), ErrorStatus.NOT_FOUND)
            else -> NetworkResult.Error(receiveMessage(R.string.try_again_later), ErrorStatus.INTERNAL_SERVER_ERROR)
        }
    }

    private fun receiveMessage(str: Int) = getApplication<Application>().getString(str)

}