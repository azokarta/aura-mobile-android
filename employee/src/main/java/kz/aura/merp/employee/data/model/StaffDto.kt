package kz.aura.merp.employee.data.model

data class StaffDto (
    val staffId: Long,
    val mobile: String,
    val lastName: String,
    val middlename: String,
    val sacked: Int,
    val sackedDate: String,
    val birthDay: String
)