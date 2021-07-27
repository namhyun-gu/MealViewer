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
package com.earlier.yma.data.remote

import com.earlier.yma.data.MealDataSource
import com.earlier.yma.data.model.MealResponse
import com.earlier.yma.data.model.School
import com.skydoves.sandwich.getOrNull

class RemoteMealDataSource(
    val service: NeisService
) : MealDataSource {

    override suspend fun read(
        school: School,
        date: String,
        type: String
    ): MealResponse? {
        val response = school.run {
            service.getMeal(
                orgCode,
                code,
                date,
                type
            )
        }

        val data = response.getOrNull() ?: return null
        return ResponseParser.parseMealResponse(data)
    }

    override suspend fun write(school: School, date: String, type: String, meal: MealResponse) {
    }
}
