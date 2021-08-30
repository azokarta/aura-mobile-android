package kz.aura.merp.employee.data.finance.contribution

import dagger.hilt.android.scopes.ViewModelScoped
import kz.aura.merp.employee.base.NetworkResult
import kz.aura.merp.employee.base.executeWithResponse
import kz.aura.merp.employee.model.Contribution
import kz.aura.merp.employee.model.ResponseHelper
import javax.inject.Inject

@ViewModelScoped
class ContributionRepository @Inject constructor(
    private val contributionService: ContributionService
) {

    suspend fun fetchContributions(): NetworkResult<ResponseHelper<List<Contribution>>> {
        return executeWithResponse {
            contributionService.fetchContributions()
        }
    }

    suspend fun fetchContributionsByContractId(contractId: Long): NetworkResult<ResponseHelper<List<Contribution>>> {
        return executeWithResponse {
            contributionService.fetchContributionsByContractId(contractId)
        }
    }

}