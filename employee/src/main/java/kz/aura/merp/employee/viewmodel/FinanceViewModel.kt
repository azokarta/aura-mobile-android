package kz.aura.merp.employee.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect
import kz.aura.merp.employee.data.DataStoreRepository
import kz.aura.merp.employee.data.repository.financeRepository.FinanceRepository
import kz.aura.merp.employee.model.*
import kz.aura.merp.employee.util.CountryCode
import kz.aura.merp.employee.util.NetworkResult
import kz.aura.merp.employee.util.receiveErrorMessage
import kz.aura.merp.employee.util.saveData
import java.lang.Exception
import javax.inject.Inject

@HiltViewModel
class FinanceViewModel @Inject constructor(
    private val financeRepository: FinanceRepository,
    private val dataStoreRepository: DataStoreRepository,
    application: Application
) : AndroidViewModel(application) {

    private val scope: CoroutineScope = CoroutineScope(Job() + Dispatchers.IO)

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
    val contributionsResponse: MutableLiveData<NetworkResult<ArrayList<Contribution>>> =
        MutableLiveData()
    val callsHistoryResponse: MutableLiveData<NetworkResult<ArrayList<Call>>> = MutableLiveData()
    val assignCollectMoneyResponse: MutableLiveData<NetworkResult<ArrayList<Contribution>>> = MutableLiveData()
    val callDirectionsResponse: MutableLiveData<NetworkResult<ArrayList<CallDirection>>> = MutableLiveData()
    val callStatusesResponse: MutableLiveData<NetworkResult<ArrayList<CallStatus>>> = MutableLiveData()
    val assignCallResponse: MutableLiveData<NetworkResult<ArrayList<Call>>> = MutableLiveData()
    val scheduledCallsResponse: MutableLiveData<NetworkResult<ArrayList<ScheduledCall>>> = MutableLiveData()
    val callsResponse: MutableLiveData<NetworkResult<ArrayList<Call>>> = MutableLiveData()
    val staffUsername: MutableLiveData<String> = MutableLiveData()
    val countryCode: MutableLiveData<CountryCode> = MutableLiveData()

    fun getCountryCode() = scope.launch {
        dataStoreRepository.countryCodeFlow.collect { value ->
            countryCode.postValue(value)
        }
    }

    fun fetchPlans() = scope.launch {
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
        scope.launch {
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

    fun fetchBusinessProcessStatuses() = scope.launch {
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

    fun fetchPaymentSchedule(contractId: Long) = scope.launch {
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

    fun fetchContributions() = scope.launch {
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

    fun fetchCalls() = scope.launch {
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

    fun fetchCallHistory(contractId: Long) = scope.launch {
        callsHistoryResponse.postValue(NetworkResult.Loading())
        try {
            val response = financeRepository.remote.fetchCallHistory(contractId)

            if (response.isSuccessful) {
                callsHistoryResponse.postValue(NetworkResult.Success(response.body()!!.data))
            } else {
                callsHistoryResponse.postValue(
                    NetworkResult.Error(
                        receiveErrorMessage(response.errorBody()!!),
                        response.code()
                    )
                )
            }
        } catch (e: Exception) {
            callsHistoryResponse.postValue(NetworkResult.Error(e.message))
        }
    }

    fun fetchCallsForMonth(contractId: Long) = scope.launch {
        callsResponse.postValue(NetworkResult.Loading())
        try {
            val response = financeRepository.remote.fetchCallsForMonth(contractId)

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

    fun assignCollectMoney(contractId: Long?, plan: ChangePlanResult) = scope.launch {
        assignCollectMoneyResponse.postValue(NetworkResult.Loading())
        try {
            val response = financeRepository.remote.assignCollectMoney(contractId, plan)

            if (response.isSuccessful) {
                assignCollectMoneyResponse.postValue(NetworkResult.Success(response.body()!!.data))
            } else {
                assignCollectMoneyResponse.postValue(
                    NetworkResult.Error(
                        receiveErrorMessage(response.errorBody()!!),
                        response.code()
                    )
                )
            }
        } catch (e: Exception) {
            assignCollectMoneyResponse.postValue(NetworkResult.Error(e.message))
        }
    }

    fun fetchBanks() = scope.launch {
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

    fun fetchPaymentMethods() = scope.launch {
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

    fun fetchPlanResults() = scope.launch {
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

    fun fetchPlanContributions(contractId: Long) = scope.launch {
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

    fun fetchCallDirections() = scope.launch {
        callDirectionsResponse.postValue(NetworkResult.Loading())
        try {
            val response = financeRepository.remote.fetchCallDirections()

            if (response.isSuccessful) {
                callDirectionsResponse.postValue(NetworkResult.Success(response.body()!!.data))
            } else {
                callDirectionsResponse.postValue(
                    NetworkResult.Error(
                        receiveErrorMessage(response.errorBody()!!),
                        response.code()
                    )
                )
            }
        } catch (e: Exception) {
            callDirectionsResponse.postValue(NetworkResult.Error(e.message))
        }
    }

    fun fetchCallStatuses() = scope.launch {
        callStatusesResponse.postValue(NetworkResult.Loading())
        try {
            val response = financeRepository.remote.fetchCallStatuses()

            if (response.isSuccessful) {
                callStatusesResponse.postValue(NetworkResult.Success(response.body()!!.data))
            } else {
                callStatusesResponse.postValue(
                    NetworkResult.Error(
                        receiveErrorMessage(response.errorBody()!!),
                        response.code()
                    )
                )
            }
        } catch (e: Exception) {
            callStatusesResponse.postValue(NetworkResult.Error(e.message))
        }
    }

    fun assignCall(assignCall: AssignCall, contractId: Long) = scope.launch {
        assignCallResponse.postValue(NetworkResult.Loading())
        try {
            val response = financeRepository.remote.assignCall(assignCall, contractId)

            if (response.isSuccessful) {
                assignCallResponse.postValue(NetworkResult.Success(response.body()!!.data))
            } else {
                assignCallResponse.postValue(
                    NetworkResult.Error(
                        receiveErrorMessage(response.errorBody()!!),
                        response.code()
                    )
                )
            }
        } catch (e: Exception) {
            assignCallResponse.postValue(NetworkResult.Error(e.message))
        }
    }

    fun fetchScheduledCalls() = scope.launch {
        scheduledCallsResponse.postValue(NetworkResult.Loading())
        try {
            val response = financeRepository.remote.fetchScheduledCalls()

            if (response.isSuccessful) {
                scheduledCallsResponse.postValue(NetworkResult.Success(response.body()!!.data))
            } else {
                scheduledCallsResponse.postValue(
                    NetworkResult.Error(
                        receiveErrorMessage(response.errorBody()!!),
                        response.code()
                    )
                )
            }
        } catch (e: Exception) {
            scheduledCallsResponse.postValue(NetworkResult.Error(e.message))
        }
    }

    fun fetchScheduledCallsHistory(contractId: Long) = scope.launch {
        scheduledCallsResponse.postValue(NetworkResult.Loading())
        try {
            val response = financeRepository.remote.fetchScheduledCallsHistory(contractId)

            if (response.isSuccessful) {
                scheduledCallsResponse.postValue(NetworkResult.Success(response.body()!!.data))
            } else {
                scheduledCallsResponse.postValue(
                    NetworkResult.Error(
                        receiveErrorMessage(response.errorBody()!!),
                        response.code()
                    )
                )
            }
        } catch (e: Exception) {
            scheduledCallsResponse.postValue(NetworkResult.Error(e.message))
        }
    }


    fun getStaffUsername() = scope.launch {
        dataStoreRepository.salaryFlow.collect { value ->
            staffUsername.postValue(value.username!!)
        }
    }
}