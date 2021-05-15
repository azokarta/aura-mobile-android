package kz.aura.merp.employee.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import kz.aura.merp.employee.data.database.entities.PlansEntity

@Database(
    entities = [PlansEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(PlansTypeConverter::class)
abstract class PlansDatabase: RoomDatabase() {

    abstract fun plansDao(): PlansDao

}