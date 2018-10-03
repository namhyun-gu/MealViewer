package com.earlier.yma.data.service;

import androidx.annotation.IntRange;
import com.earlier.yma.data.SearchResult;
import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface NeisService {

  String BASE_URL = "http://stu.%s";

  String SEARCH_BASE_URL = "http://par.%s";

  @GET("/sts_sci_md01_001.do")
  Observable<String> weeklyMeal(@Query("schulCode") String schulCode,
      @Query("schulCrseScCode") String schulCrseScCode,
      @Query("schulKndScCode") String schulKndScCode,
      @Query("schMmealScCode") @IntRange(from = 1, to = 3) int schMmealScCode,
      @Query("schYmd") String requestDate);

  @GET("/spr_ccm_cm01_100.do")
  Observable<SearchResult> searchSchool(@Query("kraOrgNm") String query);

}
