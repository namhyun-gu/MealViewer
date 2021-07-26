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

import com.earlier.yma.data.MealDataSource
import com.earlier.yma.data.model.Cache
import com.earlier.yma.data.model.MealResponse
import com.earlier.yma.data.model.School
import com.earlier.yma.util.CacheUtils
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

class LocalMealDataSource(
    val cacheDao: CacheDao
) : MealDataSource {
    private val jsonAdapter: JsonAdapter<MealResponse>

    init {
        val moshi = Moshi.Builder()
            .addLast(KotlinJsonAdapterFactory())
            .build()

        jsonAdapter = moshi.adapter(MealResponse::class.java)
    }

    override suspend fun read(
        school: School,
        date: String,
        type: String
    ): MealResponse? {
        val cacheKey = CacheUtils.makeKey(school, date, type)
        val cache = cacheDao.loadByKey(cacheKey) ?: return null

        return jsonAdapter.fromJson(cache.content)
    }

    override suspend fun write(
        school: School,
        date: String,
        type: String,
        meal: MealResponse
    ) {
        val json = jsonAdapter.toJson(meal)
        val cache = Cache(
            key = CacheUtils.makeKey(school, date, type),
            content = json
        )

        cacheDao.insert(cache)
    }
}
