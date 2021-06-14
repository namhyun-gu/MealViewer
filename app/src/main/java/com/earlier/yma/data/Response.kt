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

import com.squareup.moshi.Json

sealed class Response<T>(open val content: List<T>? = null) {
    val isValid: Boolean
        get() = content != null && content!!.size >= 2
}

data class Result(
    @Json(name = "CODE") val code: String? = null,
    @Json(name = "MESSAGE") val message: String? = null,
)

data class Head(
    @Json(name = "list_total_count") val listTotalCount: Int? = null,
    @Json(name = "RESULT") val result: Result? = null,
)

data class MealResponse(
    @Json(name = "mealServiceDietInfo") override val content: List<MealContent>? = null,
) : Response<MealResponse.MealContent>(content) {

    data class MealContent(
        @Json(name = "head") val head: List<Head>? = null,
        @Json(name = "row") val mealList: List<Meal>? = null,
    )

    data class Meal(
        /** 시도교육청코드 */
        @Json(name = "ATPT_OFCDC_SC_CODE") val atptOfcdcScCode: String,
        /** 시도교육청명 */
        @Json(name = "ATPT_OFCDC_SC_NM") val atptOfcdcScNm: String,
        /** 표준학교코드 */
        @Json(name = "SD_SCHUL_CODE") val sdSchulCode: String,
        /** 학교명 */
        @Json(name = "SCHUL_NM") val schulNm: String,
        /** 식사코드 */
        @Json(name = "MMEAL_SC_CODE") val mmealScCode: String,
        /** 식사명 */
        @Json(name = "MMEAL_SC_NM") val mmealScNm: String,
        /** 급식일자 */
        @Json(name = "MLSV_YMD") val mlsvYmd: String,
        /** 급식인원수 */
        @Json(name = "MLSV_FGR") val mlsvFgr: String,
        /** 요리명 */
        @Json(name = "DDISH_NM") val ddishNm: String,
        /** 원산지정보 */
        @Json(name = "ORPLC_INFO") val orplcInfo: String,
        /** 칼로리정보 */
        @Json(name = "CAL_INFO") val calInfo: String,
        /** 영양정보 */
        @Json(name = "NTR_INFO") val ntrInfo: String,
        /** 급식시작일자 */
        @Json(name = "MLSV_FROM_YMD") val mlsvFromYmd: String,
        /** 급식종료일자 */
        @Json(name = "MLSV_TO_YMD") val mlsvToYmd: String,
    )
}

data class SearchResponse(
    @Json(name = "schoolInfo") override val content: List<SearchContent>? = null,
) : Response<SearchResponse.SearchContent>(content) {

    data class SearchContent(
        @Json(name = "head") val head: List<Head>? = null,
        @Json(name = "row") val schoolList: List<School>? = null,
    )

    data class School(
        /** 시도교육청코드 */
        @Json(name = "ATPT_OFCDC_SC_CODE") val orgCode: String,
        /** 시도교육청명 */
        @Json(name = "ATPT_OFCDC_SC_NM") val orgName: String,
        /** 표준학교코드 */
        @Json(name = "SD_SCHUL_CODE") val code: String,
        /** 학교명 */
        @Json(name = "SCHUL_NM") val name: String,
        /** 학교종류명 */
        @Json(name = "SCHUL_KND_SC_NM") val kind: String,
        /** 소재지명 */
        @Json(name = "LCTN_SC_NM") val location: String,
    )
}
