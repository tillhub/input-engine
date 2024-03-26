package de.tillhub.inputengine.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.math.BigDecimal

@Parcelize
sealed class AmountParam : Parcelable {
    data class Enable(val amount: BigDecimal) : AmountParam()
    data object Disable : AmountParam()
}