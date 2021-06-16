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
package com.earlier.yma.ui.main

import androidx.annotation.StringRes
import com.earlier.yma.R
import com.earlier.yma.data.MealResponse

enum class MealType(val value: String, @StringRes val stringResId: Int) {
    Lunch("2", R.string.type_lunch),
    Dinner("3", R.string.type_dinner)
}

sealed class MainUiState {
    object Loading : MainUiState()

    data class Success(
        val content: MealResponse.Meal
    ) : MainUiState()

    data class Error(
        val exception: Throwable
    ) : MainUiState()
}
