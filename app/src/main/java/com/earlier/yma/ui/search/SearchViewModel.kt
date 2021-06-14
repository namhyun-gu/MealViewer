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
import com.earlier.yma.data.SearchResponse
import com.earlier.yma.data.preferences.PreferenceStorage
import com.earlier.yma.data.preferences.UserPreferences
import com.earlier.yma.util.Event
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import retrofit2.HttpException
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val neisService: NeisService,
    private val preferenceStorage: PreferenceStorage,
) : ViewModel() {
    private val _uiState = MutableLiveData<SearchUiState>()
    val uiState: LiveData<SearchUiState> = _uiState

    private val _loadMoreError = MutableLiveData<Event<Unit>>()
    val loadMoreError: LiveData<Event<Unit>> = _loadMoreError

    fun search(keyword: CharSequence) {
        val exceptionHandler = CoroutineExceptionHandler { _, exception ->
            _uiState.value = SearchUiState.Error(exception)
        }

        _uiState.value = SearchUiState.Loading
        tryToSearch(exceptionHandler, keyword)
    }

    private fun tryToSearch(exceptionHandler: CoroutineExceptionHandler, keyword: CharSequence) =
        viewModelScope.launch(exceptionHandler) {
            val schoolList = requestSearch(keyword.toString())
            _uiState.value = SearchUiState.Success(
                keyword = keyword.toString(),
                schoolList = schoolList,
                orgList = getOrgList(schoolList)
            )
        }

    fun loadMore() {
        val exceptionHandler = CoroutineExceptionHandler { _, _ ->
            _loadMoreError.value = Event(Unit)
        }

        tryToLoadMore(exceptionHandler)
    }

    private fun tryToLoadMore(exceptionHandler: CoroutineExceptionHandler) =
        viewModelScope.launch(exceptionHandler) {
            val state = _uiState.value!!
            if (state is SearchUiState.Success) {
                val nextPage = state.page + 1
                val nextList = requestSearch(state.keyword, nextPage)

                val newList = mutableListOf<SearchResponse.School>()
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
    }

    private fun getOrgList(list: List<SearchResponse.School>): List<String> {
        val orgSet = mutableSetOf<String>()
        list.forEach {
            orgSet.add(it.orgName)
        }
        return orgSet.toList()
    }

    @Throws(HttpException::class, IllegalArgumentException::class)
    private suspend fun requestSearch(
        keyword: String,
        page: Int = 1
    ): List<SearchResponse.School> {
        val response = neisService.searchSchool(keyword, page)
        if (!response.isValid) {
            throw IllegalArgumentException("Invalid response")
        }
        return response.content!![1].schoolList!!
    }
}
