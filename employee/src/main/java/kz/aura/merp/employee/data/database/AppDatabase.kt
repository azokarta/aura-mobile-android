package kz.aura.merp.employee.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import kz.aura.merp.employee.data.database.converter.PlansTypeConverter
import kz.aura.merp.employee.data.database.dao.ContributionsDao
import kz.aura.merp.employee.data.database.dao.PlansDao
import kz.aura.merp.employee.data.database.entities.ContributionsEntity
import kz.aura.merp.employee.data.database.entities.PlansEntity

@Database(
    entities = [PlansEntity::class, ContributionsEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(PlansTypeConverter::class)
abstract class AppDatabase: RoomDatabase() {

    abstract fun plansDao(): PlansDao

    abstract fun contributionsDao(): ContributionsDao

}