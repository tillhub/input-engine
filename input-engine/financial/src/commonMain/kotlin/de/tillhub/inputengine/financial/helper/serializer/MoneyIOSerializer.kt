package de.tillhub.inputengine.financial.helper.serializer

import com.ionspin.kotlin.bignum.decimal.BigDecimal
import de.tillhub.inputengine.financial.data.CurrencyIO
import de.tillhub.inputengine.financial.data.MoneyIO
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerializationException
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.CompositeDecoder
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

object MoneyIOSerializer : KSerializer<MoneyIO> {
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("MoneyIO") {
        element("amount", BigDecimalSerializer.descriptor)
        element("currency", CurrencySerializer.descriptor)
    }

    override fun serialize(encoder: Encoder, value: MoneyIO) {
        val composite = encoder.beginStructure(descriptor)
        composite.encodeSerializableElement(descriptor, 0, BigDecimalSerializer, value.amount)
        composite.encodeSerializableElement(descriptor, 1, CurrencySerializer, value.currency)
        composite.endStructure(descriptor)
    }

    override fun deserialize(decoder: Decoder): MoneyIO {
        val dec = decoder.beginStructure(descriptor)
        var amount: BigDecimal? = null
        var currency: CurrencyIO? = null

        loop@ while (true) {
            when (val index = dec.decodeElementIndex(descriptor)) {
                0 -> amount = dec.decodeSerializableElement(descriptor, 0, BigDecimalSerializer)
                1 -> currency = dec.decodeSerializableElement(descriptor, 1, CurrencySerializer)
                CompositeDecoder.DECODE_DONE -> break@loop
                else -> throw SerializationException("Unexpected index $index")
            }
        }

        dec.endStructure(descriptor)
        return MoneyIO.fromMajorUnits(amount!!, currency!!)
    }
}
