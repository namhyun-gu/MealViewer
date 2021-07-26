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
package com.earlier.yma.data.model

data class MealResponse(
    val calorie: String,
    val dishes: List<Dish>,
    val origins: List<Origin>,
    val nutrition: List<Nutrition>
) {
    companion object {
        fun dummy(): MealResponse {
            return MealResponse(
                calorie = "100.0 Kcal",
                dishes = listOf(
                    Dish("Dish 1", listOf()),
                    Dish("Dish 2", listOf(1, 2, 3, 4, 5)),
                    Dish("Dish 3", listOf(6, 7, 8, 9, 10)),
                    Dish("Dish 4", listOf(11, 12, 13, 14, 15, 16)),
                    Dish("Dish 4", listOf(17, 18))
                ),
                origins = List(10) {
                    Origin("Food $it", "Origin $it")
                },
                nutrition = List(10) {
                    Nutrition("Nutrition $it", it.toDouble())
                }
            )
        }
    }
}

data class Dish(val name: String, val allergy: List<Int>)

data class Origin(val food: String, val origin: String)

data class Nutrition(val name: String, val value: Double)
