package de.tillhub.inputengine.helper.serializer

import de.tillhub.inputengine.financial.data.CurrencyCode
import de.tillhub.inputengine.financial.helper.serializer.CurrencyCodeSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals

@Serializable
data class CurrencyCodeWrapper(
    @Serializable(with = CurrencyCodeSerializer::class)
    val currency: CurrencyCode,
)

class CurrencyCodeSerializerTest {

    private val json = Json { encodeDefaults = true }

    @Test
    fun testSerialize() {
        val wrapper = CurrencyCodeWrapper(CurrencyCode.parse("USD"))
        val serialized = json.encodeToString(wrapper)
        assertEquals("""{"currency":"USD"}""", serialized)
    }

    @Test
    fun testDeserialize() {
        val input = """{"currency":"EUR"}"""
        val decoded = json.decodeFromString<CurrencyCodeWrapper>(input)
        assertEquals(CurrencyCode.parse("EUR"), decoded.currency)
    }

    @Test
    fun testRoundTrip() {
        val original = CurrencyCodeWrapper(CurrencyCode.parse("JPY"))
        val encoded = json.encodeToString(original)
        val decoded = json.decodeFromString<CurrencyCodeWrapper>(encoded)
        assertEquals(original, decoded)
    }
}
