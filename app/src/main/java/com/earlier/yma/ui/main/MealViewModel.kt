package com.earlier.yma.ui.main

import androidx.lifecycle.*
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

    init {
        if (!stateHandle.contains("type")) {
            invalidType()
        }
    }

    fun loadData(date: String) = viewModelScope.launch {
        try {
            val type = stateHandle.get<String>("type")!!
            val preference = preferenceStorage.readPreferenceOnce()
            val response = neisService.getMeal(
                orgCode = preference.orgCode,
                schoolCode = preference.schoolCode,
                date,
                type
            )

            if (response.content == null || response.content.size < 2) {
                invalidResponse()
                return@launch
            }

            val meal = response.content[1].mealList!!.first()
            _uiState.value = MealUiState.Success(meal)
        } catch (e: Exception) {
            _uiState.value = MealUiState.Error(e)
        }
    }

    private fun invalidType() {
        _uiState.value = MealUiState.Error(
            IllegalArgumentException("Invalid argument \"type\"")
        )
    }

    private fun invalidResponse() {
        _uiState.value = MealUiState.Error(
            IllegalArgumentException("Invalid argument \"type\"")
        )
    }
}