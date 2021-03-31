package kz.aura.merp.employee.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kz.aura.merp.employee.data.model.ServiceApplication
import kz.aura.merp.employee.data.model.TrackEmpProcess
import kz.aura.merp.employee.data.model.TrackEmpProcessServiceApplication
import kz.aura.merp.employee.data.network.MasterApi
import kz.aura.merp.employee.data.network.ServiceBuilder
import kotlinx.coroutines.launch
import kz.aura.merp.employee.data.model.Filters
import kz.aura.merp.employee.util.Link
import kz.aura.merp.employee.util.NetworkResult
import kz.aura.merp.employee.util.receiveError
import kz.aura.merp.employee.util.saveData
import java.lang.Exception

class MasterViewModel(application: Application) : AndroidViewModel(application) {
    private val apiService = ServiceBuilder.buildService(MasterApi::class.java, application, Link.SERVICE)

    val applicationsResponse: MutableLiveData<NetworkResult<ArrayList<ServiceApplication>>> = MutableLiveData()
    val trackEmpProcessServiceApplication: MutableLiveData<NetworkResult<ArrayList<TrackEmpProcessServiceApplication>>> = MutableLiveData()
    val updatedServiceApplication: MutableLiveData<NetworkResult<ServiceApplication>> = MutableLiveData()
    val error = MutableLiveData<Any>()
    val filtersResponse: MutableLiveData<NetworkResult<Filters>> = MutableLiveData()

    fun fetchServiceApplications() = viewModelScope.launch {
        try {
            val response = apiService.fetchApplications()

            if (response.isSuccessful) {
                applicationsResponse.postValue(NetworkResult.Success(response.body()!!.data))
            } else {
                applicationsResponse.postValue(NetworkResult.Error(receiveError(response.errorBody()!!), response.code()))
            }
        } catch (e: Exception) {
            applicationsResponse.postValue(NetworkResult.Error(e.message))
        }
    }

    fun fetchFilters(applicationNumber: Long) = viewModelScope.launch {
        try {
            val response = apiService.fetchFilters(applicationNumber)

            if (response.isSuccessful) {
                filtersResponse.postValue(NetworkResult.Success(response.body()!!.data))
            } else {
                filtersResponse.postValue(NetworkResult.Error(receiveError(response.errorBody()!!), response.code()))
            }
        } catch (e: Exception) {
            filtersResponse.postValue(NetworkResult.Error(e.message))
        }
    }

    fun updateServiceApplication(serviceApplication: ServiceApplication) = viewModelScope.launch {
        try {
            val response = apiService.updateServiceApplication(serviceApplication)
            if (response.isSuccessful) {
                updatedServiceApplication.postValue(NetworkResult.Success(response.body()!!.data))
                saveData(response.body()!!.data, getApplication())
            } else {
                updatedServiceApplication.postValue(NetworkResult.Error(receiveError(response.errorBody()!!), response.code()))
            }
        } catch (e: Exception) {
            updatedServiceApplication.postValue(NetworkResult.Error(e.message))
        }
    }

    fun updateStepBusinessProcess(trackEmpProcess: TrackEmpProcess) = viewModelScope.launch {
        try {
            val response = apiService.updateBusinessProcessStep(trackEmpProcess)

            if (!response.isSuccessful) {
                error.postValue(response.errorBody())
            }
        } catch (e: Throwable) {
            error.postValue(e)
        }
    }

    fun changeData(application: ServiceApplication): ArrayList<ServiceApplication>? {
        if (applicationsResponse.value?.data?.isNotEmpty() == true) {
            val foundData = applicationsResponse.value!!.data!!.find { it.applicationNumber == application.applicationNumber }
            val idx = applicationsResponse.value!!.data!!.indexOf(foundData)
            applicationsResponse.value!!.data!![idx] = application
            return applicationsResponse.value!!.data!!
        }
        return null
    }

}