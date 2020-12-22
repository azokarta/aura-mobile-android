package kz.aura.merp.employee.data.model

data class Staff (
    val userId: Int,
    val username: String,
    val staffId: Long,
    val mobile: String,
    val agreementDate: String,
    val salaryDtoList: ArrayList<SalaryDto>,
    val staffDto: StaffDto?
)