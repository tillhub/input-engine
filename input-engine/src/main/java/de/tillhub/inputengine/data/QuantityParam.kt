package de.tillhub.inputengine.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.math.BigDecimal

@Parcelize
sealed class QuantityParam : Parcelable {
    data class Enable(val value: QuantityIO) : QuantityParam()
    data object Disable : QuantityParam()
}