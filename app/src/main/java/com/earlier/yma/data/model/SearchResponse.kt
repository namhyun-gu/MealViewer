package com.earlier.yma.data.model

import com.squareup.moshi.Json

data class SearchResponse(
    @Json(name = "schoolInfo") val content: List<SearchContent>? = null,
)

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