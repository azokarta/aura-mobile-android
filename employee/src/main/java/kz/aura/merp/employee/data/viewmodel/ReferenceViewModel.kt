package kz.aura.merp.employee.data.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kz.aura.merp.employee.data.model.*
import kz.aura.merp.employee.data.service.ReferenceApi
import kz.aura.merp.employee.data.service.ServiceBuilder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.lang.Exception

class ReferenceViewModel(application: Application) : AndroidViewModel(application) {
    private val apiService = ServiceBuilder.buildService(ReferenceApi::class.java, application.applicationContext)

    val trackedBusinessProcesses = MutableLiveData<ArrayList<TrackedBusinessProcess>>()
    val demoResults = MutableLiveData<ArrayList<DemoResult>>()
    val trackStepOrdersBusinessProcesses = MutableLiveData<ArrayList<TrackStepOrdersBusinessProcess>>()
    val serviceApplicationStatus = MutableLiveData<ArrayList<ServiceApplicationStatus>>()
    val maCollectResults = MutableLiveData<ArrayList<FinanceResult>>()
    val error = MutableLiveData<Any>()

    fun fetchTrackedBussinessProcesses() = viewModelScope.launch {
        try {
            val response = apiService.getTrackedBusinessProcesses()
            if (response.isSuccessful) {
                trackedBusinessProcesses.postValue(response.body()!!.data)
            } else {
                error.postValue(response.errorBody())
            }
        } catch (e: Exception) {
            error.postValue(e)
        }
    }

    fun fetchDemoResults() = viewModelScope.launch {
        try {
            val response = apiService.getDemoResults()

            if (response.isSuccessful) {
                demoResults.postValue(response.body()!!.data)
            } else {
                error.postValue(response.errorBody())
            }
        } catch (e: Exception) {
            error.postValue(e)
        }
    }

    fun fetchTrackStepOrdersBusinessProcesses(bpId: Int) = viewModelScope.launch {
        try {
            val response = apiService.getTrackStepOrdersBussinessProcesses(bpId)

            if (response.isSuccessful) {
                trackStepOrdersBusinessProcesses.postValue(response.body()!!.data)
            } else {
                error.postValue(response.errorBody())
            }
        } catch (e: Exception) {
            error.postValue(e)
        }
    }

    fun fetchServiceApplicationStatus() = viewModelScope.launch {
        try {
            val response = apiService.getServiceApplicationStatus()

            if (response.isSuccessful) {
                serviceApplicationStatus.postValue(response.body()!!.data)
            } else {
                error.postValue(response.errorBody())
            }
        } catch (e: Exception) {
            error.postValue(e)
        }
    }

    fun fetchMaCollectResults() = viewModelScope.launch {
        try {
            val response = apiService.fetchMaCollectResults()

            if (response.isSuccessful) {
                maCollectResults.postValue(response.body()!!.data)
            } else {
                error.postValue(response.errorBody())
            }
        } catch (e: Exception) {
            error.postValue(e)
        }
    }

    fun createStaffLocation(staffLocation: StaffLocation) = viewModelScope.launch {
        try {
            val response = apiService.newStaffLocation(staffLocation)

            if (!response.isSuccessful) {
                error.postValue(response.errorBody())
            }
        } catch (e: Exception) {
            error.postValue(e)
        }
    }
}