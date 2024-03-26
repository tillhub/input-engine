package de.tillhub.inputengine.data

import android.os.Bundle
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
sealed class BundleParam : Parcelable {
    data class Enable(val bundle: Bundle) : BundleParam()
    data object Disable : BundleParam()
}