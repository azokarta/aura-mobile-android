package kz.aura.merp.employee.viewmodel.finance

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kz.aura.merp.employee.base.AppPreferences
import kz.aura.merp.employee.base.NetworkResult
import kz.aura.merp.employee.data.finance.dailyPlan.DailyPlanRepository
import kz.aura.merp.employee.data.finance.reference.ReferenceRepository
import kz.aura.merp.employee.model.*
import javax.inject.Inject

@HiltViewModel
class ChangeResultViewModel @Inject constructor(
    private val dailyPlanRepository: DailyPlanRepository,
    private val referenceRepository: ReferenceRepository,
    val preferences: AppPreferences
) : ViewModel() {

    private val scope: CoroutineScope = CoroutineScope(Job() + Dispatchers.IO)

    val planResultsResponse = MutableLiveData<NetworkResult<ResponseHelper<List<PlanResult>>>>()
    val banksResponse = MutableLiveData<NetworkResult<ResponseHelper<List<Bank>>>>()
    val paymentMethodsResponse = MutableLiveData<NetworkResult<ResponseHelper<List<PaymentMethod>>>>()
    val changeResultResponse = MutableLiveData<NetworkResult<ResponseHelper<Nothing>>>()

    fun fetchPlanResults() = scope.launch {
        planResultsResponse.postValue(NetworkResult.Loading())
        val response = referenceRepository.fetchPlanResults()
        planResultsResponse.postValue(response)
    }

    fun fetchBanks() = scope.launch {
        banksResponse.postValue(NetworkResult.Loading())
        val response = referenceRepository.fetchBanks()
        banksResponse.postValue(response)
    }

    fun fetchPaymentMethods() = scope.launch {
        paymentMethodsResponse.postValue(NetworkResult.Loading())
        val response = referenceRepository.fetchPaymentMethods()
        paymentMethodsResponse.postValue(response)
    }

    fun changeResult(contractId: Long?, plan: ChangePlanResult) = scope.launch {
        changeResultResponse.postValue(NetworkResult.Loading())
        val response = dailyPlanRepository.changeResult(contractId, plan)
        changeResultResponse.postValue(response)
    }

}