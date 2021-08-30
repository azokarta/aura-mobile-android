package kz.aura.merp.employee.viewmodel.finance

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kz.aura.merp.employee.base.NetworkResult
import kz.aura.merp.employee.data.finance.contribution.ContributionRepository
import kz.aura.merp.employee.model.Contribution
import kz.aura.merp.employee.model.ResponseHelper
import javax.inject.Inject

@HiltViewModel
class PlanContributionsViewModel @Inject constructor(
    private val contributionRepository: ContributionRepository
) : ViewModel() {

    private val scope: CoroutineScope = CoroutineScope(Job() + Dispatchers.IO)

    val contributionsByContractIdResponse = MutableLiveData<NetworkResult<ResponseHelper<List<Contribution>>>>()

    fun fetchContributionsByContractId(contractId: Long) = scope.launch {
        contributionsByContractIdResponse.postValue(NetworkResult.Loading())
        val response = contributionRepository.fetchContributionsByContractId(contractId)
        contributionsByContractIdResponse.postValue(response)
    }
}