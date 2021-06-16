package com.earlier.yma.ui.main

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.ContentAlpha
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.DarkMode
import androidx.compose.material.icons.rounded.Event
import androidx.compose.material.icons.rounded.LightMode
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.earlier.yma.R
import com.earlier.yma.data.MealResponse
import com.earlier.yma.ui.base.Center
import com.earlier.yma.util.parseAllergyInfo
import com.google.accompanist.pager.ExperimentalPagerApi

// TODO: 날짜 변경 기능 추가
// TODO: BottomNavigation 추가 / Rotation 애니메이션이 끊기는 문제 고치기
// TODO: 영양소 정보 표시
@Composable
fun MainActivityContent(
    modifier: Modifier = Modifier
) {
    val viewModel: MainViewModel = viewModel()
    val uiState: MainUiState by viewModel.uiState.collectAsState(MainUiState.Loading)
    val uiEvent: MainUiEvent by viewModel.uiEvent.collectAsState(MainUiEvent.None)

    Scaffold(
        modifier = modifier,
        topBar = {
            MainTopBar()
        }
    ) {
        Center(
            modifier = Modifier.fillMaxSize()
        ) {
            MainContent(
                uiState = uiState
            )
        }
    }
}

@Composable
fun MainTopBar(
    modifier: Modifier = Modifier,
) {
    val isNight = false

    Column {
        Box(
            modifier = Modifier.padding(
                start = 16.dp,
                top = 16.dp,
                bottom = 8.dp,
            )
        ) {
            StateIcon()
        }
        TopAppBar(
            title = {
                Text(
                    stringResource(R.string.type_lunch),
                    style = MaterialTheme.typography.h5.copy(
                        fontWeight = FontWeight.Black
                    )
                )
            },
            backgroundColor = Color.Transparent,
            elevation = 0.dp,
            actions = {
                IconButton(onClick = { /*TODO*/ }) {
                    Icon(Icons.Rounded.Event, null)
                }
            }
        )
    }
}

@Composable
fun StateIcon(
    isNight: Boolean = false
) {
    if (isNight) {
        Icon(
            Icons.Rounded.DarkMode,
            contentDescription = null,
            tint = Color(0xFFFFD600),
            modifier = Modifier.size(48.dp)
        )
    } else {
        Icon(
            Icons.Rounded.LightMode,
            contentDescription = null,
            tint = Color(0xFFFFD600),
            modifier = Modifier.size(48.dp)
        )
    }
}

@OptIn(ExperimentalPagerApi::class)
@Composable
fun MainContent(
    modifier: Modifier = Modifier,
    uiState: MainUiState,
) {
    when (uiState) {
        MainUiState.Loading -> {
            CircularProgressIndicator()
        }
        is MainUiState.Success -> {
            Column {
                MealContent(
                    modifier = Modifier.fillMaxSize(),
                    meal = uiState.content
                )
            }
        }
        is MainUiState.Error -> {
            val errorMessage = if (uiState.exception is IllegalArgumentException) {
                stringResource(R.string.msg_meal_empty)
            } else {
                stringResource(R.string.msg_meal_error)
            }
            Text(errorMessage, style = MaterialTheme.typography.body1)
        }
    }
}

@Composable
fun MealContent(
    modifier: Modifier = Modifier,
    meal: MealResponse.Meal
) {
    Column(modifier = modifier) {
        meal.foodList.forEach { food ->
            FoodItem(
                food = food
            )
        }
    }
}

@Composable
fun FoodItem(
    modifier: Modifier = Modifier,
    food: String
) {
    val allergyNames = stringArrayResource(R.array.allergy_info)
    val (foodName, allergyInfo) = parseAllergyInfo(food)
    val allergyMessage = allergyInfo.map { allergyNames[it] }.joinToString()

    Surface(
        modifier = modifier,
        shape = MaterialTheme.shapes.medium
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(foodName, fontWeight = FontWeight.Bold)
            if (allergyInfo.isNotEmpty()) {
                CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
                    Text(allergyMessage, style = MaterialTheme.typography.body2)
                }
            }
        }
    }
}