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
import kz.aura.merp.employee.model.AssignCall
import kz.aura.merp.employee.model.ResponseHelper
import javax.inject.Inject

@HiltViewModel
class IncomingViewModel @Inject constructor(
    private val callRepository: CallRepository,
    val preferences: AppPreferences,
) : ViewModel() {

    private val scope: CoroutineScope = CoroutineScope(Job() + Dispatchers.IO)

    val assignIncomingCallResponse = MutableLiveData<NetworkResult<ResponseHelper<Nothing>>>()

    fun assignIncomingCall(assignCall: AssignCall, contractId: Long) = scope.launch {
        assignIncomingCallResponse.postValue(NetworkResult.Loading())
        val response = callRepository.assignIncomingCall(contractId, assignCall)
        assignIncomingCallResponse.postValue(response)
    }

}