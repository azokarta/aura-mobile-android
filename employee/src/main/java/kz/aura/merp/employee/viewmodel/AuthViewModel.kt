package kz.aura.merp.employee.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kz.aura.merp.employee.R
import kz.aura.merp.employee.data.DataStoreRepository
import kz.aura.merp.employee.model.AuthResponse
import kz.aura.merp.employee.model.Salary
import kz.aura.merp.employee.data.repository.authRepository.AuthRepository
import kz.aura.merp.employee.model.ResponseHelper
import kz.aura.merp.employee.util.*
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val dataStoreRepository: DataStoreRepository,
    application: Application
) : AndroidViewModel(application) {

    private val scope: CoroutineScope = CoroutineScope(Job() + Dispatchers.IO)

    val signInResponse: MutableLiveData<NetworkResult<AuthResponse>> = MutableLiveData()
    val userInfoResponse: MutableLiveData<NetworkResult<ResponseHelper<ArrayList<Salary>>>> = MutableLiveData()
    val salary: MutableLiveData<Salary> = MutableLiveData()
    val saveFcmTokenResponse: MutableLiveData<NetworkResult<Nothing>> = MutableLiveData()
    val receiveMessage = { str: Int ->
        getApplication<Application>().getString(str)
    }

    fun saveCountryCallingCode(countryCallingCode: String) = scope.launch {
        dataStoreRepository.saveCountryCallingCode(countryCallingCode)
    }

    fun signIn(phoneNumber: String, password: String) = scope.launch {
        signInResponse.postValue(NetworkResult.Loading())
        if (isInternetAvailable(getApplication())) {
            try {
                val response = authRepository.remote.signin(phoneNumber, password)

                if (response.isSuccessful) {
                    signInResponse.postValue(NetworkResult.Success(response.body()!!))
                } else {
                    signInResponse.postValue(handleError(response))
                }
            } catch (e: Exception) {
                signInResponse.postValue(NetworkResult.Error(e.message))
            }
        } else {
            signInResponse.postValue(internetIsNotConnected())
        }
    }

    fun getUserInfo() = scope.launch {
        userInfoResponse.postValue(NetworkResult.Loading())
        if (isInternetAvailable(getApplication())) {
            try {
                val response = authRepository.remote.getUserInfo()

                if (response.isSuccessful) {
                    userInfoResponse.postValue(NetworkResult.Success(response.body()!!))
                } else {
                    userInfoResponse.postValue(handleError(response))
                }
            } catch (e: Exception) {
                userInfoResponse.postValue(NetworkResult.Error(e.message))
            }
        } else {
            userInfoResponse.postValue(internetIsNotConnected())
        }
    }

    fun saveFcmToken(token: String) = scope.launch {
        saveFcmTokenResponse.postValue(NetworkResult.Loading())
        if (isInternetAvailable(getApplication())) {
            try {
                val response = authRepository.remote.saveFcmToken(token)

                if (response.isSuccessful) {
                    saveFcmTokenResponse.postValue(NetworkResult.Success())
                } else {
                    saveFcmTokenResponse.postValue(handleError(response))
                }
            } catch (e: Exception) {
                saveFcmTokenResponse.postValue(NetworkResult.Error(e.message))
            }
        } else {
            saveFcmTokenResponse.postValue(internetIsNotConnected())
        }
    }

    fun saveSalary(salary: Salary) = scope.launch {
        dataStoreRepository.saveSalary(salary)
    }

    fun getSalary() = scope.launch {
        dataStoreRepository.salaryFlow.collect { value ->
            salary.postValue(value)
        }
    }

    fun clearSettings() = scope.launch { dataStoreRepository.clearSettings() }

    private fun <T> internetIsNotConnected(): NetworkResult<T> = NetworkResult.Error(
        receiveMessage(R.string.network_disconnected),
        ErrorStatus.INTERNET_IS_NOT_AVAILABLE
    )

    private fun <T, R> handleError(res: Response<T>): NetworkResult<R> {
        val message = receiveErrorMessage(res.errorBody()!!)
        return when (res.code()) {
            401 -> NetworkResult.Error(message, ErrorStatus.UNAUTHORIZED)
            400 -> NetworkResult.Error(message, ErrorStatus.BAD_REQUEST)
            404 -> NetworkResult.Error(message, ErrorStatus.NOT_FOUND)
            500 -> NetworkResult.Error(message, ErrorStatus.INTERNAL_SERVER_ERROR)
            else -> NetworkResult.Error(message, null)
        }
    }

}