package kz.aura.merp.employee.data.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kz.aura.merp.employee.data.model.Demo
import kz.aura.merp.employee.data.model.TrackEmpProcess
import kz.aura.merp.employee.data.service.DealerApi
import kz.aura.merp.employee.data.service.ServiceBuilder
import kz.aura.merp.employee.util.Helpers.saveData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.lang.Exception

class DealerViewModel(application: Application): AndroidViewModel(application) {
    private val apiService = ServiceBuilder.buildService(DealerApi::class.java, application)

    val demoList = MutableLiveData<ArrayList<Demo>>()
    val updatedDemo = MutableLiveData<Demo>()
    val trackEmpProcessDemo = MutableLiveData<ArrayList<TrackEmpProcess>>()
    val error = MutableLiveData<Any>()
    val smsSent = MutableLiveData<Boolean>()

    fun fetchAll(staffId: Long) = viewModelScope.launch(Dispatchers.IO) {
        try {
            val response = apiService.getAll(staffId)

            if (response.isSuccessful) {
                demoList.postValue(response.body()!!.data)
            } else {
                error.postValue(response.errorBody())
            }
        } catch (e: Exception) {
            error.postValue(e)
        }
    }

    fun fetchDemoById(demoId: Long) = viewModelScope.launch(Dispatchers.IO) {
        try {
            val response = apiService.fetchDemoById(demoId)

            if (response.isSuccessful) {
                updatedDemo.postValue(response.body()!!.data)
            } else {
                error.postValue(response.errorBody())
            }
        } catch (e: Exception) {
            error.postValue(e)
        }
    }

    fun updateDemo(demo: Demo) = viewModelScope.launch(Dispatchers.IO) {
        try {
            val response = apiService.updateDemo(demo)

            if (response.isSuccessful) {
                updatedDemo.postValue(response.body()!!.data)
                saveData(response.body()!!.data, getApplication())
            } else {
                error.postValue(response.errorBody())
            }
        } catch (e: Exception) {
            error.postValue(e)
        }
    }

    fun fetchTrackEmpProcessDemo(demoId: Long) = viewModelScope.launch(Dispatchers.IO) {
        try {
            val response = apiService.getTrackEmpProcessDemo(demoId)

            if (response.isSuccessful) {
                trackEmpProcessDemo.postValue(response.body()!!.data)
            } else {
                error.postValue(response.errorBody())
            }
        } catch (e: Exception) {
            error.postValue(e)
        }
    }

    fun updateStepBusinessProcess(trackEmpProcess: TrackEmpProcess) = viewModelScope.launch(Dispatchers.IO) {
        try {
            val response = apiService.updateStepBusinessProcess(trackEmpProcess)

            if (!response.isSuccessful) {
                error.postValue(response.errorBody())
            }
        } catch (e: Throwable) {
            error.postValue(e)
        }
    }

    fun changeData(demo: Demo): ArrayList<Demo>? {
        if (demoList.value?.isNotEmpty() == true) {
            val foundData = demoList.value!!.find { it.demoId == demo.demoId }
            val idx = demoList.value!!.indexOf(foundData)
            demoList.value!![idx] = demo
            return demoList.value!!
        }
        return null
    }

    fun sendSms(demoId: Long, phoneCode: String, phoneNumber: String) = viewModelScope.launch(Dispatchers.IO) {
        try {
            val response = apiService.sendSms(demoId, phoneCode, phoneNumber)

            if (response.isSuccessful && response.body()!!.success) {
                smsSent.postValue(true)
            } else {
                error.postValue(response.errorBody())
            }
        } catch (e: Exception) {
            error.postValue(e)
        }
    }

    fun updateStatus(demoId: Long) = viewModelScope.launch(Dispatchers.IO) {
        try {
            val response = apiService.updateStatus(demoId)

            if (response.isSuccessful) {
                updatedDemo.postValue(response.body()!!.data)
                println("updated")
            } else {
                error.postValue(response.errorBody())
            }
        } catch (e: Exception) {
            error.postValue(e)
        }
    }
}