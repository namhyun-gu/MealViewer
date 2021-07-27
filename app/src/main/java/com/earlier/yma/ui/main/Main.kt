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
package com.earlier.yma.ui.main

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.AlertDialog
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Button
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.ContentAlpha
import androidx.compose.material.Divider
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.ListItem
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.contentColorFor
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Event
import androidx.compose.material.icons.rounded.LocalFireDepartment
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.earlier.yma.R
import com.earlier.yma.data.model.Dish
import com.earlier.yma.data.model.MealResponse
import com.earlier.yma.data.model.Nutrition
import com.earlier.yma.ui.AppScreens
import com.earlier.yma.ui.common.AppBar
import com.earlier.yma.ui.common.Center
import com.earlier.yma.ui.theme.MealViewerTheme
import com.earlier.yma.util.DateUtils
import com.earlier.yma.util.EmptyResponseException
import com.vanpra.composematerialdialogs.MaterialDialog
import com.vanpra.composematerialdialogs.buttons
import com.vanpra.composematerialdialogs.datetime.datepicker.datepicker
import java.util.Date

@Composable
fun Main(
    navController: NavController,
    viewModel: MainViewModel,
) {
    val uiState: MainUiState by viewModel.uiState.collectAsState(MainUiState.Loading)
    val uiEvent: MainUiEvent by viewModel.uiEvent.collectAsState(MainUiEvent.None)

    var currentType by rememberSaveable { mutableStateOf(MealType.Lunch) }
    var currentDate by rememberSaveable { mutableStateOf(Date()) }

    val dateDialog = remember { MaterialDialog() }

    dateDialog.build {
        datepicker(
            title = stringResource(R.string.dialog_select_date),
            initialDate = DateUtils.convertLocalDate(currentDate),
        ) { newDate ->
            currentDate = DateUtils.convertDate(newDate)
            viewModel.loadContent(currentType, currentDate)
        }
        buttons {
            positiveButton(res = R.string.action_ok)
            negativeButton(res = R.string.action_cancel)
        }
    }

    Scaffold(
        topBar = {
            MainTopBar(
                type = currentType,
                date = currentDate,
                onDateSelect = {
                    dateDialog.show()
                },
                onSettingSelect = {
                    navController.navigate(AppScreens.Settings.route) {
                        anim {
                            enter = android.R.anim.slide_in_left
                            exit = android.R.anim.slide_out_right
                            popEnter = android.R.anim.slide_in_left
                            popExit = android.R.anim.slide_out_right
                        }
                    }
                }
            )
        },
        bottomBar = {
            MainBottomBar(
                selectType = currentType,
                onTypeSelect = { newType ->
                    currentType = newType
                    viewModel.loadContent(currentType, currentDate)
                }
            )
        }
    ) { innerPadding ->
        MainContent(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            uiState = uiState,
            onRetryClick = {
                viewModel.loadContent(currentType, currentDate)
            }
        )
    }
}

@Composable
fun MainTopBar(
    type: MealType,
    date: Date,
    onDateSelect: () -> Unit = {},
    onSettingSelect: () -> Unit = {}
) {
    AppBar(
        title = {
            Text(
                stringResource(type.stringResId),
            )
        },
        subtitle = {
            Text(
                DateUtils.formatDate(date, "MM.dd"),
            )
        },
        expandSpace = {
            Box(
                modifier = Modifier.padding(
                    horizontal = 12.dp,
                    vertical = 16.dp
                )
            ) {
                Icon(
                    type.icon,
                    contentDescription = null,
                    modifier = Modifier
                        .size(56.dp),
                    tint = Color(0xFFFFD600),
                )
            }
        },
        actions = {
            IconButton(onClick = onDateSelect) {
                Icon(
                    Icons.Rounded.Event,
                    null,
                    tint = MaterialTheme.colors.onSurface
                )
            }
            IconButton(onClick = onSettingSelect) {
                Icon(
                    Icons.Rounded.Settings,
                    null,
                    tint = MaterialTheme.colors.onSurface
                )
            }
        },
        hideNavigationIcon = true
    )
}

@Preview(showBackground = true)
@Composable
fun MainTopBar_Preview() {
    MealViewerTheme {
        MainTopBar(
            type = MealType.Lunch,
            date = Date(),
        )
    }
}

@Composable
fun MainBottomBar(
    selectType: MealType,
    onTypeSelect: (MealType) -> Unit = {}
) {
    val types = MealType.values()

    Box(
        Modifier
            .padding(16.dp)
            .clip(MaterialTheme.shapes.large)
    ) {
        BottomNavigation {
            types.forEach { type ->
                val isSelected = (type == selectType)

                BottomNavigationItem(
                    icon = { Icon(type.icon, null) },
                    label = { Text(stringResource(id = type.stringResId)) },
                    selected = isSelected,
                    onClick = { onTypeSelect(type) },
                    alwaysShowLabel = false
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MealBottomBar_Preview() {
    MealViewerTheme {
        MainBottomBar(selectType = MealType.Breakfast)
    }
}

@Composable
fun MainContent(
    modifier: Modifier = Modifier,
    uiState: MainUiState,
    onRetryClick: () -> Unit
) {
    when (uiState) {
        is MainUiState.Success -> {
            MealContent(
                modifier = modifier,
                meal = uiState.content
            )
        }
        MainUiState.Loading -> {
            LoadingContent(
                modifier = modifier,
            )
        }
        is MainUiState.Error -> {
            ErrorContent(
                modifier = modifier,
                exception = uiState.exception,
                onRetryClick = onRetryClick
            )
        }
    }
}

@Composable
fun MealContent(
    modifier: Modifier = Modifier,
    meal: MealResponse
) {
    var openNutritionDialog by rememberSaveable { mutableStateOf(false) }

    if (openNutritionDialog) {
        NutritionDialog(nutritionList = meal.nutrition) {
            openNutritionDialog = false
        }
    }

    Column {
        Row(
            modifier = Modifier.padding(16.dp)
        ) {
            CalorieCard(
                modifier = Modifier.weight(1f),
                calorie = meal.calorie
            )
            Spacer(
                modifier = Modifier.width(8.dp)
            )
            MoreCard(
                modifier = Modifier.weight(1f).clickable {
                    openNutritionDialog = true
                }
            )
        }
        LazyColumn(modifier = modifier) {
            itemsIndexed(meal.dishes) { index, dish ->
                DishItem(dish = dish)
                if (index < meal.dishes.size - 1) {
                    Divider()
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MealContent_Preview() {
    MealViewerTheme {
        MealContent(meal = MealResponse.dummy())
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun NutritionDialog(
    nutritionList: List<Nutrition>,
    onDismissRequest: () -> Unit = {}
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        text = {
            LazyColumn {
                items(nutritionList) { nutrition ->
                    ListItem(
                        secondaryText = {
                            Text(nutrition.value.toString())
                        },
                        singleLineSecondaryText = true
                    ) {
                        Text(nutrition.name)
                    }
                }
            }
        },
        buttons = {
            Row(
                modifier = Modifier.padding(all = 8.dp).fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(onClick = onDismissRequest) {
                    Text(stringResource(id = R.string.close))
                }
            }
        }
    )
}

@Composable
fun InfoCard(
    modifier: Modifier = Modifier,
    title: @Composable () -> Unit = {},
    subtitle: @Composable () -> Unit = {},
    icon: ImageVector? = null,
    color: Color = MaterialTheme.colors.surface,
    contentColor: Color = contentColorFor(color),
) {
    Surface(
        modifier = modifier.aspectRatio(1f),
        shape = MaterialTheme.shapes.large,
        color = color,
        contentColor = contentColor
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            CompositionLocalProvider(
                LocalContentAlpha provides ContentAlpha.medium,
                LocalTextStyle provides MaterialTheme.typography.subtitle1
            ) {
                subtitle()
            }
            Spacer(modifier = Modifier.height(8.dp))
            CompositionLocalProvider(
                LocalTextStyle provides MaterialTheme.typography.h6
            ) {
                title()
            }
            Spacer(modifier = Modifier.weight(1f))
            if (icon != null) {
                Icon(
                    icon,
                    modifier = Modifier
                        .size(40.dp)
                        .align(Alignment.End),
                    contentDescription = null,
                )
            }
        }
    }
}

@Composable
fun CalorieCard(
    modifier: Modifier = Modifier,
    calorie: String
) {
    InfoCard(
        modifier = modifier,
        title = {
            Text(calorie)
        },
        subtitle = {
            Text(stringResource(id = R.string.subtitle_calorie))
        },
        icon = Icons.Rounded.LocalFireDepartment,
        color = Color(0xFFFF3F15),
        contentColor = Color.White
    )
}

@Preview(showBackground = true)
@Composable
fun CalorieCard_Preview() {
    MealViewerTheme {
        CalorieCard(
            modifier = Modifier.size(160.dp),
            calorie = "100.0 Kcal"
        )
    }
}

@Composable
fun MoreCard(
    modifier: Modifier,
) {
    InfoCard(
        modifier = modifier,
        title = {
            Text(stringResource(id = R.string.title_more))
        },
        subtitle = {
            Text(stringResource(id = R.string.subtitle_nutrition))
        },
        icon = Icons.Rounded.MoreVert,
        color = Color.LightGray,
    )
}

@Composable
fun LoadingContent(
    modifier: Modifier = Modifier
) {
    Center(modifier = modifier) {
        CircularProgressIndicator()
    }
}

@Composable
fun ErrorContent(
    modifier: Modifier = Modifier,
    exception: Throwable,
    onRetryClick: () -> Unit
) {
    val isEmptyError = exception is EmptyResponseException
    val message = if (isEmptyError) {
        stringResource(id = R.string.msg_meal_empty)
    } else {
        stringResource(id = R.string.msg_meal_error)
    }

    Center(modifier = modifier) {
        Column {
            Text(message, style = MaterialTheme.typography.body1)
            if (!isEmptyError) {
                Spacer(Modifier.height(8.dp))
                Button(onClick = onRetryClick) {
                    Text(stringResource(id = R.string.action_retry))
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun DishItem(
    modifier: Modifier = Modifier,
    dish: Dish
) {
    val allergyNames = stringArrayResource(R.array.allergy_info)
    val allergyMessage = try {
        dish.allergy.joinToString { allergyNames[it] }
    } catch (e: ArrayIndexOutOfBoundsException) {
        ""
    }

    var secondaryText: @Composable (() -> Unit)? = null
    if (allergyMessage.isNotEmpty()) {
        secondaryText = {
            Text(allergyMessage)
        }
    }

    ListItem(
        modifier = modifier,
        secondaryText = secondaryText
    ) {
        Text(dish.name)
    }
}