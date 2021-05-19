package kz.aura.merp.employee.data.database.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import kz.aura.merp.employee.data.database.entities.PlansEntity

@Dao
interface PlansDao {

    @Query("SELECT * FROM plans_table")
    fun readPlans(): Flow<List<PlansEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(plans: List<PlansEntity>)

}