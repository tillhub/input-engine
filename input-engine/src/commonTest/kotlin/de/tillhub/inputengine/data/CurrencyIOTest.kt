package de.tillhub.inputengine.data

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFails
import kotlin.test.assertNull
import kotlin.test.assertTrue

class CurrencyIOTest {
    @Test
    fun testEqualsAndHashCode() {
        assertEquals(expected = CurrencyIO.forCode("EUR"), actual = CurrencyIO.forCode("EUR"))
        assertEquals(expected = CurrencyIO.forCode("EUR").hashCode(), actual = CurrencyIO.forCode("EUR").hashCode())
    }

    @Test
    fun testForCode() {
        assertEquals(expected = "EUR", actual = CurrencyIO.forCode("EUR").isoCode)
        assertNull(CurrencyIO.forCodeOrNull("ABC"))

        assertEquals(
            expected = "Invalid ISO 4217 currency code: ABC",
            actual = assertFails { CurrencyIO.forCode("ABC") }.message,
        )
    }

    @Test
    fun testForCodeString() {
        assertEquals(expected = "EUR", actual = CurrencyIO.forCode("EUR").isoCode)
        assertNull(CurrencyIO.forCodeOrNull("abc"))
        assertNull(CurrencyIO.forCodeOrNull("ab1"))

        assertEquals(
            expected = "Invalid ISO 4217 currency code: abc",
            actual = assertFails { CurrencyIO.forCode("abc") }.message,
        )
        assertEquals(
            expected = "Invalid ISO 4217 currency code: ab1",
            actual = assertFails { CurrencyIO.forCode("ab1") }.message,
        )
    }

    @Test
    fun testProperties() {
        val eurCode = "EUR"
        val eur = CurrencyIO.forCode(eurCode)

        assertEquals(expected = eurCode, actual = eur.isoCode)
        assertEquals(expected = 2, actual = eur.defaultFractionDigits)
        assertEquals(expected = 978, actual = eur.numericCode)
    }

    @Test
    fun testToString() {
        assertEquals(
            expected = "CurrencyIO(isoCode=EUR, defaultFractionDigits=2, numericCode=978)",
            actual = CurrencyIO.forCode("EUR").toString(),
        )
    }

    @Test
    fun testAllCurrencyCodesAreUnique() {
        val codes = CurrencyIO.all.map { it.isoCode }
        assertEquals(codes.size, codes.toSet().size, "Currency codes in 'all' must be unique")
    }

    @Test
    fun testCurrencyTableHasExpectedSize() {
        assertTrue(CurrencyIO.all.size > 150, "Currency table should have at least 150 entries")
    }
}
