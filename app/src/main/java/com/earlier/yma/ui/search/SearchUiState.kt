package com.earlier.yma.ui.search

import com.earlier.yma.data.model.School

sealed class SearchUiState {
    object Idle : SearchUiState()

    object Loading : SearchUiState()

    data class Success(
        val schoolList: List<School>,
    ) : SearchUiState()

    data class Error(
        val exception: Exception,
    ) : SearchUiState()

    fun isLoading() = this is Loading

    fun isError() = this is Error
}