package de.tillhub.inputengine.financial.param

import de.tillhub.inputengine.financial.data.MoneyIO
import kotlinx.serialization.Serializable

@Serializable
sealed class MoneyParam {
    @Serializable
    data class Enable(val amount: MoneyIO) : MoneyParam() {
        override fun toString() = "Enable(amount=$amount)"
        override fun equals(other: Any?) = other is Enable && amount == other.amount
        override fun hashCode(): Int  = amount.hashCode()
    }

    @Serializable
    data object Disable : MoneyParam()
}