package de.tillhub.inputengine.domain

import kotlinx.serialization.Serializable

@Serializable
sealed class StringParam {
    data class Enable(
        val value: String,
    ) : StringParam()

    data object Disable : StringParam()
}
