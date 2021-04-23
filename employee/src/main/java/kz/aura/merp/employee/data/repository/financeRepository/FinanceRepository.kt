package kz.aura.merp.employee.data.repository.financeRepository

import dagger.hilt.android.scopes.ViewModelScoped
import javax.inject.Inject

@ViewModelScoped
class FinanceRepository @Inject constructor(
    financeRemoteDataSource: FinanceRemoteDataSource
) {
    val remote = financeRemoteDataSource
}