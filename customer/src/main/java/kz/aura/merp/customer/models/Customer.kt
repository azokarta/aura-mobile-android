package kz.aura.merp.customer.models

data class Customer (
    val accountant: String?,
    val birthday: String?,
    val countryId: Int?,
    val customerId: Long?,
    val director: String?,
    val customerFio: String?,
    val fizYur: Int?,
    val iinBin: String?,
    val name: String?,
    val passportGivenBy: String?,
    val passportGivenDate: String?,
    val passportId: String?,
    val countryName: String?,
    val telephone: String?,
    val mobile: String?,
    val address: String?,
    val addressReg: String?,
    val addressWork: String?
)