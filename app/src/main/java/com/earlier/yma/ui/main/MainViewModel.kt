package com.earlier.yma.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.earlier.yma.util.DateUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.*
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor() : ViewModel() {
    private val _uiState = MutableLiveData(MainUiState())
    val uiState: LiveData<MainUiState> = _uiState

    fun updateDate(date: Date) {
        _uiState.value = _uiState.value?.copy(
            date = DateUtils.formatDate(date)
        )
    }

    fun updateType(type: String) {
        _uiState.value = _uiState.value?.copy(
            selectedType = type
        )
    }
}