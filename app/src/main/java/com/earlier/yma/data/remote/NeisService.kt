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

import com.skydoves.sandwich.ApiResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface NeisService {
    @GET("/hub/schoolInfo")
    suspend fun search(
        @Query("SCHUL_NM") name: String,
        @Query("pIndex") page: Int = 1,
        @Query("pSize") size: Int = 100,
    ): ApiResponse<String>

    @GET("/hub/mealServiceDietInfo")
    suspend fun getMeal(
        @Query("ATPT_OFCDC_SC_CODE") orgCode: String,
        @Query("SD_SCHUL_CODE") schoolCode: String,
        @Query("MLSV_YMD") date: String,
        @Query("MMEAL_SC_CODE") type: String,
        @Query("pIndex") page: Int = 1,
        @Query("pSize") size: Int = 100,
    ): ApiResponse<String>
}
