package kz.aura.merp.employee.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kz.aura.merp.employee.data.network.FinanceApi
import kz.aura.merp.employee.data.network.ServiceBuilder
import kotlinx.coroutines.launch
import kz.aura.merp.employee.data.model.*
import kz.aura.merp.employee.util.Link
import kz.aura.merp.employee.util.NetworkResult
import kz.aura.merp.employee.util.receiveError
import kz.aura.merp.employee.util.saveData
import java.lang.Exception

class FinanceViewModel (application: Application) : AndroidViewModel(application) {
    private val apiService = ServiceBuilder.buildService(FinanceApi::class.java, application, Link.FINANCE)

    val plansResponse: MutableLiveData<NetworkResult<ArrayList<Plan>>> = MutableLiveData()
    val updatedPlanResponse: MutableLiveData<NetworkResult<Plan>> = MutableLiveData()
    val businessProcessStatusesResponse: MutableLiveData<NetworkResult<ArrayList<BusinessProcessStatus>>> = MutableLiveData()
    val planResultsResponse: MutableLiveData<NetworkResult<ArrayList<PlanResult>>> = MutableLiveData()
    val paymentScheduleResponse: MutableLiveData<NetworkResult<ArrayList<PaymentSchedule>>> = MutableLiveData()
    val banksResponse: MutableLiveData<NetworkResult<ArrayList<Bank>>> = MutableLiveData()
    val paymentMethodsResponse: MutableLiveData<NetworkResult<ArrayList<PaymentMethod>>> = MutableLiveData()

    fun fetchPlans() = viewModelScope.launch {
        plansResponse.postValue(NetworkResult.Loading())
        try {
            val response = apiService.fetchPlans()

            if (response.isSuccessful) {
                plansResponse.postValue(NetworkResult.Success(response.body()!!.data))
            } else {
                plansResponse.postValue(NetworkResult.Error(receiveError(response.errorBody()!!), response.code()))
            }
        } catch (e: Exception) {
            plansResponse.postValue(NetworkResult.Error(e.message))
        }
    }

    fun updatePlanResult(contractId: Long, plan: ChangePlanResult) = viewModelScope.launch {
        updatedPlanResponse.postValue(NetworkResult.Loading())
        try {
            val response = apiService.updatePlanResult(contractId, plan)

            if (response.isSuccessful) {
                updatedPlanResponse.postValue(NetworkResult.Success(response.body()!!.data))
                saveData(response.body()!!.data, getApplication())
            } else {
                updatedPlanResponse.postValue(NetworkResult.Error(receiveError(response.errorBody()!!), response.code()))
            }
        } catch (e: Exception) {
            updatedPlanResponse.postValue(NetworkResult.Error(e.message))
        }
    }

    fun updateBusinessProcessStep(contractId: Long, businessProcess: ChangeBusinessProcess) = viewModelScope.launch {
        updatedPlanResponse.postValue(NetworkResult.Loading())
        try {
            val response = apiService.updateBusinessProcessStep(contractId, businessProcess)

            if (response.isSuccessful) {
                updatedPlanResponse.postValue(NetworkResult.Success(response.body()!!.data))
                saveData(response.body()!!.data, getApplication())
            } else {
                updatedPlanResponse.postValue(NetworkResult.Error(receiveError(response.errorBody()!!), response.code()))
            }
        } catch (e: Exception) {
            updatedPlanResponse.postValue(NetworkResult.Error(e.message))
        }
    }

    fun fetchPlanResults() = viewModelScope.launch {
        planResultsResponse.postValue(NetworkResult.Loading())
        try {
            val response = apiService.fetchPlanResults()

            if (response.isSuccessful) {
                planResultsResponse.postValue(NetworkResult.Success(response.body()!!.data))
            } else {
                planResultsResponse.postValue(NetworkResult.Error(receiveError(response.errorBody()!!), response.code()))
            }
        } catch (e: Exception) {
            planResultsResponse.postValue(NetworkResult.Error(e.message))
        }
    }

    fun changeData(plan: Plan): Boolean {
        return if (plansResponse.value?.data?.isNotEmpty() == true) {
            val foundData = plansResponse.value?.data!!.find { it.contractId == plan.contractId }
            val idx = plansResponse.value?.data!!.indexOf(foundData)
            plansResponse.value?.data!![idx] = plan
            true
        } else false
    }

    fun fetchBusinessProcessStatuses() = viewModelScope.launch {
        businessProcessStatusesResponse.postValue(NetworkResult.Loading())
        try {
            val response = apiService.fetchBusinessProcessStatuses()

            if (response.isSuccessful) {
                businessProcessStatusesResponse.postValue(NetworkResult.Success(response.body()!!.data))
            } else {
                businessProcessStatusesResponse.postValue(NetworkResult.Error(receiveError(response.errorBody()!!), response.code()))
            }
        } catch (e: Exception) {
            businessProcessStatusesResponse.postValue(NetworkResult.Error(e.message))
        }
    }

    fun fetchPaymentSchedule(contractId: Long) = viewModelScope.launch {
        paymentScheduleResponse.postValue(NetworkResult.Loading())
        try {
            val response = apiService.fetchPaymentSchedule(contractId)

            if (response.isSuccessful) {
                paymentScheduleResponse.postValue(NetworkResult.Success(response.body()!!.data))
            } else {
                paymentScheduleResponse.postValue(NetworkResult.Error(receiveError(response.errorBody()!!), response.code()))
            }
        } catch (e: Exception) {
            paymentScheduleResponse.postValue(NetworkResult.Error(e.message))
        }
    }

    fun fetchBanks() = viewModelScope.launch {
        banksResponse.postValue(NetworkResult.Loading())
        try {
            val response = apiService.fetchBanks()

            if (response.isSuccessful) {
                banksResponse.postValue(NetworkResult.Success(response.body()!!.data))
            } else {
                banksResponse.postValue(NetworkResult.Error(receiveError(response.errorBody()!!), response.code()))
            }
        } catch (e: Exception) {
            banksResponse.postValue(NetworkResult.Error(e.message))
        }
    }

    fun fetchPaymentMethods() = viewModelScope.launch {
        paymentMethodsResponse.postValue(NetworkResult.Loading())
        try {
            val response = apiService.fetchPaymentMethods()

            if (response.isSuccessful) {
                paymentMethodsResponse.postValue(NetworkResult.Success(response.body()!!.data))
            } else {
                paymentMethodsResponse.postValue(NetworkResult.Error(receiveError(response.errorBody()!!), response.code()))
            }
        } catch (e: Exception) {
            paymentMethodsResponse.postValue(NetworkResult.Error(e.message))
        }
    }
}