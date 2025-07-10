package de.tillhub.inputengine.domain.serializer

import com.ionspin.kotlin.bignum.integer.BigInteger
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals

@Serializable
data class IntegerWrapper(
    @Serializable(with = BigIntegerSerializer::class)
    val value: BigInteger,
)

class BigIntegerSerializerTest {

    private val json = Json { encodeDefaults = true }

    @Test
    fun testSerialize() {
        val value = BigInteger.parseString("12345678901234567890")
        val wrapper = IntegerWrapper(value)

        val serialized = json.encodeToString(wrapper)
        assertEquals("""{"value":"12345678901234567890"}""", serialized)
    }

    @Test
    fun testDeserialize() {
        val input = """{"value":"98765432109876543210"}"""
        val result = json.decodeFromString<IntegerWrapper>(input)

        assertEquals(BigInteger.parseString("98765432109876543210"), result.value)
    }

    @Test
    fun testRoundTrip() {
        val original = IntegerWrapper(BigInteger.parseString("1000000000000000000000001"))
        val encoded = json.encodeToString(original)
        val decoded = json.decodeFromString<IntegerWrapper>(encoded)

        assertEquals(original, decoded)
    }
}
