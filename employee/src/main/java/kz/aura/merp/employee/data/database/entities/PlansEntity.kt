package kz.aura.merp.employee.data.database.entities

import androidx.room.Embedded
import androidx.room.Entity
import kz.aura.merp.employee.model.Plan
import kz.aura.merp.employee.util.Constants.PLANS_TABLE

@Entity(tableName = PLANS_TABLE)
class PlansEntity(
    @Embedded
    var plan: Plan
)