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
package com.earlier.yma.ui.spash

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavController
import com.earlier.yma.ui.AppScreens

@Composable
fun Splash(
    navController: NavController,
    viewModel: SplashViewModel
) {
    LaunchedEffect(true) {
        val dest = if (viewModel.isFirstRun()) {
            AppScreens.Search.route
        } else {
            AppScreens.Main.route
        }

        navController.navigate(dest) {
            popUpTo(AppScreens.Splash.route) {
                inclusive = true
            }
        }
    }
}
