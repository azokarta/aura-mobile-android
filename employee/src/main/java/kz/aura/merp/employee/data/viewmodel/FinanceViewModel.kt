package kz.aura.merp.employee.data.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kz.aura.merp.employee.data.model.Plan
import kz.aura.merp.employee.data.model.TrackEmpProcess
import kz.aura.merp.employee.data.service.FinanceApi
import kz.aura.merp.employee.data.service.ServiceBuilder
import kz.aura.merp.employee.util.Helpers
import kotlinx.coroutines.launch
import java.lang.Exception

class FinanceViewModel (application: Application) : AndroidViewModel(application) {
    private val apiService = ServiceBuilder.buildService(FinanceApi::class.java, application)

    val error = MutableLiveData<Any>()
    val plans = MutableLiveData<ArrayList<Plan>>()
    val trackEmpProcessCollectMoney = MutableLiveData<ArrayList<TrackEmpProcess>>()
    val updatedPlan = MutableLiveData<Plan>()

    fun fetchClients(collectorId: Long) = viewModelScope.launch {
        try {
            val response = apiService.fetchClients(collectorId)

            if (response.isSuccessful) {
                plans.postValue(response.body()!!.data)
            } else {
                error.postValue(response.errorBody())
            }
        } catch (e: Exception) {
            error.postValue(e)
        }
    }

    fun fetchTrackEmpProcessCollectMoney(maCollectMoneyId: Long) = viewModelScope.launch {
        try {
            val response = apiService.fetchTrackEmpProcessCollectMoney(maCollectMoneyId)

            if (response.isSuccessful) {
                trackEmpProcessCollectMoney.postValue(response.body()!!.data)
            } else {
                error.postValue(response.errorBody())
            }
        } catch (e: Exception) {
            error.postValue(e)
        }
    }

    fun updatePlan(plan: Plan) = viewModelScope.launch {
        try {
            val response = apiService.updatePlan(plan)

            if (response.isSuccessful) {
                updatedPlan.postValue(response.body()!!.data)
                Helpers.saveData(response.body()!!.data, getApplication())
            } else {
                error.postValue(response.errorBody())
            }
        } catch (e: Exception) {
            error.postValue(e)
        }
    }

    fun updateBusinessProcessStep(trackEmpProcess: TrackEmpProcess) = viewModelScope.launch {
        try {
            val response = apiService.updateBusinessProcessStep(trackEmpProcess)

            if (!response.isSuccessful) {
                error.postValue(response.errorBody())
            }
        } catch (e: Exception) {
            error.postValue(e)
        }
    }

    fun changeData(plan: Plan): ArrayList<Plan>? {
        if (plans.value?.isNotEmpty() == true) {
            val foundData = plans.value!!.find { it.maCollectMoneyId == plan.maCollectMoneyId }
            val idx = plans.value!!.indexOf(foundData)
            plans.value!![idx] = plan
            return plans.value!!
        }
        return null
    }

}