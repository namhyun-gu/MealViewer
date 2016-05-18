package com.earlier.yma.data.service;

import com.earlier.yma.data.model.MealObject;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by namhyun on 2015-12-02.
 */
public interface MealService {
    @GET("macros/s/{script_id}/exec")
    Call<MealObject> execute(@Path("script_id") String scriptId,
                             @Query("countryCode") String path,
                             @Query("schulCode") String schulCode,
                             @Query("schulCrseScCode") String schulCrseScCode,
                             @Query("schulKndScCode") String schulKndScCode,
                             @Query("schMmealScCode") String schMmealScCode);
}
