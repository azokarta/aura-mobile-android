package kz.aura.merp.employee.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class PhoneNumber (
    var phoneNumber: String
): Parcelable