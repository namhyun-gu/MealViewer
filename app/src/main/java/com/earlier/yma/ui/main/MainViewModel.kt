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
import com.earlier.yma.data.preferences.PreferenceStorage
import com.earlier.yma.data.remote.MealViewerService
import com.earlier.yma.util.DateUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.Date
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class MainViewModel @Inject constructor(
    val preferenceStorage: PreferenceStorage,
    val mealViewerService: MealViewerService,
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
            mealViewerService.getMeal(
                orgCode = school.orgCode,
                schoolCode = school.code,
                type = type.value,
                date = DateUtils.formatDate(date)
            )
        }

        _uiState.value = MainUiState.Success(content = meal)
    }
}
