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
import kz.aura.merp.employee.model.AssignCall
import kz.aura.merp.employee.model.Call
import kz.aura.merp.employee.model.ResponseHelper
import javax.inject.Inject

@HiltViewModel
class CallsViewModel @Inject constructor(
    private val callRepository: CallRepository
) : ViewModel() {

    private val scope: CoroutineScope = CoroutineScope(Job() + Dispatchers.IO)

    val lastMonthCallsResponse = MutableLiveData<NetworkResult<ResponseHelper<List<Call>>>>()
    val lastMonthCallsByContractIdResponse = MutableLiveData<NetworkResult<ResponseHelper<List<Call>>>>()
    val callsHistoryResponse = MutableLiveData<NetworkResult<ResponseHelper<List<Call>>>>()
    val assignIncomingCallResponse = MutableLiveData<NetworkResult<ResponseHelper<Nothing>>>()
    val assignOutgoingCallResponse = MutableLiveData<NetworkResult<ResponseHelper<Nothing>>>()

    fun fetchLastMonthCalls() = scope.launch {
        val response = callRepository.fetchLastMonthCalls()
        lastMonthCallsResponse.postValue(response)
    }

    fun fetchCallsHistory(contractId: Long) = scope.launch {
        val response = callRepository.fetchCallsHistory(contractId)
        callsHistoryResponse.postValue(response)
    }

    fun fetchLastMonthCallsByContractId(contractId: Long) = scope.launch {
        val response = callRepository.fetchLastMonthCallsByContractId(contractId)
        lastMonthCallsByContractIdResponse.postValue(response)
    }

    fun assignIncomingCall(assignCall: AssignCall, contractId: Long) = scope.launch {
        val response = callRepository.assignIncomingCall(contractId, assignCall)
        assignIncomingCallResponse.postValue(response)
    }

    fun assignOutgoingCall(assignCall: AssignCall, contractId: Long) = scope.launch {
        val response = callRepository.assignOutgoingCall(contractId, assignCall)
        assignOutgoingCallResponse.postValue(response)
    }

}