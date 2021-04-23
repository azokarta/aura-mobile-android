package kz.aura.merp.employee.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ServiceApplication (
    val applicationNumber: Long?,
    val applicationDate: String?,
    val applicationRescheduledDate: String?,
    val taxiExpenseAmount: Double?,
    val taxiExpenseAmountCurrency: String?,
    val operatorId: Long?,
    val operatorFirstname: String?,
    val operatorMiddlename: String?,
    val operatorLastname: String?,
    val applicantName: String?,
    val applicationStatusId: Long?,
    val applicationStatusName: String?,
    val applicationTypeId: Long?,
    val applicationTypeName: String?,
    val applicationBusinessProcessId: Long?,
    val applicationBusinessProcessName: String?,
    val applicationUrgencyLevel: Boolean?,
    val applicationDescription: String?,
    val applicationReasonDescription: String?,
    val contractId: Long?,
    val contractNumber: Long?,
    val tovarSn: String?,
    val contractDate: String?,
    val contractCurrency: String?,
    val contractTypeId: Long?,
    val contractTypeName: String?,
    val financeStatusId: Long?,
    val financeStatusName: String?,
    val physicalStatusId: Long?,
    val physicalStatusName: String?,
    val warranty: Boolean?,
    val customerId: Long?,
    val customerYurName: String?,
    val customerFirstname: String?,
    val customerMiddlename: String?,
    val customerLastname: String?,
    val customerPhoneNumbers: List<String?>?,
    val customerAddressId: Long?,
    val customerAddress: String?,
    val customerAddressLongitude: Double?,
    val customerAddressLatitude: Double?
) : Parcelable {
    fun getFullnameOfOperator() = "${operatorLastname ?: ""} ${operatorFirstname ?: ""} ${operatorMiddlename ?: ""}"
    fun getFullNameOfClient() = "${customerLastname ?: ""} ${customerFirstname ?: ""} ${customerMiddlename ?: ""}"
}