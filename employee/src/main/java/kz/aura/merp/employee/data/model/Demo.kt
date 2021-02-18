package kz.aura.merp.employee.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Demo(
    val demoId: Long,
    val clientName: String?,
    val address: String?,
    var price: Double?,
    var note: String?,
    val crmPhoneDtoList: ArrayList<PhoneNumber>?,
    var resultId: Int?,
    val dateTime: String?,
    val customerId: Long?,
    val contractNumber: Long?,
    val dealerId: Long?,
    val recoId: Long?,
    val dateTimeFormatted: String?,
    var ocrDemoStatus: String?
): Parcelable