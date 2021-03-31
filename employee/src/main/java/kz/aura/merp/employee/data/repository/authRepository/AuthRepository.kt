package kz.aura.merp.employee.data.repository.authRepository

import dagger.hilt.android.scopes.ViewModelScoped
import javax.inject.Inject

@ViewModelScoped
class AuthRepository @Inject constructor(
    authRemoteDataSource: AuthRemoteDataSource
) {
    val remote = authRemoteDataSource
}