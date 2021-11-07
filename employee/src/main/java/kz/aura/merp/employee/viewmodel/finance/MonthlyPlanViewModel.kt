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
import kz.aura.merp.employee.data.finance.monthPlan.MonthPlanRepository
import kz.aura.merp.employee.model.Plan
import kz.aura.merp.employee.model.MonthlyPlanFilter
import kz.aura.merp.employee.model.ResponseHelper
import kz.aura.merp.employee.util.SearchType
import kz.aura.merp.employee.util.SortType
import org.joda.time.format.DateTimeFormat
import org.joda.time.format.DateTimeFormatter
import javax.inject.Inject

@HiltViewModel
class MonthlyPlanViewModel @Inject constructor(
    private val monthPlanRepository: MonthPlanRepository,
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

    fun filter(monthlyPlanFilter: MonthlyPlanFilter): LiveData<List<Plan>?> {
        val result = MutableLiveData<List<Plan>?>()
        scope.launch(Dispatchers.Default) {
            val (query, sortType, problematic, searchType) = monthlyPlanFilter

            val dtf: DateTimeFormatter = DateTimeFormat.forPattern("dd.MM.yyyy")

            var filteredPlans = plansResponse.value?.data?.data

            if (!filteredPlans.isNullOrEmpty()) {

                if (query.isNotBlank() && searchType != null) {
                    filteredPlans = when (searchType) {
                        SearchType.CN -> filteredPlans.filter {
                            it.contractNumber.toString().indexOf(query) >= 0
                        }
                        SearchType.FULL_NAME -> {
                            val conditions = ArrayList<(Plan) -> Boolean>()
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
                            plan.customerPhoneNumbers.map {
                                it.lowercase().indexOf(query.lowercase()) >= 0
                            }.contains(true)
                        }
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