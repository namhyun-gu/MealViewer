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

import com.earlier.yma.data.model.Dish
import com.earlier.yma.data.model.MealResponse
import com.earlier.yma.data.model.Nutrition
import com.earlier.yma.data.model.Origin
import com.earlier.yma.data.model.School
import com.earlier.yma.data.model.SearchResponse
import com.earlier.yma.util.parseAllergyInfo
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

object ResponseParser {
    private const val MEAL_HEADER = "mealServiceDietInfo"
    private const val SEARCH_HEADER = "schoolInfo"

    fun parseMealResponse(response: String): MealResponse? {
        return try {
            val dataObject = parseResponse(MEAL_HEADER, response).getJSONObject(0)
            val calorie = dataObject.getString("CAL_INFO")
            val dishes = dataObject.getString("DDISH_NM").split("<br/>")
                .map { dish ->
                    val (foodName, allergy) = parseAllergyInfo(dish)
                    Dish(foodName, allergy)
                }
            val origins = dataObject.getString("ORPLC_INFO").split("<br/>")
                .map {
                    val (food, origin) = it.split(" : ")
                    Origin(food, origin)
                }
            val nutrition = dataObject.getString("NTR_INFO").split("<br/>")
                .map {
                    val (name, value) = it.split(" : ")
                    Nutrition(name, value.toDouble())
                }

            MealResponse(calorie, dishes, origins, nutrition)
        } catch (e: JSONException) {
            null
        }
    }

    fun parseSearchResponse(response: String): SearchResponse? {
        return try {
            val dataArray = parseResponse(SEARCH_HEADER, response)
            val schoolList = mutableListOf<School>()

            for (idx in 0 until dataArray.length()) {
                val dataObject = dataArray.getJSONObject(idx)

                val orgCode = dataObject.getString("ATPT_OFCDC_SC_CODE")
                val orgName = dataObject.getString("ATPT_OFCDC_SC_NM")
                val code = dataObject.getString("SD_SCHUL_CODE")
                val name = dataObject.getString("SCHUL_NM")
                val kind = dataObject.getString("SCHUL_KND_SC_NM")
                val location = dataObject.getString("LCTN_SC_NM")

                schoolList += School(orgCode, orgName, code, name, kind, location)
            }

            SearchResponse(schoolList)
        } catch (e: JSONException) {
            null
        }
    }

    private fun parseResponse(header: String, content: String): JSONArray {
        val jsonObject = JSONObject(content)
        val infoArray = jsonObject.getJSONArray(header)
        val infoObject = infoArray.getJSONObject(1)
        return infoObject.getJSONArray("row")
    }
}
