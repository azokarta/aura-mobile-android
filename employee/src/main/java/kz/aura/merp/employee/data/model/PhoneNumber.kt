package kz.aura.merp.employee.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class PhoneNumber (
    var phoneNumber: String
): Parcelable