package kz.aura.merp.employee.data.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kz.aura.merp.employee.data.model.Client
import kz.aura.merp.employee.data.model.TrackEmpProcess
import kz.aura.merp.employee.service.FinanceApi
import kz.aura.merp.employee.service.ServiceBuilder
import kz.aura.merp.employee.util.Helpers
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.lang.Exception

class FinanceViewModel (application: Application) : AndroidViewModel(application) {
    private val apiService = ServiceBuilder.buildService(FinanceApi::class.java, application)

    val error = MutableLiveData<Any>()
    val clients = MutableLiveData<ArrayList<Client>>()
    val trackEmpProcessCollectMoney = MutableLiveData<ArrayList<TrackEmpProcess>>()
    val updatedClient = MutableLiveData<Client>()

    fun fetchClients(collectorId: Long) = viewModelScope.launch(Dispatchers.IO) {
        try {
            val response = apiService.fetchClients(collectorId)

            if (response.isSuccessful) {
                clients.postValue(response.body()!!.data)
            } else {
                error.postValue(response.errorBody())
            }
        } catch (e: Exception) {
            error.postValue(e)
        }
    }

    fun fetchTrackEmpProcessCollectMoney(maCollectMoneyId: Long) = viewModelScope.launch(Dispatchers.IO) {
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

    fun updateClient(client: Client) = viewModelScope.launch(Dispatchers.IO) {
        try {
            val response = apiService.updateClient(client)

            if (response.isSuccessful) {
                updatedClient.postValue(response.body()!!.data)
                Helpers.saveData(response.body()!!.data, getApplication())
            } else {
                error.postValue(response.errorBody())
            }
        } catch (e: Exception) {
            error.postValue(e)
        }
    }

    fun updateBusinessProcessStep(trackEmpProcess: TrackEmpProcess) = viewModelScope.launch(Dispatchers.IO) {
        try {
            val response = apiService.updateBusinessProcessStep(trackEmpProcess)

            if (!response.isSuccessful) {
                error.postValue(response.errorBody())
            }
        } catch (e: Exception) {
            error.postValue(e)
        }
    }

    fun changeData(client: Client): ArrayList<Client> {
        val foundData = clients.value!!.find { it.maCollectMoneyId == client.maCollectMoneyId }
        val idx = clients.value!!.indexOf(foundData)
        clients.value!![idx] = client
        return clients.value!!
    }

}