package com.earlier.yma.util

import org.junit.Assert.*
import org.junit.Test

class UtilsTest {
    @Test
    fun `parseAllergyInfo provide empty allergy info`() {
        val (food, allergyInfo) = parseAllergyInfo("food")

        assertEquals("food", food)
        assertTrue(allergyInfo.isEmpty())
    }

    @Test
    fun `parseAllergyInfo provide one allergy info`() {
        val (food, allergyInfo) = parseAllergyInfo("food1.")

        assertEquals("food", food)
        assertTrue(allergyInfo.isNotEmpty())
        assertEquals(1, allergyInfo.first())
    }

    @Test
    fun `parseAllergyInfo provide one allergy info more than 10`() {
        val (food, allergyInfo) = parseAllergyInfo("food10.")

        assertEquals("food", food)
        assertTrue(allergyInfo.isNotEmpty())
        assertEquals(10, allergyInfo.first())
    }

    @Test
    fun `parseAllergyInfo provide many allergy info`() {
        val (food, allergyInfo) = parseAllergyInfo("food1.2.3.10.")

        assertEquals("food", food)
        assertTrue(allergyInfo.isNotEmpty())
        assertArrayEquals(intArrayOf(1, 2, 3, 10), allergyInfo.toIntArray())
    }
}