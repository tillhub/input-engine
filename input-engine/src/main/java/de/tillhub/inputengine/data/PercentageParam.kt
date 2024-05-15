package de.tillhub.inputengine.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
sealed class PercentageParam : Parcelable {
    class Enable(val percent: PercentIO) : PercentageParam()
    data object Disable : PercentageParam()
}