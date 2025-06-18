package de.tillhub.inputengine.financial.helper.serializer

import de.tillhub.inputengine.financial.data.CurrencyIO
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerializationException
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

object CurrencySerializer : KSerializer<CurrencyIO> {
	override val descriptor: SerialDescriptor =
		PrimitiveSerialDescriptor("Currency", PrimitiveKind.STRING)

	override fun serialize(encoder: Encoder, value: CurrencyIO) {
		encoder.encodeString(value.code.value) // assuming CurrencyCode wraps a String
	}

	override fun deserialize(decoder: Decoder): CurrencyIO {
		val code = decoder.decodeString()
		return CurrencyIO.Companion.forCodeOrNull(code)
			?: throw SerializationException("Unknown currency code: $code")
	}
}
