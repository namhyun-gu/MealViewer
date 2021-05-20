package com.earlier.yma.data.model

import com.squareup.moshi.Json

data class Result(
    @Json(name = "CODE") val code: String? = null,
    @Json(name = "MESSAGE") val message: String? = null,
)

data class Head(
    @Json(name = "list_total_count") val listTotalCount: Int? = null,
    @Json(name = "RESULT") val result: Result? = null,
)