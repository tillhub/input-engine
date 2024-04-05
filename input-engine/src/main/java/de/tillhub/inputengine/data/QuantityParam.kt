package de.tillhub.inputengine.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.math.BigInteger

@Parcelize
sealed class QuantityParam : Parcelable {
    data class Enable(val quantity: BigInteger) : QuantityParam()
    data object Disable : QuantityParam()
}