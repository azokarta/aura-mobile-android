package kz.aura.merp.employee.model

data class SalaryDto (
    val salaryId: Long,
    val bukrs: String,
    val staffId: Long,
    val positionId: Int,
    val begDate: String,
    val endDate: String,
    val branchId: Long,
    val countryId: Long,
    val departmentId: Long,
    val positionDto: PositionDto
)