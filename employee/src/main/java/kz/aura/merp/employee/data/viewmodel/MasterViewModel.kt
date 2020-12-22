package kz.aura.merp.employee.data.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kz.aura.merp.employee.data.model.ServiceApplication
import kz.aura.merp.employee.data.model.TrackEmpProcess
import kz.aura.merp.employee.data.model.TrackEmpProcessServiceApplication
import kz.aura.merp.employee.service.MasterApi
import kz.aura.merp.employee.service.ServiceBuilder
import kz.aura.merp.employee.util.Helpers
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.lang.Exception

class MasterViewModel(application: Application) : AndroidViewModel(application) {
    private val apiService = ServiceBuilder.buildService(MasterApi::class.java, application)

    val applications = MutableLiveData<ArrayList<ServiceApplication>>()
    val trackEmpProcessServiceApplication = MutableLiveData<ArrayList<TrackEmpProcessServiceApplication>>()
    val updatedServiceApplication = MutableLiveData<ServiceApplication>()
    val error = MutableLiveData<Any>()

    fun fetchServiceApplications(masterId: Long) = viewModelScope.launch(Dispatchers.IO) {
        try {
            val response = apiService.fetchApplications(masterId)

            if (response.isSuccessful) {
                applications.postValue(response.body()!!.data)
            } else {
                error.postValue(response.errorBody())
            }
        } catch (e: Exception) {
            error.postValue(e)
        }
    }

    fun fetchTrackEmpProcessServiceApplication(serviceApplicationId: Long) = viewModelScope.launch(Dispatchers.IO) {
        try {
            val response = apiService.fetchTrackEmpProcessServiceApplication(serviceApplicationId)

            if (response.isSuccessful) {
                trackEmpProcessServiceApplication.postValue(response.body()!!.data)
            } else {
                error.postValue(response.errorBody())
            }
        } catch (e: Exception) {
            error.postValue(e)
        }
    }

    fun updateServiceApplication(serviceApplication: ServiceApplication) = viewModelScope.launch(Dispatchers.IO) {
        try {
            val response = apiService.updateServiceApplication(serviceApplication)
            if (response.isSuccessful) {
                updatedServiceApplication.postValue(response.body()!!.data)
                Helpers.saveData(response.body()!!.data, getApplication())
            } else {
                error.postValue(response.errorBody())
            }
        } catch (e: Exception) {
            error.postValue(e)
        }
    }

    fun updateStepBusinessProcess(trackEmpProcess: TrackEmpProcess) = viewModelScope.launch(Dispatchers.IO) {
        try {
            val response = apiService.updateBusinessProcessStep(trackEmpProcess)

            if (!response.isSuccessful) {
                error.postValue(response.errorBody())
            }
        } catch (e: Throwable) {
            error.postValue(e)
        }
    }

    fun changeData(application: ServiceApplication): ArrayList<ServiceApplication> {
        val foundData = applications.value!!.find { it.id == application.id }
        val idx = applications.value!!.indexOf(foundData)
        applications.value!![idx] = application
        return applications.value!!
    }

}