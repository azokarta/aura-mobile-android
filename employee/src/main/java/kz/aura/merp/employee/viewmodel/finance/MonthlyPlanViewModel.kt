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
import kz.aura.merp.employee.model.Plan
import kz.aura.merp.employee.model.ResponseHelper
import javax.inject.Inject

@HiltViewModel
class MonthlyPlanViewModel @Inject constructor(
    private val monthPlanRepository: MonthPlanRepository
) : ViewModel() {

    private val scope: CoroutineScope = CoroutineScope(Job() + Dispatchers.IO)

    val plansResponse = MutableLiveData<NetworkResult<ResponseHelper<List<Plan>>>>()
    val createDailyPlanResponse = MutableLiveData<NetworkResult<ResponseHelper<Nothing>>>()

    fun fetchPlans() = scope.launch {
        plansResponse.postValue(NetworkResult.Loading())
        val response = monthPlanRepository.fetchPlans()
        plansResponse.postValue(response)
    }

    fun createDailyPlan(contractId: Long, planTime: String) = scope.launch {
        createDailyPlanResponse.postValue(NetworkResult.Loading())
        val response = monthPlanRepository.createDailyPlan(contractId, planTime)
        createDailyPlanResponse.postValue(response)
    }
}