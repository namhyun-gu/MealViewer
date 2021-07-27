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

import com.earlier.yma.data.MealDataSource
import com.earlier.yma.data.MealRepository
import com.earlier.yma.data.MealRepositoryImpl
import com.earlier.yma.data.local.CacheDao
import com.earlier.yma.data.local.LocalMealDataSource
import com.earlier.yma.data.remote.NeisService
import com.earlier.yma.data.remote.RemoteMealDataSource
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataModule {
    @Provides
    @Singleton
    @LocalSource
    fun provideLocalMealDataSource(
        cacheDao: CacheDao,
    ): MealDataSource {
        return LocalMealDataSource(cacheDao)
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
}
