package com.earlier.yma.data.service;

import okhttp3.OkHttpClient;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.Retrofit;

/**
 * Created by namhyun on 2015-12-02.
 */
public class ServiceGenerator {
    public static final String MEAL_BASE_URL = "https://script.google.com/";
    public static final String SEARCH_BASE_URL = "http://hes.%s/";
    private static OkHttpClient httpClient = new OkHttpClient();

    public static <S> S createService(Class<S> serviceClass) {
        return createService(serviceClass, "");
    }

    public static <S> S createService(Class<S> serviceClass, String... param) {
        String baseUrl = null;
        if (MealService.class.isAssignableFrom(serviceClass)) {
            baseUrl = MEAL_BASE_URL;
        } else if (SchoolSearchService.class.isAssignableFrom(serviceClass)) {
            if (param != null)
                baseUrl = String.format(SEARCH_BASE_URL, param[0]);
        }
        Retrofit.Builder builder =
                new Retrofit.Builder()
                        .baseUrl(baseUrl)
                        .addConverterFactory(GsonConverterFactory.create());
        Retrofit retrofit = builder.client(httpClient).build();
        return retrofit.create(serviceClass);
    }
}
