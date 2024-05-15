package de.tillhub.inputengine.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
sealed class MoneyParam : Parcelable {
    class Enable(val amount: MoneyIO) : MoneyParam() {
        override fun toString() = "Enable(amount=$amount)"
        override fun equals(other: Any?) = other is Enable && amount == other.amount
        override fun hashCode() = amount.hashCode()
    }
    data object Disable : MoneyParam()
}