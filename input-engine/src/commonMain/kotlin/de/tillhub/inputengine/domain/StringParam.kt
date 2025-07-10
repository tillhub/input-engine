package de.tillhub.inputengine.domain

sealed class StringParam {
    data class Enable(
        val value: String,
    ) : StringParam()

    data object Disable : StringParam()
}
