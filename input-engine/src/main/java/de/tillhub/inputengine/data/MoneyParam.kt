package de.tillhub.inputengine.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.math.BigInteger

@Parcelize
sealed class MoneyParam : Parcelable {
    data class Enable(val amount: BigInteger) : MoneyParam()
    data object Disable : MoneyParam()
}