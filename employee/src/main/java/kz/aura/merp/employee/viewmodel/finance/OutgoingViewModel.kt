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
import kz.aura.merp.employee.data.finance.call.CallRepository
import kz.aura.merp.employee.data.finance.reference.ReferenceRepository
import kz.aura.merp.employee.model.AssignCall
import kz.aura.merp.employee.model.CallStatus
import kz.aura.merp.employee.model.ResponseHelper
import javax.inject.Inject

@HiltViewModel
class OutgoingViewModel @Inject constructor(
    private val callRepository: CallRepository,
    private val referenceRepository: ReferenceRepository,
    val preferences: AppPreferences
) : ViewModel() {

    private val scope: CoroutineScope = CoroutineScope(Job() + Dispatchers.IO)

    val callStatusesResponse = MutableLiveData<NetworkResult<ResponseHelper<List<CallStatus>>>>()
    val assignOutgoingCallResponse = MutableLiveData<NetworkResult<ResponseHelper<Nothing>>>()

    fun fetchCallStatuses() = scope.launch {
        callStatusesResponse.postValue(NetworkResult.Loading())
        val response = referenceRepository.fetchCallStatuses()
        callStatusesResponse.postValue(response)
    }

    fun assignOutgoingCall(assignCall: AssignCall, contractId: Long) = scope.launch {
        assignOutgoingCallResponse.postValue(NetworkResult.Loading())
        val response = callRepository.assignOutgoingCall(contractId, assignCall)
        assignOutgoingCallResponse.postValue(response)
    }
}