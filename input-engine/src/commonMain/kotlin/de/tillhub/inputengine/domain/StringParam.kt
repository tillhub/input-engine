package de.tillhub.inputengine.domain

import kotlinx.serialization.Serializable

@Serializable
sealed class StringParam {
    @Serializable
    data class Enable(
        val value: String,
    ) : StringParam()

    @Serializable
    data object Disable : StringParam()
}
