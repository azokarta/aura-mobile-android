package kz.aura.merp.employee.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kz.aura.merp.employee.data.finance.call.CallService
import kz.aura.merp.employee.data.finance.contribution.ContributionService
import kz.aura.merp.employee.data.finance.dailyPlan.DailyPlanService
import kz.aura.merp.employee.data.finance.monthPlan.MonthPlanService
import kz.aura.merp.employee.data.finance.reference.ReferenceService
import kz.aura.merp.employee.data.finance.scheduledCall.ScheduledCallService
import retrofit2.Retrofit

@Module
@InstallIn(SingletonComponent::class)
object FinanceServiceModule {

    @Provides
    fun provideCallService(@FinanceServiceQualifier retrofit: Retrofit): CallService {
        return retrofit.create(CallService::class.java)
    }

    @Provides
    fun provideContributionService(@FinanceServiceQualifier retrofit: Retrofit): ContributionService {
        return retrofit.create(ContributionService::class.java)
    }

    @Provides
    fun provideDailyPlanService(@FinanceServiceQualifier retrofit: Retrofit): DailyPlanService {
        return retrofit.create(DailyPlanService::class.java)
    }

    @Provides
    fun provideMonthPlanService(@FinanceServiceQualifier retrofit: Retrofit): MonthPlanService {
        return retrofit.create(MonthPlanService::class.java)
    }

    @Provides
    fun provideScheduleCallService(@FinanceServiceQualifier retrofit: Retrofit): ScheduledCallService {
        return retrofit.create(ScheduledCallService::class.java)
    }

    @Provides
    fun provideReferenceService(@FinanceServiceQualifier retrofit: Retrofit): ReferenceService {
        return retrofit.create(ReferenceService::class.java)
    }

}