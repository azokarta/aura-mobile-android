package kz.aura.merp.customer.data.model

import android.os.Parcel
import android.os.Parcelable

data class PaymentSchedule(
    val paymentTypeId: Int,
    val paymentDate: String?,
    val sum2: Double,
    val paid: Double,
    val paymentScheduleNum: Int,
    val matnrName: String?,
    val serviceDescription: String?,
    val waers: String?
): Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString(),
        parcel.readDouble(),
        parcel.readDouble(),
        parcel.readInt(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(paymentTypeId)
        parcel.writeString(paymentDate)
        parcel.writeDouble(sum2)
        parcel.writeDouble(paid)
        parcel.writeInt(paymentScheduleNum)
        parcel.writeString(matnrName)
        parcel.writeString(serviceDescription)
        parcel.writeString(waers)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<PaymentSchedule> {
        override fun createFromParcel(parcel: Parcel): PaymentSchedule {
            return PaymentSchedule(parcel)
        }

        override fun newArray(size: Int): Array<PaymentSchedule?> {
            return arrayOfNulls(size)
        }
    }

}