package de.tillhub.inputengine.data

import de.tillhub.inputengine.domain.StringParam
import kotlinx.serialization.Serializable

@Serializable
sealed class QuantityParam {
    @Serializable
    data class Enable(
        val value: QuantityIO,
    ) : QuantityParam()

    @Serializable
    data object Disable : QuantityParam()
}

fun QuantityParam.mapToStringParam(transform: (value: QuantityIO) -> String): StringParam = when (this) {
    is QuantityParam.Disable -> StringParam.Disable
    is QuantityParam.Enable -> StringParam.Enable(transform(value))
}
