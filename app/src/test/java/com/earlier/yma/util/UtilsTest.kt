/*
 * Copyright 2021 Namhyun, Gu
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.earlier.yma.util

import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
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
