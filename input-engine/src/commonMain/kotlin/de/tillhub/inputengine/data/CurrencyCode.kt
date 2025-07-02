package de.tillhub.inputengine.data

import de.tillhub.inputengine.domain.helper.isLatinLetter
import de.tillhub.inputengine.domain.serializer.CurrencyCodeSerializer
import kotlinx.serialization.Serializable

/**
 * An ISO 4217 3-letter currency code, for example `EUR` or `USD`.
 *
 * References:
 * - [https://www.iso.org/iso-4217-currency-codes.html]
 * - [https://www.currency-iso.org/en/home/tables/table-a1.html]
 */
@Serializable(CurrencyCodeSerializer::class)
class CurrencyCode(val isoValue: String) {

    override fun equals(other: Any?): Boolean =
        this === other || (other is CurrencyCode && isoValue == other.isoValue)

    override fun hashCode(): Int = isoValue.hashCode()

    fun isValid(): Boolean = CurrencyIO.forCodeOrNull(this) != null

    override fun toString(): String = isoValue

    companion object {
        fun parse(string: String): CurrencyCode =
            parseOrNull(string) ?: error("Invalid ISO 4217 currency code format: $string")

        fun parseOrNull(string: String): CurrencyCode? =
            string.takeIf { isValidFormat(it) }?.let { CurrencyCode(it.uppercase()) }

        private fun isValidFormat(string: String) =
            string.length == 3 &&
                string[0].isLatinLetter() &&
                string[1].isLatinLetter() &&
                string[2].isLatinLetter()
    }
}
