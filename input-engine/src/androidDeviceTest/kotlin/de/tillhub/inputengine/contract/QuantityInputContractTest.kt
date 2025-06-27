package de.tillhub.inputengine.contract

import android.app.Activity
import android.os.Bundle
import de.tillhub.inputengine.financial.data.QuantityIO
import de.tillhub.inputengine.helper.ExtraKeys
import org.junit.Assert.assertEquals
import org.junit.Test

class QuantityInputContractTest {

    @Test
    fun parse_success_result_correctly() {
        val quantityValue = 42.5
        val extras = Bundle().apply {
            putDouble(ExtraKeys.EXTRAS_RESULT, quantityValue)
            putBundle(
                ExtraKeys.EXTRAS_ARGS,
                Bundle().apply {
                    putInt("unit", 1)
                    putInt("note", 2)
                },
            )
        }

        val actual = parseQuantityInputResult(Activity.RESULT_OK, extras)
        val expected = QuantityInputResult.Success(
            QuantityIO.of(quantityValue),
            mapOf("unit" to 1, "note" to 2),
        )

        assertEquals(expected, actual)
    }

    @Test
    fun return_canceled_if_result_code_not_ok() {
        val extras = Bundle().apply {
            putDouble(ExtraKeys.EXTRAS_RESULT, 42.0)
        }

        val actual = parseQuantityInputResult(Activity.RESULT_CANCELED, extras)
        assertEquals(QuantityInputResult.Canceled, actual)
    }

    @Test
    fun return_canceled_if_extras_null() {
        val actual = parseQuantityInputResult(Activity.RESULT_OK, null)
        assertEquals(QuantityInputResult.Canceled, actual)
    }

    @Test
    fun return_canceled_if_quantity_missing() {
        val extras = Bundle().apply {
            putBundle(
                ExtraKeys.EXTRAS_ARGS,
                Bundle().apply {
                    putInt("unit", 1)
                },
            )
        }

        val actual = parseQuantityInputResult(Activity.RESULT_OK, extras)
        assertEquals(QuantityInputResult.Canceled, actual)
    }

    @Test
    fun return_canceled_if_quantity_is_nan() {
        val extras = Bundle().apply {
            putDouble(ExtraKeys.EXTRAS_RESULT, Double.NaN)
        }

        val actual = parseQuantityInputResult(Activity.RESULT_OK, extras)
        assertEquals(QuantityInputResult.Canceled, actual)
    }
}
