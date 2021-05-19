package kz.aura.merp.employee.data.repository.financeRepository

import kotlinx.coroutines.flow.Flow
import kz.aura.merp.employee.data.database.dao.PlansDao
import kz.aura.merp.employee.data.database.entities.PlansEntity
import javax.inject.Inject

class FinanceLocalDataSource @Inject constructor(
    private val plansDao: PlansDao
) {

    fun readPlans(): Flow<List<PlansEntity>> {
        return plansDao.readPlans()
    }

}