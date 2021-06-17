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
import com.earlier.yma.data.SearchResponse
import com.earlier.yma.data.SearchSource
import com.earlier.yma.data.preferences.PreferenceStorage
import com.earlier.yma.data.preferences.UserPreferences
import com.earlier.yma.data.remote.NeisService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
@HiltViewModel
class SearchViewModel @Inject constructor(
    private val neisService: NeisService,
    private val preferenceStorage: PreferenceStorage,
) : ViewModel() {
    private val _uiState = MutableStateFlow<SearchUiState>(SearchUiState.Idle)
    val uiState: Flow<SearchUiState> = _uiState

    private val _uiEvent = MutableSharedFlow<SearchUiEvent>(replay = 0)
    val uiEvent: SharedFlow<SearchUiEvent> = _uiEvent

    fun search(keyword: CharSequence) {
        val pagingFlow = Pager(PagingConfig(100)) {
            SearchSource(neisService, keyword.toString())
        }.flow

        _uiState.value = SearchUiState.Requested(
            keyword = keyword.toString(),
            schoolPagingData = pagingFlow,
        )
    }

//    fun search(keyword: CharSequence) = viewModelScope.launch {
//        try {
//            _uiState.value = SearchUiState.Loading
//            tryToSearch(keyword)
//        } catch (e: Exception) {
//            _uiState.value = SearchUiState.Error(e)
//        }
//    }

//    private suspend fun tryToSearch(keyword: CharSequence) {
//        val schoolList = requestSearch(keyword.toString())
//        _uiState.value = SearchUiState.Success(
//            keyword = keyword.toString(),
//            schoolList = schoolList,
//            orgList = getOrgList(schoolList)
//        )
//    }
//
//    fun loadMore() = viewModelScope.launch {
//        try {
//            _uiEvent.emit(SearchUiEvent.None)
//            tryToLoadMore()
//        } catch (e: Exception) {
//            _uiEvent.emit(SearchUiEvent.LoadMoreError)
//        }
//    }
//
//    private suspend fun tryToLoadMore() {
//        val state = _uiState.value
//        if (state is SearchUiState.Success) {
//            val nextPage = state.page + 1
//            val nextList = requestSearch(state.keyword, nextPage)
//
//            val newList = mutableListOf<SearchResponse.School>()
//            newList.addAll(state.schoolList)
//            newList.addAll(nextList)
//
//            _uiState.value = state.copy(
//                schoolList = newList,
//                orgList = getOrgList(newList),
//                page = nextPage,
//            )
//        }
//    }

    fun updateFilter(filter: Set<String>) {
        val state = _uiState.value
        if (state is SearchUiState.Requested) {
            _uiState.value = state.copy(
                filterOrg = filter
            )
        }
    }

    fun saveSchool(school: SearchResponse.School) = viewModelScope.launch {
        preferenceStorage.writePreference(
            UserPreferences(
                school = UserPreferences.School(
                    schoolCode = school.code,
                    schoolName = school.name,
                    schoolKind = school.kind,
                    orgCode = school.orgCode,
                    orgName = school.orgName,
                )
            )
        )

        _uiEvent.emit(SearchUiEvent.SchoolSaved)
    }

//    private fun getOrgList(list: List<SearchResponse.School>): List<String> {
//        val orgSet = mutableSetOf<String>()
//        list.forEach {
//            orgSet.add(it.orgName)
//        }
//        return orgSet.toList()
//    }
//
//    @Throws(HttpException::class, IllegalArgumentException::class)
//    private suspend fun requestSearch(
//        keyword: String,
//        page: Int = 1
//    ): List<SearchResponse.School> {
//        val response = neisService.searchSchool(keyword, page)
//        if (!response.isValid) {
//            throw IllegalArgumentException("Invalid response")
//        }
//        return response.content!![1].schoolList!!
//    }
}
