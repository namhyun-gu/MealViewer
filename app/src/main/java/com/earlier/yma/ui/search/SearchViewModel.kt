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
import com.earlier.yma.util.Event
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

    private val _loadMoreError = MutableLiveData<Event<Unit>>()
    val loadMoreError: LiveData<Event<Unit>> = _loadMoreError

    fun search(keyword: CharSequence) = viewModelScope.launch {
        _uiState.value = SearchUiState.Loading

        val (list, exception) = requestSearch(keyword.toString())
        if (exception != null) {
            _uiState.value = SearchUiState.Error(exception)
        } else {
            _uiState.value = SearchUiState.Success(
                keyword = keyword.toString(),
                schoolList = list,
                orgList = getOrgList(list)
            )
        }
    }

    fun loadMore() = viewModelScope.launch {
        val state = _uiState.value!!
        if (state is SearchUiState.Success) {
            val nextPage = state.page + 1
            val (nextList, exception) = requestSearch(state.keyword, nextPage)

            if (exception != null) {
                _loadMoreError.value = Event(Unit)
                return@launch
            }

            val newList = mutableListOf<School>()
            newList.addAll(state.schoolList)
            newList.addAll(nextList)

            _uiState.value = state.copy(
                schoolList = newList,
                orgList = getOrgList(newList),
                page = nextPage,
            )
        }
    }

    fun updateFilter(filter: Set<String>) {
        val state = _uiState.value!!
        if (state is SearchUiState.Success) {
            _uiState.value = state.copy(
                filterOrg = filter
            )
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

    private fun getOrgList(list: List<School>): List<String> {
        val orgSet = mutableSetOf<String>()
        list.forEach {
            orgSet.add(it.orgName)
        }
        return orgSet.toList()
    }

    private suspend fun requestSearch(
        keyword: String,
        page: Int = 1
    ): Pair<List<School>, Exception?> {
        try {
            val response = neisService.searchSchool(keyword, page)
            if (!response.isValid) {
                return emptyList<School>() to IllegalArgumentException("Invalid response")
            }

            val schoolList = response.content!![1].schoolList!!
            return schoolList to null
        } catch (e: Exception) {
            return emptyList<School>() to e
        }
    }
}
