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
            val response = neisService.searchSchool(keyword.toString())
            if (response.content == null || response.content.size < 2) {
                invalidResponse()
                return@launch
            }

            val schoolList = response.content[1].schoolList!!
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

    private fun invalidResponse() {
        _uiState.value = SearchUiState.Error(
            IllegalArgumentException("Invalid argument \"type\"")
        )
    }
}