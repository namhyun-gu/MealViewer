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
package com.earlier.yma.ui.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.earlier.yma.data.NeisService
import com.earlier.yma.data.PreferenceModel
import com.earlier.yma.data.PreferenceStorage
import com.earlier.yma.data.model.School
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val neisService: NeisService,
    private val preferenceStorage: PreferenceStorage,
) : ViewModel() {
    private val _uiState = MutableLiveData<SearchUiState>(SearchUiState.Idle)
    val uiState: LiveData<SearchUiState> = _uiState

    fun search(keyword: CharSequence) = viewModelScope.launch {
        try {
            _uiState.value = SearchUiState.Loading
            val response = neisService.searchSchool(keyword.toString())
            if (!response.isValid) {
                throwInvalidResponse()
            }

            val schoolList = response.content!![1].schoolList!!
            _uiState.value = SearchUiState.Success(schoolList)
        } catch (e: Exception) {
            _uiState.value = SearchUiState.Error(e)
        }
    }

    fun saveSchool(school: School) = viewModelScope.launch {
        preferenceStorage.writePreference(
            PreferenceModel(
                schoolCode = school.code,
                schoolName = school.name,
                schoolKind = school.kind,
                orgCode = school.orgCode,
                orgName = school.orgName,
            )
        )
    }

    private fun throwInvalidResponse() {
        throw IllegalArgumentException("Invalid response")
    }
}
