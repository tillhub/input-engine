package de.tillhub.inputengine.helper.serializer

import com.ionspin.kotlin.bignum.decimal.BigDecimal
import de.tillhub.inputengine.financial.helper.serializer.BigDecimalSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals

@Serializable
data class DecimalWrapper(
    @Serializable(with = BigDecimalSerializer::class)
    val value: BigDecimal
)

class BigDecimalSerializerTest {

    private val json = Json { encodeDefaults = true }

    @Test
    fun testSerialize() {
        val decimal = BigDecimal.parseString("12345.6789")
        val wrapper = DecimalWrapper(decimal)

        val serialized = json.encodeToString(wrapper)
        assertEquals("""{"value":"12345.6789"}""", serialized)
    }

    @Test
    fun testDeserialize() {
        val input = """{"value":"9876543210.0001"}"""
        val result = json.decodeFromString<DecimalWrapper>(input)

        assertEquals(BigDecimal.parseString("9876543210.0001"), result.value)
    }

    @Test
    fun testRoundTrip() {
        val original = DecimalWrapper(BigDecimal.parseString("0.000000001"))
        val encoded = json.encodeToString(original)
        val decoded = json.decodeFromString<DecimalWrapper>(encoded)

        assertEquals(original, decoded)
    }
}
