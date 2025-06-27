package de.tillhub.inputengine.data

import de.tillhub.inputengine.financial.data.CurrencyCode
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFails
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class CurrencyCodeTest {

    @Test
    fun testEqualsAndHashCode() {
        assertEquals(CurrencyCode.parse("EUR"), CurrencyCode.parse("eur"))
        assertEquals(CurrencyCode.parse("EUR").hashCode(), CurrencyCode.parse("eur").hashCode())
    }

    @Test
    fun testIsValid() {
        assertTrue(CurrencyCode.parse("eur").isValid(), "Known currency code should be valid")
        assertFalse(CurrencyCode.parse("abc").isValid(), "Unknown currency code should be invalid")
    }

    @Test
    fun testParseAndToString() {
        assertEquals("EUR", CurrencyCode.parse("EUR").toString())
        assertEquals("ABC", CurrencyCode.parse("abc").toString())
    }

    @Test
    fun testParseFailsOnInvalidFormat() {
        val ex = assertFails { CurrencyCode.parse("ab1") }
        assertEquals("Invalid ISO 4217 currency code format: ab1", ex.message)

        assertFails { CurrencyCode.parse("US") }
        assertFails { CurrencyCode.parse("EURO") }
        assertFails { CurrencyCode.parse("12A") }
        assertFails { CurrencyCode.parse("") }
    }

    @Test
    fun testParseOrNull() {
        assertEquals(CurrencyCode("USD"), CurrencyCode.parseOrNull("usd"))
        assertEquals(CurrencyCode("JPY"), CurrencyCode.parseOrNull("jpy"))
        assertNull(CurrencyCode.parseOrNull("us1"))
        assertNull(CurrencyCode.parseOrNull("e"))
        assertNull(CurrencyCode.parseOrNull("toolong"))
    }

    @Test
    fun testToString() {
        val code = CurrencyCode.parse("gBp")
        assertEquals("GBP", code.toString())
    }
}
