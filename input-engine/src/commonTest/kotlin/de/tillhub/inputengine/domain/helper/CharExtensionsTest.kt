package de.tillhub.inputengine.domain.helper

import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class CharExtensionsTest {

    @Test
    fun testLatinLowercaseLetters() {
        assertTrue('a'.isLatinLetter())
        assertTrue('m'.isLatinLetter())
        assertTrue('z'.isLatinLetter())
    }

    @Test
    fun testLatinUppercaseLetters() {
        assertTrue('A'.isLatinLetter())
        assertTrue('M'.isLatinLetter())
        assertTrue('Z'.isLatinLetter())
    }

    @Test
    fun testNonLatinLetters() {
        assertFalse('ä'.isLatinLetter()) // umlaut
        assertFalse('ç'.isLatinLetter())
        assertFalse('1'.isLatinLetter())
        assertFalse('%'.isLatinLetter())
        assertFalse('Ω'.isLatinLetter()) // Greek
        assertFalse('中'.isLatinLetter()) // Chinese
    }
}
