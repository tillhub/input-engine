package de.tillhub.inputengine.financial.param

import de.tillhub.inputengine.financial.data.PercentIO
import kotlinx.serialization.Serializable

@Serializable
sealed class PercentageParam {
    @Serializable
    data class Enable(val percent: PercentIO) : PercentageParam()

    @Serializable
    data object Disable : PercentageParam()
}
