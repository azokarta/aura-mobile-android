package kz.aura.merp.employee.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Call(
    val id: Long,
    val phoneNumber: String,
    val callStatusId: Long,
    val callStatusName: String,
    val callDirectionId: Long,
    val callDirectionName: String,
    val callDateTime: String,
    val duration: String,
    val description: String?,
    val contractId: Long,
    val contractNumber: Long,
    val callerId: Long,
    val callerFirstName: String?,
    val callerMiddleName: String?,
    val callerLastName: String?
): Parcelable
