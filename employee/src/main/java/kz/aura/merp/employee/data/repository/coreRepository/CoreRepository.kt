package kz.aura.merp.employee.data.repository.coreRepository

import dagger.hilt.android.scopes.ViewModelScoped
import javax.inject.Inject

@ViewModelScoped
class CoreRepository @Inject constructor(
    coreRemoteDataSource: CoreRemoteDataSource
) {
    val remote = coreRemoteDataSource
}