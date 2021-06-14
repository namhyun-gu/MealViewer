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
package com.earlier.yma.di

import android.content.Context
import com.earlier.yma.appPrefDataStore
import com.earlier.yma.data.AuthInterceptor
import com.earlier.yma.data.NeisService
import com.earlier.yma.data.preferences.AppPreferenceStorage
import com.earlier.yma.data.preferences.PreferenceStorage
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.create
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataModule {
    @Provides
    @Singleton
    fun provideNeisService(): NeisService {
        val client = OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor())
            .build()

        val moshi = Moshi.Builder()
            .addLast(KotlinJsonAdapterFactory())
            .build()

        return Retrofit.Builder()
            .baseUrl("https://open.neis.go.kr")
            .client(client)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create()
    }

    @Provides
    @Singleton
    fun provideAppPreferenceStorage(@ApplicationContext context: Context): PreferenceStorage {
        return AppPreferenceStorage(context.appPrefDataStore)
    }
}
