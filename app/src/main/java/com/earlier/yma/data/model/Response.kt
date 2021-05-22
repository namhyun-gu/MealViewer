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

import com.squareup.moshi.Json

sealed class Response<T>(open val content: List<T>? = null) {
    val isValid: Boolean
        get() = content != null && content!!.size >= 2
}

data class MealResponse(
    @Json(name = "mealServiceDietInfo") override val content: List<MealContent>? = null,
) : Response<MealContent>(content)

data class SearchResponse(
    @Json(name = "schoolInfo") override val content: List<SearchContent>? = null,
) : Response<SearchContent>(content)

data class Result(
    @Json(name = "CODE") val code: String? = null,
    @Json(name = "MESSAGE") val message: String? = null,
)

data class Head(
    @Json(name = "list_total_count") val listTotalCount: Int? = null,
    @Json(name = "RESULT") val result: Result? = null,
)
