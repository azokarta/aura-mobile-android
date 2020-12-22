package kz.aura.merp.customer.models

import java.io.Serializable

data class Product(
    val contractId: Long,
    val tovarSerial: String,
    val price: Double,
    val paid: Double,
    val contractDate: String,
    val contractTypeId: Int,
    val matnrName: String,
    val customerId: Long,
    val paymentScheduleNum: Int,
    val waers: String,
    val awkey: Long,
    val bukrs: String,
    val contractStatusId: Int,
    val contractStatusName: String,
    val nextPaymentDate: String,
    val paymentAmount: Double,
    val iinBin: String,
    val customerBirthday: String,
    val customerFirstName: String,
    val customerMiddleName: String,
    val customerLastName: String,
    val name: String,
    val staffFirstName: String,
    val staffMiddleName: String,
    val staffLastName: String,
    val staffBirthday: String,
    val staffPhone: String,
    val staffSacked: Int,
    val lastServiceDate: String
) : Serializable