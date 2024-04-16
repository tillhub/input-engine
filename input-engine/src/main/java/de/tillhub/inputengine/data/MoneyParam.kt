package de.tillhub.inputengine.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
sealed class MoneyParam : Parcelable {
    data class Enable(val amount: MoneyIO) : MoneyParam()
    data object Disable : MoneyParam()
}