package kz.aura.merp.employee.data.database.entities

import androidx.room.Embedded
import androidx.room.Entity
import kz.aura.merp.employee.model.Contribution
import kz.aura.merp.employee.util.Constants

@Entity(tableName = Constants.PLANS_TABLE)
class ContributionsEntity (
    @Embedded
    var contribution: Contribution
)