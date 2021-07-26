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

import com.earlier.yma.data.model.MealResponse
import com.earlier.yma.data.model.School

interface MealRepository {

    suspend fun read(
        school: School,
        date: String,
        type: String
    ): MealResponse?
}

class MealRepositoryImpl constructor(
    val localDataSource: MealDataSource,
    val remoteDataSource: MealDataSource
) : MealRepository {

    override suspend fun read(school: School, date: String, type: String): MealResponse? {
        return localDataSource.read(school, date, type)
            ?: remoteDataSource.read(school, date, type).also { response ->
                if (response != null) {
                    localDataSource.write(school, date, type, response)
                }
            }
    }
}
