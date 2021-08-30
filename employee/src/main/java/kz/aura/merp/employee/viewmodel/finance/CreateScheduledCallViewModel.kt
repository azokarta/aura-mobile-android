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
import kz.aura.merp.employee.data.finance.scheduledCall.ScheduledCallRepository
import kz.aura.merp.employee.model.AssignScheduledCallCommand
import kz.aura.merp.employee.model.ResponseHelper
import kz.aura.merp.employee.util.CountryCode
import javax.inject.Inject

@HiltViewModel
class CreateScheduledCallViewModel @Inject constructor(
    private val scheduledCallRepository: ScheduledCallRepository,
    val preferences: AppPreferences
) : ViewModel() {

    private val scope: CoroutineScope = CoroutineScope(Job() + Dispatchers.IO)
    val assignScheduledCallResponse = MutableLiveData<NetworkResult<ResponseHelper<Nothing>>>()

    fun assignScheduledCall(contractId: Long, scheduledCall: AssignScheduledCallCommand) = scope.launch {
        assignScheduledCallResponse.postValue(NetworkResult.Loading())
        val response = scheduledCallRepository.assignScheduledCall(contractId, scheduledCall)
        assignScheduledCallResponse.postValue(response)
    }

    fun getCountryCode(): CountryCode {
        val countryCallingCode = preferences.countryCallingCode
        return CountryCode.values().find { it.phoneCode == countryCallingCode } ?: CountryCode.KZ
    }

}