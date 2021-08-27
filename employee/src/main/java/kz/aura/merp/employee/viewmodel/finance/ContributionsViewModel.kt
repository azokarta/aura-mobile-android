package kz.aura.merp.employee.viewmodel.finance

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kz.aura.merp.employee.base.NetworkResult
import kz.aura.merp.employee.data.finance.contributions.ContributionRepository
import kz.aura.merp.employee.model.Contribution
import kz.aura.merp.employee.model.ResponseHelper
import kz.aura.merp.employee.util.isInternetAvailable
import javax.inject.Inject

@HiltViewModel
class ContributionsViewModel @Inject constructor(
    private val contributionRepository: ContributionRepository
) : ViewModel() {

    private val scope: CoroutineScope = CoroutineScope(Job() + Dispatchers.IO)

    val contributionsResponse = MutableLiveData<NetworkResult<ResponseHelper<List<Contribution>>>>()

    fun fetchContributions() = scope.launch {
        val response = contributionRepository.fetchContributions()
        contributionsResponse.postValue(response)
    }
}