package kz.aura.merp.employee.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import kz.aura.merp.employee.data.repository.financeRepository.FinanceRepository
import kz.aura.merp.employee.model.*
import kz.aura.merp.employee.util.NetworkResult
import kz.aura.merp.employee.util.receiveErrorMessage
import kz.aura.merp.employee.util.saveData
import java.lang.Exception
import javax.inject.Inject

@HiltViewModel
class FinanceViewModel @Inject constructor(
    private val financeRepository: FinanceRepository,
    application: Application
) : AndroidViewModel(application) {

    val plansResponse: MutableLiveData<NetworkResult<ArrayList<Plan>>> = MutableLiveData()
    val updatedPlanResponse: MutableLiveData<NetworkResult<Plan>> = MutableLiveData()
    val businessProcessStatusesResponse: MutableLiveData<NetworkResult<ArrayList<BusinessProcessStatus>>> =
        MutableLiveData()
    val planResultsResponse: MutableLiveData<NetworkResult<ArrayList<PlanResult>>> =
        MutableLiveData()
    val paymentScheduleResponse: MutableLiveData<NetworkResult<ArrayList<PaymentSchedule>>> =
        MutableLiveData()
    val banksResponse: MutableLiveData<NetworkResult<ArrayList<Bank>>> = MutableLiveData()
    val paymentMethodsResponse: MutableLiveData<NetworkResult<ArrayList<PaymentMethod>>> =
        MutableLiveData()
    val planHistoryResponse: MutableLiveData<NetworkResult<ArrayList<PlanHistoryItem>>> =
        MutableLiveData()
    val contributionsResponse: MutableLiveData<NetworkResult<ArrayList<Contribution>>> =
        MutableLiveData()
    val callsResponse: MutableLiveData<NetworkResult<ArrayList<Call>>> = MutableLiveData()

    fun fetchPlans() = viewModelScope.launch {
        plansResponse.postValue(NetworkResult.Loading())
        try {
            val response = financeRepository.remote.fetchPlans()

            if (response.isSuccessful) {
                plansResponse.postValue(NetworkResult.Success(response.body()!!.data))
            } else {
                plansResponse.postValue(
                    NetworkResult.Error(
                        receiveErrorMessage(response.errorBody()!!),
                        response.code()
                    )
                )
            }
        } catch (e: Exception) {
            plansResponse.postValue(NetworkResult.Error(e.message))
        }
    }

    fun updateBusinessProcessStep(contractId: Long, businessProcess: ChangeBusinessProcess) =
        viewModelScope.launch {
            updatedPlanResponse.postValue(NetworkResult.Loading())
            try {
                val response =
                    financeRepository.remote.updateBusinessProcessStep(contractId, businessProcess)

                if (response.isSuccessful) {
                    updatedPlanResponse.postValue(NetworkResult.Success(response.body()!!.data))
                    saveData(response.body()!!.data, getApplication())
                } else {
                    updatedPlanResponse.postValue(
                        NetworkResult.Error(
                            receiveErrorMessage(response.errorBody()!!),
                            response.code()
                        )
                    )
                }
            } catch (e: Exception) {
                updatedPlanResponse.postValue(NetworkResult.Error(e.message))
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
            val response = financeRepository.remote.fetchBusinessProcessStatuses()

            if (response.isSuccessful) {
                businessProcessStatusesResponse.postValue(NetworkResult.Success(response.body()!!.data))
            } else {
                businessProcessStatusesResponse.postValue(
                    NetworkResult.Error(
                        receiveErrorMessage(
                            response.errorBody()!!
                        ), response.code()
                    )
                )
            }
        } catch (e: Exception) {
            businessProcessStatusesResponse.postValue(NetworkResult.Error(e.message))
        }
    }

    fun fetchPaymentSchedule(contractId: Long) = viewModelScope.launch {
        paymentScheduleResponse.postValue(NetworkResult.Loading())
        try {
            val response = financeRepository.remote.fetchPaymentSchedule(contractId)

            if (response.isSuccessful) {
                paymentScheduleResponse.postValue(NetworkResult.Success(response.body()!!.data))
            } else {
                paymentScheduleResponse.postValue(
                    NetworkResult.Error(
                        receiveErrorMessage(response.errorBody()!!),
                        response.code()
                    )
                )
            }
        } catch (e: Exception) {
            paymentScheduleResponse.postValue(NetworkResult.Error(e.message))
        }
    }

    fun fetchContributions() = viewModelScope.launch {
        contributionsResponse.postValue(NetworkResult.Loading())
        try {
            val response = financeRepository.remote.fetchContributions()

            if (response.isSuccessful) {
                contributionsResponse.postValue(NetworkResult.Success(response.body()!!.data))
            } else {
                contributionsResponse.postValue(
                    NetworkResult.Error(
                        receiveErrorMessage(response.errorBody()!!),
                        response.code()
                    )
                )
            }
        } catch (e: Exception) {
            contributionsResponse.postValue(NetworkResult.Error(e.message))
        }
    }

    fun fetchCalls() = viewModelScope.launch {
        callsResponse.postValue(NetworkResult.Loading())
        try {
            val response = financeRepository.remote.fetchCalls()

            if (response.isSuccessful) {
                callsResponse.postValue(NetworkResult.Success(response.body()!!.data))
            } else {
                callsResponse.postValue(
                    NetworkResult.Error(
                        receiveErrorMessage(response.errorBody()!!),
                        response.code()
                    )
                )
            }
        } catch (e: Exception) {
            callsResponse.postValue(NetworkResult.Error(e.message))
        }
    }

    fun fetchCallHistory() = viewModelScope.launch {
        callsResponse.postValue(NetworkResult.Loading())
        try {
            val response = financeRepository.remote.fetchCallHistory()

            if (response.isSuccessful) {
                callsResponse.postValue(NetworkResult.Success(response.body()!!.data))
            } else {
                callsResponse.postValue(
                    NetworkResult.Error(
                        receiveErrorMessage(response.errorBody()!!),
                        response.code()
                    )
                )
            }
        } catch (e: Exception) {
            callsResponse.postValue(NetworkResult.Error(e.message))
        }
    }

    fun assignCollectMoney(contractId: Long?, plan: ChangePlanResult) = viewModelScope.launch {
        updatedPlanResponse.postValue(NetworkResult.Loading())
        println(plan)
        try {
            val response = financeRepository.remote.assignCollectMoney(contractId, plan)

            if (response.isSuccessful) {
                println(response.body())
            } else {
                updatedPlanResponse.postValue(
                    NetworkResult.Error(
                        receiveErrorMessage(response.errorBody()!!),
                        response.code()
                    )
                )
            }
        } catch (e: Exception) {
            updatedPlanResponse.postValue(NetworkResult.Error(e.message))
        }
    }

    fun fetchBanks() = viewModelScope.launch {
        banksResponse.postValue(NetworkResult.Loading())
        try {
            val response = financeRepository.remote.fetchBanks()

            if (response.isSuccessful) {
                banksResponse.postValue(NetworkResult.Success(response.body()!!.data))
            } else {
                banksResponse.postValue(
                    NetworkResult.Error(
                        receiveErrorMessage(response.errorBody()!!),
                        response.code()
                    )
                )
            }
        } catch (e: Exception) {
            banksResponse.postValue(NetworkResult.Error(e.message))
        }
    }

    fun fetchPaymentMethods() = viewModelScope.launch {
        paymentMethodsResponse.postValue(NetworkResult.Loading())
        try {
            val response = financeRepository.remote.fetchPaymentMethods()

            if (response.isSuccessful) {
                paymentMethodsResponse.postValue(NetworkResult.Success(response.body()!!.data))
            } else {
                paymentMethodsResponse.postValue(
                    NetworkResult.Error(
                        receiveErrorMessage(response.errorBody()!!),
                        response.code()
                    )
                )
            }
        } catch (e: Exception) {
            paymentMethodsResponse.postValue(NetworkResult.Error(e.message))
        }
    }

    fun fetchPlanResults() = viewModelScope.launch {
        planResultsResponse.postValue(NetworkResult.Loading())
        try {
            val response = financeRepository.remote.fetchPlanResults()

            if (response.isSuccessful) {
                planResultsResponse.postValue(NetworkResult.Success(response.body()!!.data))
            } else {
                planResultsResponse.postValue(NetworkResult.Error(receiveErrorMessage(response.errorBody()!!), response.code()))
            }
        } catch (e: Exception) {
            planResultsResponse.postValue(NetworkResult.Error(e.message))
        }
    }

    fun fetchPlanContributions(contractId: Long) = viewModelScope.launch {
        contributionsResponse.postValue(NetworkResult.Loading())
        try {
            val response = financeRepository.remote.fetchPlanContributions(contractId)

            if (response.isSuccessful) {
                contributionsResponse.postValue(NetworkResult.Success(response.body()!!.data))
            } else {
                contributionsResponse.postValue(
                    NetworkResult.Error(
                        receiveErrorMessage(response.errorBody()!!),
                        response.code()
                    )
                )
            }
        } catch (e: Exception) {
            contributionsResponse.postValue(NetworkResult.Error(e.message))
        }
    }
}