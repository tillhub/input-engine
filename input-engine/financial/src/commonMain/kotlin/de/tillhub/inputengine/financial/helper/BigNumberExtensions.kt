package de.tillhub.inputengine.financial.helper

import com.ionspin.kotlin.bignum.decimal.BigDecimal
import com.ionspin.kotlin.bignum.integer.BigInteger

// BigInteger
fun pow10(exp: Int): BigInteger = BigInteger.TEN.pow(exp)
fun BigInteger.isPositive(includeZero: Boolean = false): Boolean {
    return if (includeZero) signum() >= 0 else signum() > 0
}

// BigDecimal
fun pow10decimal(exp: Int): BigDecimal = BigDecimal.TEN.pow(exp)