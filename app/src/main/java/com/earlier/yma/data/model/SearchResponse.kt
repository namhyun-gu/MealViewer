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
