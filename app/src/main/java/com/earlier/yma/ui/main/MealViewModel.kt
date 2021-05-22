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

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.earlier.yma.data.NeisService
import com.earlier.yma.data.PreferenceStorage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MealViewModel @Inject constructor(
    val stateHandle: SavedStateHandle,
    val preferenceStorage: PreferenceStorage,
    val neisService: NeisService,
) : ViewModel() {
    private val _uiState = MutableLiveData<MealUiState>(MealUiState.Loading)
    val uiState: LiveData<MealUiState> = _uiState

    fun loadData(date: String) = viewModelScope.launch {
        try {
            if (!stateHandle.contains("type")) {
                throwInvalidType()
            }

            val type = stateHandle.get<String>("type")!!
            val preference = preferenceStorage.readPreferenceOnce()
            val response = neisService.getMeal(
                orgCode = preference.orgCode,
                schoolCode = preference.schoolCode,
                date,
                type
            )

            if (!response.isValid) {
                throwInvalidResponse()
            }

            val meal = response.content!![1].mealList!!.first()
            _uiState.value = MealUiState.Success(meal)
        } catch (e: Exception) {
            _uiState.value = MealUiState.Error(e)
        }
    }

    private fun throwInvalidType() {
        throw IllegalArgumentException("Invalid argument \"type\"")
    }

    private fun throwInvalidResponse() {
        throw IllegalArgumentException("Invalid response")
    }
}
