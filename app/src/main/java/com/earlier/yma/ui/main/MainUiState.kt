package com.earlier.yma.ui.main

import com.earlier.yma.util.Constants
import com.earlier.yma.util.DateUtils

data class MainUiState(
    val date: String = DateUtils.getToday(),
    val selectedType: String = Constants.TYPE_LUNCH,
)
