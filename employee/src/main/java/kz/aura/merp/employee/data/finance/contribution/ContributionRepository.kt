package kz.aura.merp.employee.data.finance.contribution

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ViewModelScoped
import kz.aura.merp.employee.base.NetworkResult
import kz.aura.merp.employee.base.executeWithResponse
import kz.aura.merp.employee.model.Contribution
import kz.aura.merp.employee.model.ResponseHelper
import javax.inject.Inject

@ViewModelScoped
class ContributionRepository @Inject constructor(
    private val contributionService: ContributionService,
    @ApplicationContext private val context: Context
) {

    suspend fun fetchContributions(): NetworkResult<ResponseHelper<List<Contribution>>> {
        return executeWithResponse(context) {
            contributionService.fetchContributions()
        }
    }

    suspend fun fetchContributionsByContractId(contractId: Long): NetworkResult<ResponseHelper<List<Contribution>>> {
        return executeWithResponse(context) {
            contributionService.fetchContributionsByContractId(contractId)
        }
    }

}