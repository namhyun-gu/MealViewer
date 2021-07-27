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
package com.earlier.yma

import android.app.Application
import android.content.Context
import androidx.datastore.preferences.preferencesDataStore
import com.earlier.yma.util.GlobalResponseOperator
import com.skydoves.sandwich.SandwichInitializer
import dagger.hilt.android.HiltAndroidApp

val Context.appPrefDataStore by preferencesDataStore("app")

@HiltAndroidApp
class MealViewerApp : Application() {

    override fun onCreate() {
        super.onCreate()
        SandwichInitializer.sandwichOperator = GlobalResponseOperator<Any>()
    }
}
