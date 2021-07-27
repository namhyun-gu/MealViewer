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
import com.earlier.yma.data.local.AppDatabase
import com.earlier.yma.data.local.CacheDao
import com.earlier.yma.data.local.MealResponseConverter
import com.earlier.yma.data.preferences.AppPreferenceStorage
import com.earlier.yma.data.preferences.PreferenceStorage
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object PersistenceModule {

    @Provides
    @Singleton
    fun provideMoshi(): Moshi {
        return Moshi.Builder()
            .addLast(KotlinJsonAdapterFactory())
            .build()
    }

    @Provides
    @Singleton
    fun provideAppDatabase(
        @ApplicationContext context: Context,
        mealResponseConverter: MealResponseConverter
    ): AppDatabase {
        return Room
            .databaseBuilder(context, AppDatabase::class.java, "app.db")
            .addTypeConverter(mealResponseConverter)
            .build()
    }

    @Provides
    @Singleton
    fun provideCacheDao(database: AppDatabase): CacheDao {
        return database.cacheDao()
    }

    @Provides
    @Singleton
    fun provideMealResponseConverter(moshi: Moshi): MealResponseConverter {
        return MealResponseConverter(moshi)
    }

    @Provides
    @Singleton
    fun provideAppPreferenceStorage(@ApplicationContext context: Context): PreferenceStorage {
        return AppPreferenceStorage(context.appPrefDataStore)
    }
}
