package kz.aura.merp.employee.data.database

import androidx.room.Dao
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import kz.aura.merp.employee.data.database.entities.PlansEntity

@Dao
interface PlansDao {

    @Query("SELECT * FROM plans_table")
    fun readPlans(): Flow<List<PlansEntity>>

}