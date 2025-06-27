package de.tillhub.inputengine.helper.serializer

import com.ionspin.kotlin.bignum.decimal.BigDecimal
import de.tillhub.inputengine.financial.data.CurrencyIO
import de.tillhub.inputengine.financial.data.MoneyIO
import de.tillhub.inputengine.financial.helper.serializer.MoneyIOSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

@Serializable
data class MoneyWrapper(
    @Serializable(with = MoneyIOSerializer::class)
    val money: MoneyIO,
)

class MoneyIOSerializerTest {

    private val json = Json { encodeDefaults = true }

    @Test
    fun testSerialize() {
        val money = MoneyIO.fromMajorUnits(BigDecimal.parseString("123.45"), CurrencyIO.forCode("EUR"))
        val wrapper = MoneyWrapper(money)

        val result = json.encodeToString(wrapper)
        assertEquals("""{"money":{"amount":"123.45","currency":"EUR"}}""", result)
    }

    @Test
    fun testDeserialize() {
        val input = """{"money":{"amount":"99.99","currency":"USD"}}"""
        val result = json.decodeFromString<MoneyWrapper>(input)

        assertEquals(BigDecimal.parseString("99.99"), result.money.amount)
        assertEquals("USD", result.money.currency.code.value)
    }

    @Test
    fun testRoundTrip() {
        val original = MoneyWrapper(
            MoneyIO.fromMajorUnits(BigDecimal.parseString("0.01"), CurrencyIO.forCode("JPY")),
        )
        val encoded = json.encodeToString(original)
        val decoded = json.decodeFromString<MoneyWrapper>(encoded)

        assertEquals(original, decoded)
    }

    @Test
    fun testDeserializeMissingCurrency() {
        val input = """{"money":{"amount":"42.00"}}"""
        val ex = assertFailsWith<SerializationException> {
            json.decodeFromString<MoneyWrapper>(input)
        }
        assertTrue(ex.message!!.contains("Missing currency field"))
    }

    @Test
    fun testDeserializeMissingAmount() {
        val input = """{"money":{"currency":"USD"}}"""
        val ex = assertFailsWith<SerializationException> {
            json.decodeFromString<MoneyWrapper>(input)
        }
        assertTrue(ex.message!!.contains("Missing amount field"))
    }
}
