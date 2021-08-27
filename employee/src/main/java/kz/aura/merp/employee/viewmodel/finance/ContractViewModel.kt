package kz.aura.merp.employee.viewmodel.finance

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kz.aura.merp.employee.base.NetworkResult
import kz.aura.merp.employee.data.finance.monthPlan.MonthPlanRepository
import kz.aura.merp.employee.data.finance.reference.ReferenceRepository
import kz.aura.merp.employee.model.BusinessProcessStatus
import kz.aura.merp.employee.model.Plan
import kz.aura.merp.employee.model.ResponseHelper
import javax.inject.Inject

@HiltViewModel
class ContractViewModel @Inject constructor(
    private val monthPlanRepository: MonthPlanRepository,
    private val referenceRepository: ReferenceRepository
) : ViewModel() {

    private val scope: CoroutineScope = CoroutineScope(Job() + Dispatchers.IO)

    val planResponse = MutableLiveData<NetworkResult<ResponseHelper<Plan>>>()
    val businessProcessStatusesResponse = MutableLiveData<NetworkResult<ResponseHelper<List<BusinessProcessStatus>>>>()

    fun fetchPlan(contractId: Long) = scope.launch {
        val response = monthPlanRepository.fetchPlan(contractId)
        planResponse.postValue(response)
    }

    fun fetchBusinessProcessStatuses() = scope.launch {
        val response = referenceRepository.fetchBusinessProcessStatuses()
        businessProcessStatusesResponse.postValue(response)
    }
}