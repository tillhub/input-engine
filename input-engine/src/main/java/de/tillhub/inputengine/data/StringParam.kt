package de.tillhub.inputengine.data

import android.os.Parcelable
import androidx.annotation.StringRes
import kotlinx.parcelize.Parcelize

@Parcelize
sealed class StringParam : Parcelable {
    class StringResource(@StringRes val resIdRes: Int) : StringParam()
    class String(val value: kotlin.String) : StringParam()
}