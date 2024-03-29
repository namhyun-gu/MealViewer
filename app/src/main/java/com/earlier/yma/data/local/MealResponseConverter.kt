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
package com.earlier.yma.data.local

import androidx.room.ProvidedTypeConverter
import androidx.room.TypeConverter
import com.earlier.yma.data.model.MealResponse
import com.squareup.moshi.Moshi
import javax.inject.Inject

@ProvidedTypeConverter
class MealResponseConverter @Inject constructor(
    private val moshi: Moshi
) {
    @TypeConverter
    fun fromString(value: String): MealResponse? {
        val adapter = moshi.adapter(MealResponse::class.java)
        return adapter.fromJson(value)
    }

    @TypeConverter
    fun toString(response: MealResponse): String {
        val adapter = moshi.adapter(MealResponse::class.java)
        return adapter.toJson(response)
    }
}
