package de.tillhub.inputengine.data

import de.tillhub.inputengine.financial.data.CurrencyCode
import de.tillhub.inputengine.financial.data.CurrencyIO
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFails
import kotlin.test.assertNull

class CurrencyIOTests {

	@Test
	fun testEqualsAndHashCode() {
		assertEquals(expected = CurrencyIO.forCode("EUR"), actual = CurrencyIO.forCode("eur"))
		assertEquals(expected = CurrencyIO.forCode("EUR").hashCode(), actual = CurrencyIO.forCode("eur").hashCode())
	}


	@Test
	fun testForCode() {
		assertEquals(expected = "EUR", actual = CurrencyIO.forCode(CurrencyCode.parse("EUR")).code.toString())
		assertNull(CurrencyIO.forCodeOrNull(CurrencyCode.parse("ABC")))

		assertEquals(
			expected = "Invalid ISO 4217 currency code: ABC",
			actual = assertFails { CurrencyIO.forCode(CurrencyCode.parse("ABC")) }.message
		)
	}


	@Test
	fun testForCodeString() {
		assertEquals(expected = "EUR", actual = CurrencyIO.forCode("EUR").code.toString())
		assertEquals(expected = "EUR", actual = CurrencyIO.forCode("eur").code.toString())
		assertNull(CurrencyIO.forCodeOrNull("abc"))
		assertNull(CurrencyIO.forCodeOrNull("ab1"))

		assertEquals(
			expected = "Invalid ISO 4217 currency code: abc",
			actual = assertFails { CurrencyIO.forCode("abc") }.message
		)
		assertEquals(
			expected = "Invalid ISO 4217 currency code format: ab1",
			actual = assertFails { CurrencyIO.forCode("ab1") }.message
		)
	}


	@Test
	fun testProperties() {
		val eurCode = CurrencyCode.parse("EUR")
		val eur = CurrencyIO.forCode(eurCode)

		assertEquals(expected = eurCode, actual = eur.code)
		assertEquals(expected = 2, actual = eur.defaultFractionDigits)
		assertEquals(expected = 978, actual = eur.numericCode)
	}


	@Test
	fun testToString() {
		assertEquals(expected = "EUR", actual = CurrencyIO.forCode("eur").toString())
	}
}
