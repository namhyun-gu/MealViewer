package com.earlier.yma.ui.main

import com.earlier.yma.data.model.Meal

sealed class MealUiState {
    object Loading : MealUiState()

    data class Success(
        val meal: Meal,
    ) : MealUiState()

    data class Error(
        val exception: Exception,
    ) : MealUiState()

    fun isLoading() = this is Loading

    fun isError() = this is Error
}