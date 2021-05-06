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
import kz.aura.merp.employee.data.DataStoreRepository
import kz.aura.merp.employee.model.AuthResponse
import kz.aura.merp.employee.model.Salary
import kz.aura.merp.employee.data.repository.authRepository.AuthRepository
import kz.aura.merp.employee.util.NetworkResult
import kz.aura.merp.employee.util.receiveErrorMessage
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val dataStoreRepository: DataStoreRepository,
    application: Application
) : AndroidViewModel(application) {

    private val scope: CoroutineScope = CoroutineScope(Job() + Dispatchers.IO)

    val signInResponse: MutableLiveData<NetworkResult<AuthResponse>> = MutableLiveData()
    val userInfoResponse: MutableLiveData<NetworkResult<ArrayList<Salary>>> = MutableLiveData()
    val token: MutableLiveData<String> = MutableLiveData()
    val salary: MutableLiveData<Salary> = MutableLiveData()

    fun signIn(phoneNumber: String, password: String) = scope.launch {
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

    fun getUserInfo() = scope.launch {
        try {
            val response = authRepository.remote.getUserInfo()

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

    fun saveToken(token: String) = scope.launch {
        dataStoreRepository.saveToken(token)
    }

    fun saveSalary(salary: Salary) = scope.launch {
        dataStoreRepository.saveSalary(salary)
    }

    fun getToken() = scope.launch {
        dataStoreRepository.tokenFlow.collect { value ->
            token.postValue(value)
        }
    }

    fun getSalary() = scope.launch {
        dataStoreRepository.salaryFlow.collect { value ->
            salary.postValue(value)
        }
    }
}