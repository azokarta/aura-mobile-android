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
import kz.aura.merp.employee.model.PaymentSchedule
import kz.aura.merp.employee.model.ResponseHelper
import javax.inject.Inject

@HiltViewModel
class PlanPaymentScheduleViewModel @Inject constructor(
    private val monthPlanRepository: MonthPlanRepository
) : ViewModel() {

    private val scope: CoroutineScope = CoroutineScope(Job() + Dispatchers.IO)

    val paymentScheduleResponse = MutableLiveData<NetworkResult<ResponseHelper<List<PaymentSchedule>>>>()

    fun fetchPaymentSchedule(contractId: Long) = scope.launch {
        paymentScheduleResponse.postValue(NetworkResult.Loading())
        val response = monthPlanRepository.fetchPaymentSchedule(contractId)
        paymentScheduleResponse.postValue(response)
    }
}