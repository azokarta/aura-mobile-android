package kz.aura.merp.employee.data.database.dao

import androidx.room.Dao
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import kz.aura.merp.employee.data.database.entities.ContributionsEntity

@Dao
interface ContributionsDao {

    @Query("SELECT * FROM contributions_table")
    fun readPlans(): Flow<List<ContributionsEntity>>

}