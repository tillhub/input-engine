package de.tillhub.inputengine.helper.serializer

import de.tillhub.inputengine.financial.data.CurrencyIO
import de.tillhub.inputengine.financial.helper.serializer.CurrencySerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

@Serializable
data class CurrencyWrapper(
    @Serializable(with = CurrencySerializer::class)
    val currency: CurrencyIO
)

class CurrencySerializerTest {

    private val json = Json { encodeDefaults = true }

    @Test
    fun testSerializeCurrencyIO() {
        val wrapper = CurrencyWrapper(CurrencyIO.forCode("EUR"))
        val result = json.encodeToString(wrapper)
        assertEquals("""{"currency":"EUR"}""", result)
    }

    @Test
    fun testDeserializeCurrencyIO() {
        val jsonInput = """{"currency":"USD"}"""
        val result = json.decodeFromString<CurrencyWrapper>(jsonInput)
        assertEquals("USD", result.currency.code.value)
    }

    @Test
    fun testDeserializeInvalidCurrencyIO() {
        val jsonInput = """{"currency":"XYZ"}"""
        val exception = assertFailsWith<SerializationException> {
            json.decodeFromString<CurrencyWrapper>(jsonInput)
        }
        assertTrue(exception.message!!.contains("Unknown currency code: XYZ"))
    }

    @Test
    fun testRoundTrip() {
        val original = CurrencyWrapper(CurrencyIO.forCode("JPY"))
        val encoded = json.encodeToString(original)
        val decoded = json.decodeFromString<CurrencyWrapper>(encoded)
        assertEquals(original, decoded)
    }
}
