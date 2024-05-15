package de.tillhub.inputengine.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
sealed class QuantityParam : Parcelable {
    class Enable(val value: QuantityIO) : QuantityParam()
    data object Disable : QuantityParam()
}