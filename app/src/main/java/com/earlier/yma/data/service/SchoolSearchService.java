package com.earlier.yma.data.service;

import com.earlier.yma.data.model.SearchResultObject;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by namhyun on 2015-12-14.
 */
public interface SchoolSearchService {
    @GET("spr_ccm_cm01_100.do")
    Call<SearchResultObject> search(@Query("kraOrgNm") String schoolName);
}
