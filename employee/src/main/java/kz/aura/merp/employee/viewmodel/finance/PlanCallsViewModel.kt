package kz.aura.merp.employee.viewmodel.finance

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kz.aura.merp.employee.base.NetworkResult
import kz.aura.merp.employee.data.finance.call.CallRepository
import kz.aura.merp.employee.model.Call
import kz.aura.merp.employee.model.ResponseHelper
import javax.inject.Inject

@HiltViewModel
class PlanCallsViewModel @Inject constructor(
    private val callRepository: CallRepository
) : ViewModel() {

    private val scope: CoroutineScope = CoroutineScope(Job() + Dispatchers.IO)

    val lastMonthCallsByContractIdResponse = MutableLiveData<NetworkResult<ResponseHelper<List<Call>>>>()
    val callHistoryResponse = MutableLiveData<NetworkResult<ResponseHelper<List<Call>>>>()

    fun fetchCallHistory(contractId: Long) = scope.launch {
        callHistoryResponse.postValue(NetworkResult.Loading())
        val response = callRepository.fetchCallHistory(contractId)
        callHistoryResponse.postValue(response)
    }

    fun fetchLastMonthCallsByContractId(contractId: Long) = scope.launch {
        lastMonthCallsByContractIdResponse.postValue(NetworkResult.Loading())
        val response = callRepository.fetchLastMonthCallsByContractId(contractId)
        lastMonthCallsByContractIdResponse.postValue(response)
    }
}