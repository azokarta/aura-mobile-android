package kz.aura.merp.employee.model

import kz.aura.merp.employee.util.SearchType
import kz.aura.merp.employee.util.SortType

data class DailyPlanFilter(
    val query: String = "",
    val sortType: SortType = SortType.PAYMENT_DATE,
    val problematic: Boolean = false,
    val searchType: SearchType? = null,
    val statusId: Long = 4040L
)
