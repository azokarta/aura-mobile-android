package kz.aura.merp.employee.model

data class PlanFilter(
    val query: String,
    val selectedSortFilter: Int,
    val selectedStatusFilter: Int,
    val selectedSearchBy: Int,
    val problematic: Boolean
)
