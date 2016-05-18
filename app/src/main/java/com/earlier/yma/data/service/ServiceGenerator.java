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

import okhttp3.OkHttpClient;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.Retrofit;

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
