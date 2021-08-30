package kz.aura.merp.employee.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kz.aura.merp.employee.data.auth.AuthService
import kz.aura.merp.employee.data.core.CoreService
import retrofit2.Retrofit

@Module
@InstallIn(SingletonComponent::class)
object ServiceModule {

    @Provides
    fun provideAuthService(@AuthServiceQualifier retrofit: Retrofit): AuthService {
        return retrofit.create(AuthService::class.java)
    }

    @Provides
    fun provideCoreService(@CoreServiceQualifier retrofit: Retrofit): CoreService {
        return retrofit.create(CoreService::class.java)
    }

}