package kz.aura.merp.employee.model

data class Salary (
    val userId: Long? = null,
    val username: String? = null,
    var phoneNumber: String? = null,
    val staffId: Long? = null,
    val staffFirstname: String? = null,
    val staffMiddlename: String? = null,
    val staffLastname: String? = null,
    val salaryId: Long? = null,
    var bukrs: String? = null,
    val companyName: String? = null,
    val countryId: Long? = null,
    val countryName: String? = null,
    val branchId: Long? = null,
    val branchName: String? = null,
    val positionId: Long? = null,
    val positionName: String? = null,
    val departmentId: Long? = null,
    val departmentName: String? = null,
    val salaryBeginDate: String? = null,
    var salaryEndDate: String? = null
)