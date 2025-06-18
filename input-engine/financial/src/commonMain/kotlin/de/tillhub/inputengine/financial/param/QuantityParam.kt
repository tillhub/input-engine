package de.tillhub.inputengine.financial.param

import de.tillhub.inputengine.financial.data.QuantityIO
import kotlinx.serialization.Serializable

@Serializable
sealed class QuantityParam {
    @Serializable
    data class Enable(val value: QuantityIO) : QuantityParam()

    @Serializable
    data object Disable : QuantityParam()
}