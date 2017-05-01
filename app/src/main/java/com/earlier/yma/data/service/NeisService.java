/*
 * Copyright 2016 Namhyun, Gu
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.earlier.yma.data.service;

import android.support.annotation.IntRange;

import com.earlier.yma.data.SearchResult;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface  NeisService {
    String BASE_URL = "http://stu.%s";
    
    @GET("/sts_sci_md01_001.do")
    Call<String> weeklyMeal(@Query("schulCode") String schulCode,
            @Query("schulCrseScCode") String schulCrseScCode,
            @Query("schulKndScCode") String schulKndScCode,
            @Query("schMmealScCode") @IntRange(from = 1, to = 3) int schMmealScCode,
            @Query("schYmd") String requestDate);

    @GET("/spr_ccm_cm01_100.do")
    Call<SearchResult> searchSchool(@Query("kraOrgNm") String query);
}
