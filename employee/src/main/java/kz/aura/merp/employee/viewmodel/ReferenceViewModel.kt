package kz.aura.merp.employee.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kz.aura.merp.employee.data.model.*
import kz.aura.merp.employee.data.network.ReferenceApi
import kz.aura.merp.employee.data.network.ServiceBuilder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kz.aura.merp.employee.util.Link
import java.lang.Exception

class ReferenceViewModel(application: Application) : AndroidViewModel(application) {
    private val apiService = ServiceBuilder.buildService(ReferenceApi::class.java, application.applicationContext, Link.MAIN)

    val demoResults = MutableLiveData<ArrayList<DemoResult>>()
    val trackStepOrdersBusinessProcesses = MutableLiveData<ArrayList<BusinessProcessStatus>>()
    val serviceApplicationStatus = MutableLiveData<ArrayList<ServiceApplicationStatus>>()
    val error = MutableLiveData<Any>()
    val contractTypes = MutableLiveData<ArrayList<ContractType>>()

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

    fun fetchContractTypes(staffId: Long) = viewModelScope.launch(Dispatchers.IO) {
        try {
            val response = apiService.getContractTypes(staffId)

            if (response.isSuccessful) {
                contractTypes.postValue(response.body()!!.data)
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