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

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.earlier.yma.data.School
import com.earlier.yma.data.SearchSource
import com.earlier.yma.data.preferences.PreferenceStorage
import com.earlier.yma.data.preferences.UserPreferences
import com.earlier.yma.data.remote.MealViewerService
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
@HiltViewModel
class SearchViewModel @Inject constructor(
    private val mealViewerService: MealViewerService,
    private val preferenceStorage: PreferenceStorage,
) : ViewModel() {
    private val _uiState = MutableStateFlow<SearchUiState>(SearchUiState.Idle)
    val uiState: Flow<SearchUiState> = _uiState

    private val _uiEvent = MutableSharedFlow<SearchUiEvent>(replay = 0)
    val uiEvent: SharedFlow<SearchUiEvent> = _uiEvent

    fun search(keyword: CharSequence) {
        val pagingFlow = Pager(PagingConfig(100)) {
            SearchSource(mealViewerService, keyword.toString())
        }.flow

        _uiState.value = SearchUiState.Requested(
            keyword = keyword.toString(),
            schoolPagingData = pagingFlow,
        )
    }

    fun updateFilter(filter: Set<String>) {
        val state = _uiState.value
        if (state is SearchUiState.Requested) {
            _uiState.value = state.copy(
                filterOrg = filter
            )
        }
    }

    fun saveSchool(school: School) = viewModelScope.launch {
        preferenceStorage.writePreference(
            UserPreferences(
                school = School(
                    code = school.code,
                    name = school.name,
                    kind = school.kind,
                    orgCode = school.orgCode,
                    orgName = school.orgName,
                )
            )
        )

        _uiEvent.emit(SearchUiEvent.SchoolSaved)
    }
}
