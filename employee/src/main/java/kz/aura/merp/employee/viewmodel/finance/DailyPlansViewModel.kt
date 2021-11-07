package kz.aura.merp.employee.viewmodel.finance

import androidx.lifecycle.LiveData
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
import kz.aura.merp.employee.model.*
import kz.aura.merp.employee.util.SearchType
import kz.aura.merp.employee.util.SortType
import org.joda.time.format.DateTimeFormat
import org.joda.time.format.DateTimeFormatter
import javax.inject.Inject

@HiltViewModel
class DailyPlansViewModel @Inject constructor(
    private val dailyPlanRepository: DailyPlanRepository,
    private val referenceRepository: ReferenceRepository
) : ViewModel() {

    private val scope: CoroutineScope = CoroutineScope(Job() + Dispatchers.IO)

    val dailyPlansResponse = MutableLiveData<NetworkResult<ResponseHelper<List<DailyPlan>>>>()
    val businessProcessStatusesResponse = MutableLiveData<NetworkResult<ResponseHelper<List<BusinessProcessStatus>>>>()

    fun fetchDailyPlans() = scope.launch {
        dailyPlansResponse.postValue(NetworkResult.Loading())
        val response = dailyPlanRepository.fetchDailyPlans()
        dailyPlansResponse.postValue(response)
    }

    fun fetchBusinessProcessStatuses() = scope.launch {
        businessProcessStatusesResponse.postValue(NetworkResult.Loading())
        val response = referenceRepository.fetchBusinessProcessStatuses()
        businessProcessStatusesResponse.postValue(response)
    }

    fun filter(dailyPlanFilter: DailyPlanFilter): LiveData<List<DailyPlan>?> {
        val result = MutableLiveData<List<DailyPlan>?>()
        scope.launch(Dispatchers.Default) {
            val (query, sortType, problematic, searchType, statusId) = dailyPlanFilter

            val dtf: DateTimeFormatter = DateTimeFormat.forPattern("dd.MM.yyyy")

            var filteredPlans = dailyPlansResponse.value?.data?.data

            if (!filteredPlans.isNullOrEmpty()) {

                if (query.isNotBlank() && searchType != null) {
                    filteredPlans = when (searchType) {
                        SearchType.CN -> filteredPlans.filter {
                            it.contractNumber.toString().indexOf(query) >= 0
                        }
                        SearchType.FULL_NAME -> {
                            val conditions = ArrayList<(DailyPlan) -> Boolean>()
                            query.lowercase().split(" ").map {
                                conditions.add { plan ->
                                    (plan.getFullName()).lowercase().indexOf(
                                        it
                                    ) >= 0
                                }
                            }
                            filteredPlans.filter { candidate -> conditions.all { it(candidate) } }
                        }
                        SearchType.ADDRESS -> filteredPlans.filter { plan ->
                            plan.customerAddress?.let { it.lowercase().indexOf(query.lowercase()) >= 0 } ?: false
                        }
                        SearchType.PHONE_NUMBER -> filteredPlans.filter { plan ->
                            if (!plan.customerPhoneNumbers.isNullOrEmpty()) {
                                plan.customerPhoneNumbers.map {
                                    it.lowercase().indexOf(query.lowercase()) >= 0
                                }.contains(true)
                            } else false
                        }
                    }
                }

                if (statusId != 4040L) {
                    filteredPlans = filteredPlans.filter { plan ->
                        plan.planBusinessProcessId == statusId
                    }
                }

                filteredPlans = when (sortType) {
                    SortType.PAYMENT_DATE -> filteredPlans.sortedBy { dtf.parseLocalDate(it.nextPaymentDate) }
                    SortType.CONTRACT_DATE -> filteredPlans.sortedBy { dtf.parseLocalDate(it.contractDate) }
                    SortType.FULL_NAME -> filteredPlans.sortedBy { it.getFullName() }
                }

                filteredPlans = filteredPlans.filter { it.problem == problematic }

            }
            result.postValue(filteredPlans)
        }
        return result
    }
}