package kz.aura.merp.customer.data.model

import java.io.Serializable

data class Service (
    val id: Long,
    val image: String,
    val dateOpen: String,
    val paymentDue: Double,
    val description: String,
    val paid: Double? = null,
    val contractId: Long,
    val firstName: String,
    val lastName: String,
    val masterPhone: String,
    val matnrName: String,
    val middleName: String,
    val paymentScheduleNum: Long,
    val sacked: Int,
    val sumTotal: Double,
    val tovarSerial: String,
    val waers: String,
    val masterFio: String,
    val awkey: Long,
    val bukrs: String
) : Serializable
