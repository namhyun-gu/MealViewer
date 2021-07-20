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
package com.earlier.yma.data

data class MealResponse(
    val calorie: String,
    val dishes: List<Dish>,
    val origins: List<Origin>,
    val nutrition: List<Nutrition>
)

data class Dish(val name: String, val allergy: List<Int>)

data class Origin(val food: String, val origin: String)

data class Nutrition(val name: String, val value: Double)
