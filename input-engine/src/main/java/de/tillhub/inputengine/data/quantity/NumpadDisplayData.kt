package de.tillhub.inputengine.data.quantity

/**
 * User readable text and related data of a numpad input.
 */
data class NumpadDisplayData<T>(
    val currentValue: NumpadDisplayValue<T>,
    val initialValue: NumpadDisplayValue<T>,
) {

    fun setCurrentValueWithMinAndMaxValue(
        data: T,
        text: String,
        isValid: Boolean = true,
        minValue: String?,
        maxValue: String?
    ): NumpadDisplayData<T> = this.copy(
        currentValue = NumpadDisplayValue(
            data,
            text,
            isValid,
            minValue,
            maxValue
        )
    )

    fun setCurrentValue(
        data: T,
        text: String,
        isValid: Boolean = true,
    ): NumpadDisplayData<T> = this.copy(
        currentValue = NumpadDisplayValue(
            data,
            text,
            isValid
        )
    )

    fun setInitialValue(
        data: T,
        text: String,
        isValid: Boolean = true,
    ): NumpadDisplayData<T> = this.copy(
        initialValue = NumpadDisplayValue(
            data,
            text,
            isValid
        )
    )

    companion object {
        /**
         * Create a [NumpadDisplayData] with the same value for [currentValue] and [initialValue].
         */
        fun <T> create(
            data: T,
            text: String,
            isValid: Boolean,
        ): NumpadDisplayData<T> {
            return NumpadDisplayData(
                currentValue = NumpadDisplayValue(data, text, isValid),
                initialValue = NumpadDisplayValue(data, text, isValid),
            )
        }
    }
}

data class NumpadDisplayValue<T>(
    val data: T,
    val text: String,
    val isValid: Boolean = true,
    val leftValue: String? = null,
    val rightValue: String? = null
)
