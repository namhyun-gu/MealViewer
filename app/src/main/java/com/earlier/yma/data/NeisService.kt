package com.earlier.yma.data

import com.earlier.yma.data.model.MealResponse
import com.earlier.yma.data.model.SearchResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface NeisService {
    @GET("/hub/schoolInfo")
    suspend fun searchSchool(
        @Query("SCHUL_NM") name: String,
        @Query("pIndex") page: Int = 1,
        @Query("pSize") size: Int = 100,
    ): SearchResponse

    @GET("/hub/mealServiceDietInfo")
    suspend fun getMeal(
        @Query("ATPT_OFCDC_SC_CODE") orgCode: String,
        @Query("SD_SCHUL_CODE") schoolName: String,
        @Query("MLSV_YMD") date: String,
        @Query("MMEAL_SC_CODE") code: String = "2",
        @Query("pIndex") page: Int = 1,
        @Query("pSize") size: Int = 100,
    ): MealResponse

    @GET("/hub/mealServiceDietInfo")
    suspend fun getMealRange(
        @Query("ATPT_OFCDC_SC_CODE") orgCode: String,
        @Query("SD_SCHUL_CODE") schoolName: String,
        @Query("MLSV_FROM_YMD") from: String,
        @Query("MLSV_TO_YMD") to: String,
        @Query("MMEAL_SC_CODE") code: String = "2",
        @Query("pIndex") page: Int = 1,
        @Query("pSize") size: Int = 100,
    ): MealResponse
}