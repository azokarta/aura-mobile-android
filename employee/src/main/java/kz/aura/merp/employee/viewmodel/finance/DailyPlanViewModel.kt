package kz.aura.merp.employee.viewmodel.finance

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kz.aura.merp.employee.base.NetworkResult
import kz.aura.merp.employee.data.finance.dailyPlan.DailyPlanRepository
import kz.aura.merp.employee.data.finance.reference.ReferenceRepository
import kz.aura.merp.employee.model.BusinessProcessStatus
import kz.aura.merp.employee.model.ChangeBusinessProcess
import kz.aura.merp.employee.model.DailyPlan
import kz.aura.merp.employee.model.ResponseHelper
import javax.inject.Inject

@HiltViewModel
class DailyPlanViewModel @Inject constructor(
    private val dailyPlanRepository: DailyPlanRepository,
    private val referenceRepository: ReferenceRepository,
) : ViewModel() {

    private val scope: CoroutineScope = CoroutineScope(Job() + Dispatchers.IO)

    val dailyPlanResponse = MutableLiveData<NetworkResult<ResponseHelper<DailyPlan>>>()
    val businessProcessStatusesResponse = MutableLiveData<NetworkResult<ResponseHelper<List<BusinessProcessStatus>>>>()
    val updateBusinessProcessResponse = MutableLiveData<NetworkResult<ResponseHelper<Nothing>>>()

    fun fetchDailyPlan(dailyPlanId: Long) = scope.launch {
        dailyPlanResponse.postValue(NetworkResult.Loading())
        val response = dailyPlanRepository.fetchDailyPlan(dailyPlanId)
        dailyPlanResponse.postValue(response)
    }

    fun fetchBusinessProcessStatuses() = scope.launch {
        businessProcessStatusesResponse.postValue(NetworkResult.Loading())
        val response = referenceRepository.fetchBusinessProcessStatuses()
        businessProcessStatusesResponse.postValue(response)
    }

    fun updateBusinessProcess(contractId: Long, businessProcess: ChangeBusinessProcess) = scope.launch {
        updateBusinessProcessResponse.postValue(NetworkResult.Loading())
        val response = dailyPlanRepository.updateBusinessProcess(contractId, businessProcess)
        updateBusinessProcessResponse.postValue(response)
    }
}