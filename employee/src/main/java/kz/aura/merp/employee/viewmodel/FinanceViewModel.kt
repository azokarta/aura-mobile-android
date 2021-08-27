package kz.aura.merp.employee.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kz.aura.merp.employee.R
import kz.aura.merp.employee.base.NetworkResult
import kz.aura.merp.employee.data.finance.FinanceRepository
import kz.aura.merp.employee.model.*
import kz.aura.merp.employee.util.*
import org.joda.time.format.DateTimeFormat
import org.joda.time.format.DateTimeFormatter
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class FinanceViewModel @Inject constructor(
    private val financeRepository: FinanceRepository
    application: Application
) : AndroidViewModel(application) {

    private val scope: CoroutineScope = CoroutineScope(Job() + Dispatchers.IO)

    val plansResponse: MutableLiveData<NetworkResult<List<Plan>>> = MutableLiveData()
    val updatedPlanResponse: MutableLiveData<NetworkResult<Plan>> = MutableLiveData()
    val businessProcessStatusesResponse: MutableLiveData<NetworkResult<List<BusinessProcessStatus>>> = MutableLiveData()
    val planResultsResponse: MutableLiveData<NetworkResult<List<PlanResult>>> = MutableLiveData()
    val paymentScheduleResponse: MutableLiveData<NetworkResult<List<PaymentSchedule>>> = MutableLiveData()
    val banksResponse: MutableLiveData<NetworkResult<List<Bank>>> = MutableLiveData()
    val paymentMethodsResponse: MutableLiveData<NetworkResult<List<PaymentMethod>>> = MutableLiveData()
    val contributionsResponse: MutableLiveData<NetworkResult<List<Contribution>>> = MutableLiveData()


    val changeResultResponse: MutableLiveData<NetworkResult<Nothing>> = MutableLiveData()
    val callDirectionsResponse: MutableLiveData<NetworkResult<List<CallDirection>>> = MutableLiveData()
    val callStatusesResponse: MutableLiveData<NetworkResult<List<CallStatus>>> = MutableLiveData()
    val assignCallResponse: MutableLiveData<NetworkResult<Nothing>> = MutableLiveData()
    val scheduledCallsResponse: MutableLiveData<NetworkResult<List<ScheduledCall>>> = MutableLiveData()
    val callsResponse: MutableLiveData<NetworkResult<List<Call>>> = MutableLiveData()
    val salary: MutableLiveData<Salary> = MutableLiveData()
    val countryCode: MutableLiveData<CountryCode> = MutableLiveData()
    val dailyPlansResponse: MutableLiveData<NetworkResult<List<DailyPlan>>> = MutableLiveData()
    val dailyPlanResponse: MutableLiveData<NetworkResult<DailyPlan>> = MutableLiveData()
    val createDailyPlanResponse: MutableLiveData<NetworkResult<Nothing>> = MutableLiveData()
    val changeBusinessProcessStatusResponse: MutableLiveData<NetworkResult<Nothing>> = MutableLiveData()
    val planResponse: MutableLiveData<NetworkResult<Plan>> = MutableLiveData()
    val assignScheduledCallResponse: MutableLiveData<NetworkResult<Nothing>> = MutableLiveData()
    val messagesResponse: MutableLiveData<NetworkResult<List<Message>>> = MutableLiveData()

    private suspend fun getPlansByPage(page: Int): List<Plan> {
        val response = financeRepository.remote.fetchPlans()
        val chunked = response.body()!!.data.chunked(20)[page]
        return chunked
    }

    fun getCountryCode() = scope.launch {
        dataStoreRepository.countryCodeFlow.collect { value ->
            countryCode.postValue(value)
        }
    }



    fun fetchPlans() = scope.launch {
        plansResponse.postValue(NetworkResult.Loading())
        if (isInternetAvailable(getApplication())) {
            try {
                val response = financeRepository.remote.fetchPlans()

                if (response.isSuccessful) {
                    plansResponse.postValue(NetworkResult.Success(response.body()!!.data))
                } else {
                    plansResponse.postValue(handleError(response))
                }
            } catch (e: Exception) {
                plansResponse.postValue(NetworkResult.Error(e.message))
            }
        } else {
            plansResponse.postValue(internetIsNotConnected())
        }
    }

    fun createDailyPlan(contractId: Long, planTime: String) = scope.launch {
        createDailyPlanResponse.postValue(NetworkResult.Loading())
        if (isInternetAvailable(getApplication())) {
            try {
                val response = financeRepository.remote.createDailyPlan(contractId, planTime)

                if (response.isSuccessful) {
                    createDailyPlanResponse.postValue(NetworkResult.Success())
                } else {
                    createDailyPlanResponse.postValue(handleError(response))
                }
            } catch (e: Exception) {
                createDailyPlanResponse.postValue(NetworkResult.Error(e.message))
            }
        } else {
            createDailyPlanResponse.postValue(internetIsNotConnected())
        }
    }

    fun updateBusinessProcess(contractId: Long, businessProcess: ChangeBusinessProcess) =
        scope.launch {
            changeBusinessProcessStatusResponse.postValue(NetworkResult.Loading())
            if (isInternetAvailable(getApplication())) {
                try {
                    val response =
                        financeRepository.remote.updateBusinessProcess(contractId, businessProcess)

                    if (response.isSuccessful) {
                        changeBusinessProcessStatusResponse.postValue(NetworkResult.Success())
                    } else {
                        changeBusinessProcessStatusResponse.postValue(handleError(response))
                    }
                } catch (e: Exception) {
                    changeBusinessProcessStatusResponse.postValue(NetworkResult.Error(e.message))
                }
            } else {
                changeBusinessProcessStatusResponse.postValue(internetIsNotConnected())
            }
        }

    fun fetchDailyPlans() = scope.launch {
        dailyPlansResponse.postValue(NetworkResult.Loading())
        if (isInternetAvailable(getApplication())) {
            try {
                val response = financeRepository.remote.fetchDailyPlans()

                if (response.isSuccessful) {
                    dailyPlansResponse.postValue(NetworkResult.Success(response.body()!!.data))
                } else {
                    dailyPlansResponse.postValue(handleError(response))
                }
            } catch (e: Exception) {
                dailyPlansResponse.postValue(NetworkResult.Error(e.message))
            }

        } else {
            dailyPlansResponse.postValue(internetIsNotConnected())
        }
    }

    fun fetchDailyPlan(dailyPlanId: Long) = scope.launch {
        dailyPlanResponse.postValue(NetworkResult.Loading())
        if (isInternetAvailable(getApplication())) {
            try {
                val response = financeRepository.remote.fetchDailyPlan(dailyPlanId)

                if (response.isSuccessful) {
                    dailyPlanResponse.postValue(NetworkResult.Success(response.body()!!.data))
                } else {
                    dailyPlanResponse.postValue(handleError(response))
                }
            } catch (e: Exception) {
                dailyPlanResponse.postValue(NetworkResult.Error(e.message))
            }

        } else {
            dailyPlanResponse.postValue(internetIsNotConnected())
        }
    }

    fun fetchPaymentSchedule(contractId: Long) = scope.launch {
        paymentScheduleResponse.postValue(NetworkResult.Loading())
        if (isInternetAvailable(getApplication())) {
            try {
                val response = financeRepository.remote.fetchPaymentSchedule(contractId)

                if (response.isSuccessful) {
                    paymentScheduleResponse.postValue(NetworkResult.Success(response.body()!!.data))
                } else {
                    paymentScheduleResponse.postValue(handleError(response))
                }
            } catch (e: Exception) {
                paymentScheduleResponse.postValue(NetworkResult.Error(e.message))
            }

        } else {
            paymentScheduleResponse.postValue(internetIsNotConnected())
        }
    }

    fun changeResult(contractId: Long?, plan: ChangePlanResult) = scope.launch {
        changeResultResponse.postValue(NetworkResult.Loading())
        if (isInternetAvailable(getApplication())) {
            try {
                val response = financeRepository.remote.changeResult(contractId, plan)

                if (response.isSuccessful) {
                    changeResultResponse.postValue(NetworkResult.Success())
                } else {
                    changeResultResponse.postValue(handleError(response))
                }
            } catch (e: Exception) {
                changeResultResponse.postValue(NetworkResult.Error(e.message))
            }

        } else {
            changeResultResponse.postValue(internetIsNotConnected())
        }
    }

    fun fetchBanks() = scope.launch {
        banksResponse.postValue(NetworkResult.Loading())
        if (isInternetAvailable(getApplication())) {
            try {
                val response = financeRepository.remote.fetchBanks()

                if (response.isSuccessful) {
                    banksResponse.postValue(NetworkResult.Success(response.body()!!.data))
                } else {
                    banksResponse.postValue(handleError(response))
                }
            } catch (e: Exception) {
                banksResponse.postValue(NetworkResult.Error(e.message))
            }

        } else {
            banksResponse.postValue(internetIsNotConnected())
        }
    }

    fun fetchPaymentMethods() = scope.launch {
        paymentMethodsResponse.postValue(NetworkResult.Loading())
        if (isInternetAvailable(getApplication())) {
            try {
                val response = financeRepository.remote.fetchPaymentMethods()

                if (response.isSuccessful) {
                    paymentMethodsResponse.postValue(NetworkResult.Success(response.body()!!.data))
                } else {
                    paymentMethodsResponse.postValue(handleError(response))
                }
            } catch (e: Exception) {
                paymentMethodsResponse.postValue(NetworkResult.Error(e.message))
            }

        } else {
            paymentMethodsResponse.postValue(internetIsNotConnected())
        }
    }

    fun fetchPlanResults() = scope.launch {
        planResultsResponse.postValue(NetworkResult.Loading())
        if (isInternetAvailable(getApplication())) {
            try {
                val response = financeRepository.remote.fetchPlanResults()

                if (response.isSuccessful) {
                    planResultsResponse.postValue(NetworkResult.Success(response.body()!!.data))
                } else {
                    planResultsResponse.postValue(handleError(response))
                }
            } catch (e: Exception) {
                planResultsResponse.postValue(NetworkResult.Error(e.message))
            }

        } else {
            planResultsResponse.postValue(internetIsNotConnected())
        }
    }

    fun fetchContributionsByContractId(contractId: Long) = scope.launch {
        contributionsResponse.postValue(NetworkResult.Loading())
        if (isInternetAvailable(getApplication())) {
            try {
                val response = financeRepository.remote.fetchContributionsByContractId(contractId)

                if (response.isSuccessful) {
                    contributionsResponse.postValue(NetworkResult.Success(response.body()!!.data))
                } else {
                    contributionsResponse.postValue(handleError(response))
                }
            } catch (e: Exception) {
                contributionsResponse.postValue(NetworkResult.Error(e.message))
            }

        } else {
            contributionsResponse.postValue(internetIsNotConnected())
        }
    }

    fun fetchCallDirections() = scope.launch {
        callDirectionsResponse.postValue(NetworkResult.Loading())
        if (isInternetAvailable(getApplication())) {
            try {
                val response = financeRepository.remote.fetchCallDirections()

                if (response.isSuccessful) {
                    callDirectionsResponse.postValue(NetworkResult.Success(response.body()!!.data))
                } else {
                    callDirectionsResponse.postValue(handleError(response))
                }
            } catch (e: Exception) {
                callDirectionsResponse.postValue(NetworkResult.Error(e.message))
            }

        } else {
            callDirectionsResponse.postValue(internetIsNotConnected())
        }
    }

    fun fetchCallStatuses() = scope.launch {
        callStatusesResponse.postValue(NetworkResult.Loading())
        if (isInternetAvailable(getApplication())) {
            try {
                val response = financeRepository.remote.fetchCallStatuses()

                if (response.isSuccessful) {
                    callStatusesResponse.postValue(NetworkResult.Success(response.body()!!.data))
                } else {
                    callStatusesResponse.postValue(handleError(response))
                }
            } catch (e: Exception) {
                callStatusesResponse.postValue(NetworkResult.Error(e.message))
            }

        } else {
            callStatusesResponse.postValue(internetIsNotConnected())
        }
    }



    fun fetchLastMonthScheduledCalls() = scope.launch {
        scheduledCallsResponse.postValue(NetworkResult.Loading())
        if (isInternetAvailable(getApplication())) {
            try {
                val response = financeRepository.remote.fetchLastMonthScheduledCalls()

                if (response.isSuccessful) {
                    scheduledCallsResponse.postValue(NetworkResult.Success(response.body()!!.data))
                } else {
                    scheduledCallsResponse.postValue(handleError(response))
                }
            } catch (e: Exception) {
                scheduledCallsResponse.postValue(NetworkResult.Error(e.message))
            }

        } else {
            scheduledCallsResponse.postValue(internetIsNotConnected())
        }
    }

    fun fetchScheduledCallsHistory(contractId: Long) = scope.launch {
        scheduledCallsResponse.postValue(NetworkResult.Loading())
        if (isInternetAvailable(getApplication())) {
            try {
                val response = financeRepository.remote.fetchScheduledCallsHistory(contractId)

                if (response.isSuccessful) {
                    scheduledCallsResponse.postValue(NetworkResult.Success(response.body()!!.data))
                } else {
                    scheduledCallsResponse.postValue(handleError(response))
                }
            } catch (e: Exception) {
                scheduledCallsResponse.postValue(NetworkResult.Error(e.message))
            }

        } else {
            scheduledCallsResponse.postValue(internetIsNotConnected())
        }
    }

    fun assignScheduledCall(contractId: Long, scheduledCall: AssignScheduledCallCommand) = scope.launch {
        assignScheduledCallResponse.postValue(NetworkResult.Loading())
        if (isInternetAvailable(getApplication())) {
            try {
                val response = financeRepository.remote.assignScheduledCall(contractId, scheduledCall)

                if (response.isSuccessful) {
                    assignScheduledCallResponse.postValue(NetworkResult.Success())
                } else {
                    assignScheduledCallResponse.postValue(handleError(response))
                }
            } catch (e: Exception) {
                assignScheduledCallResponse.postValue(NetworkResult.Error(e.message))
            }

        } else {
            assignScheduledCallResponse.postValue(internetIsNotConnected())
        }
    }

    fun fetchMessages(staffId: Long) {
        if (isInternetAvailable(getApplication())) {
            val db = Firebase.firestore
            db.collection("users").document(staffId.toString()).collection("messages")
                .addSnapshotListener { documents, e ->
                    if (e != null) {
                        Log.w("Firestore", "Listen failed.", e)
                        return@addSnapshotListener
                    }

                    if (documents != null) {
                        val messages = ArrayList<Message>()
                        for (doc in documents) {
                            val message = Message(
                                doc.getLong("fromId"),
                                doc.getString("createdAt"),
                                doc.getString("from"),
                                doc.getString("message")
                            )
                            messages.add(message)
                        }

                        // Sort by date
                        val dtf: DateTimeFormatter = DateTimeFormat.forPattern("dd.MM.yyyy HH:mm")
                        val sortedMessages = messages.sortedBy { dtf.parseLocalDate(it.createdAt) }

                        messagesResponse.postValue(NetworkResult.Success(sortedMessages))
                    }
                }
        } else {
            messagesResponse.postValue(internetIsNotConnected())
        }
    }

    fun getSalary() = scope.launch {
        dataStoreRepository.salaryFlow.collect { value ->
            salary.postValue(value)
        }
    }

    private fun <T> internetIsNotConnected(): NetworkResult<T> = NetworkResult.Error(
        receiveMessage(R.string.network_disconnected),
        ErrorStatus.INTERNET_IS_NOT_AVAILABLE
    )

    private fun <T> handleError(res: Response<ResponseHelper<T>>): NetworkResult<T> {
        val message = receiveErrorMessage(res.errorBody()!!)
        return when (res.code()) {
            401 -> NetworkResult.Error(message, ErrorStatus.UNAUTHORIZED)
            400 -> NetworkResult.Error(message, ErrorStatus.BAD_REQUEST)
            404 -> NetworkResult.Error(message, ErrorStatus.NOT_FOUND)
            500 -> NetworkResult.Error(message, ErrorStatus.INTERNAL_SERVER_ERROR)
            else -> NetworkResult.Error(message, null)
        }
    }
}