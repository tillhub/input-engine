package de.tillhub.inputengine.financial.helper.serializer

import de.tillhub.inputengine.financial.data.CurrencyCode
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.encoding.Decoder

internal object CurrencyCodeSerializer : KSerializer<CurrencyCode> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor(
            "CurrencyCode",
            PrimitiveKind.STRING
        )

    override fun serialize(encoder: Encoder, value: CurrencyCode) {
        encoder.encodeString(value.toString())
    }

    override fun deserialize(decoder: Decoder): CurrencyCode =
        CurrencyCode.parse(decoder.decodeString())
}