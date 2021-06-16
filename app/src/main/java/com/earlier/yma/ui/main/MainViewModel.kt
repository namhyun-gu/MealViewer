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

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.earlier.yma.data.MealResponse
import com.earlier.yma.data.NeisService
import com.earlier.yma.data.preferences.PreferenceStorage
import com.earlier.yma.util.DateUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    val preferenceStorage: PreferenceStorage,
    val neisService: NeisService,
) : ViewModel() {
    private val _uiState = MutableStateFlow<MainUiState>(MainUiState.Loading)
    val uiState: StateFlow<MainUiState> = _uiState

    private val _uiEvent = MutableSharedFlow<MainUiEvent>(replay = 0)
    val uiEvent: SharedFlow<MainUiEvent> = _uiEvent

    init {
        val initType = MealType.Lunch
        val initDate = Date()

        loadContent(initType, initDate)
    }

    fun loadContent(type: MealType, date: Date) = viewModelScope.launch {
        try {
            _uiState.value = MainUiState.Loading
            // TODO: 데이터베이스 캐시 기능 추가
            tryToLoadContent(type, date)
        } catch (e: Exception) {
            _uiState.value = MainUiState.Error(e)
        }
    }

    private suspend fun tryToLoadContent(type: MealType, date: Date) {
        val preference = preferenceStorage.readPreferenceOnce()
        val meal = preference.run {
            requestMeal(
                orgCode = school.orgCode,
                schoolCode = school.schoolCode,
                type = type.value,
                date = DateUtils.formatDate(date)
            )
        }

        _uiState.value = MainUiState.Success(
            type = type,
            date = date,
            content = meal
        )
    }

    @Throws(HttpException::class, IllegalArgumentException::class)
    private suspend fun requestMeal(
        orgCode: String,
        schoolCode: String,
        type: String,
        date: String,
    ): MealResponse.Meal {
        val response = neisService.getMeal(
            orgCode = orgCode,
            schoolCode = schoolCode,
            type = type,
            date = date,
        )
        if (!response.isValid) {
            throw IllegalArgumentException("Invalid response")
        }

        return response.content!![1].mealList!!.first()
    }

    fun updateDate(date: Date) {
        val state = _uiState.value
        if (state is MainUiState.Success) {
            loadContent(state.type, date)
        }
    }

    fun updateType(type: MealType) {
        val state = _uiState.value
        if (state is MainUiState.Success) {
            loadContent(type, state.date)
        }
    }
}
