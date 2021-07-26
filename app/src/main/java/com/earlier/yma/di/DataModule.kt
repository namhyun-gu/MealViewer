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
import androidx.room.Room
import com.earlier.yma.appPrefDataStore
import com.earlier.yma.data.MealDataSource
import com.earlier.yma.data.MealRepository
import com.earlier.yma.data.MealRepositoryImpl
import com.earlier.yma.data.local.AppDatabase
import com.earlier.yma.data.local.LocalMealDataSource
import com.earlier.yma.data.preferences.AppPreferenceStorage
import com.earlier.yma.data.preferences.PreferenceStorage
import com.earlier.yma.data.remote.NeisService
import com.earlier.yma.data.remote.RemoteMealDataSource
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.create

@Module
@InstallIn(SingletonComponent::class)
object DataModule {
    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "app-db"
        ).build()
    }

    @Provides
    @Singleton
    fun provideNeisService(): NeisService {
        val client = OkHttpClient.Builder()
            .addInterceptor(
                HttpLoggingInterceptor().apply {
                    level = HttpLoggingInterceptor.Level.BODY
                }
            )
            .addInterceptor { chain ->
                val request = chain.request()
                val newUrl =
                    request
                        .url
                        .newBuilder()
                        .addQueryParameter("TYPE", "json")
                        .addQueryParameter("KEY", "39f312e68dea4568a6a1167bb98ec38c")
                        .build()
                val newRequest = request.newBuilder().url(newUrl).build()
                chain.proceed(newRequest)
            }
            .build()

        return Retrofit.Builder()
            .baseUrl("https://open.neis.go.kr/")
            .client(client)
            .addConverterFactory(ScalarsConverterFactory.create())
            .build()
            .create()
    }

    @Provides
    @Singleton
    @LocalSource
    fun provideLocalMealDataSource(
        db: AppDatabase,
    ): MealDataSource {
        return LocalMealDataSource(db.cacheDao())
    }

    @Provides
    @Singleton
    @RemoteSource
    fun provideRemoteMealDataSource(
        service: NeisService,
    ): MealDataSource {
        return RemoteMealDataSource(service)
    }

    @Provides
    @Singleton
    fun provideMealRepository(
        @LocalSource localSource: MealDataSource,
        @RemoteSource remoteSource: MealDataSource,
    ): MealRepository {
        return MealRepositoryImpl(
            localSource,
            remoteSource
        )
    }

    @Provides
    @Singleton
    fun provideAppPreferenceStorage(@ApplicationContext context: Context): PreferenceStorage {
        return AppPreferenceStorage(context.appPrefDataStore)
    }
}
