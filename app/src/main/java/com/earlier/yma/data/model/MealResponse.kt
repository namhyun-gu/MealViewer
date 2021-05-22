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
