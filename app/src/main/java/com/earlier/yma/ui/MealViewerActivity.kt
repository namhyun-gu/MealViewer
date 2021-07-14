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
package com.earlier.yma.ui

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.earlier.yma.ui.main.Main
import com.earlier.yma.ui.main.MainViewModel
import com.earlier.yma.ui.search.Search
import com.earlier.yma.ui.search.SearchViewModel
import com.earlier.yma.ui.settings.Settings
import com.earlier.yma.ui.spash.Splash
import com.earlier.yma.ui.spash.SplashViewModel
import com.earlier.yma.ui.theme.MealViewerTheme
import dagger.hilt.android.AndroidEntryPoint

enum class AppScreens {
    Splash,
    Main,
    Search,
    Settings
}

@AndroidEntryPoint
class MealViewerActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            App()
        }
    }
}

@Composable
fun App() {
    MealViewerTheme {
        val navController = rememberNavController()
        NavHost(
            navController = navController,
            startDestination = AppScreens.Splash.name
        ) {
            composable(AppScreens.Splash.name) {
                val viewModel = hiltViewModel<SplashViewModel>()
                Splash(navController, viewModel)
            }
            composable(AppScreens.Main.name) {
                val viewModel = hiltViewModel<MainViewModel>()
                Main(navController, viewModel)
            }
            composable(AppScreens.Search.name) {
                val viewModel = hiltViewModel<SearchViewModel>()
                Search(navController, viewModel)
            }
            composable(AppScreens.Settings.name) {
                Settings(navController)
            }
        }
    }
}
