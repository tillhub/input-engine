package de.tillhub.inputengine.data

import kotlinx.serialization.Serializable

@Serializable
sealed class MoneyParam {
    @Serializable
    data class Enable(
        val amount: MoneyIO,
    ) : MoneyParam()

    @Serializable
    data object Disable : MoneyParam()
}
