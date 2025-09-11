package de.tillhub.inputengine.data

import de.tillhub.inputengine.domain.StringParam
import kotlinx.serialization.Serializable

@Serializable
sealed class PercentageParam {
    @Serializable
    data class Enable(
        val percent: PercentIO,
    ) : PercentageParam()

    @Serializable
    data object Disable : PercentageParam()
}

fun PercentageParam.mapToStringParam(transform: (value: PercentIO) -> String): StringParam = when (this) {
    is PercentageParam.Disable -> StringParam.Disable
    is PercentageParam.Enable -> StringParam.Enable(transform(percent))
}
