package com.earlier.yma.data.model

import com.squareup.moshi.Json

data class MealResponse(
    @Json(name = "mealServiceDietInfo") val content: List<MealContent>? = null,
)

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