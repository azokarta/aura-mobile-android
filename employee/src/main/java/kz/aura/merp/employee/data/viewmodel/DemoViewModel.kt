package kz.aura.merp.employee.data.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kz.aura.merp.employee.data.model.Demo
import kz.aura.merp.employee.data.model.DemoModify
import kz.aura.merp.employee.data.model.TrackEmpProcess
import kz.aura.merp.employee.service.DemoApi
import kz.aura.merp.employee.service.ServiceBuilder
import kz.aura.merp.employee.util.Helpers.saveData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.lang.Exception

class DemoViewModel(application: Application): AndroidViewModel(application) {
    private val apiService = ServiceBuilder.buildService(DemoApi::class.java, application)

    val demoList = MutableLiveData<ArrayList<Demo>>()
    val updatedDemo = MutableLiveData<Demo>()
    val trackEmpProcessDemo = MutableLiveData<ArrayList<TrackEmpProcess>>()
    val error = MutableLiveData<Any>()

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

    fun updateDemo(demo: DemoModify) = viewModelScope.launch(Dispatchers.IO) {
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

    fun changeData(demo: Demo): ArrayList<Demo> {
        val foundData = demoList.value!!.find { it.demoId == demo.demoId }
        val idx = demoList.value!!.indexOf(foundData)
        demoList.value!![idx] = demo
        return demoList.value!!
    }
}