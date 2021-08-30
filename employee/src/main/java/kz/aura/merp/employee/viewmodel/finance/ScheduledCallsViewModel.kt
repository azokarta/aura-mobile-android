package kz.aura.merp.employee.viewmodel.finance

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kz.aura.merp.employee.base.NetworkResult
import kz.aura.merp.employee.data.finance.scheduledCall.ScheduledCallRepository
import kz.aura.merp.employee.model.ResponseHelper
import kz.aura.merp.employee.model.ScheduledCall
import javax.inject.Inject

@HiltViewModel
class ScheduledCallsViewModel @Inject constructor(
    private val scheduledCallRepository: ScheduledCallRepository
) : ViewModel() {

    private val scope: CoroutineScope = CoroutineScope(Job() + Dispatchers.IO)

    val lastMonthScheduledCallsResponse = MutableLiveData<NetworkResult<ResponseHelper<List<ScheduledCall>>>>()

    fun fetchLastMonthScheduledCalls() = scope.launch {
        lastMonthScheduledCallsResponse.postValue(NetworkResult.Loading())
        val response = scheduledCallRepository.fetchLastMonthScheduledCalls()
        lastMonthScheduledCallsResponse.postValue(response)
    }

}