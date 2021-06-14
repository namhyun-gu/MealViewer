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

import android.content.res.Configuration
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Card
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.ContentAlpha
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Check
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.earlier.yma.R
import com.earlier.yma.data.SearchResponse
import com.earlier.yma.ui.base.Center
import com.earlier.yma.ui.base.EditableUserInput
import com.earlier.yma.ui.base.PagingColumn
import com.earlier.yma.ui.theme.MealViewerTheme
import retrofit2.HttpException

@Composable
fun SearchActivityContent(
    modifier: Modifier = Modifier,
    onNavIconPress: () -> Unit = {}
) {
    val viewModel: SearchViewModel = viewModel()
    val uiState by viewModel.uiState.observeAsState(SearchUiState.Idle)

    Scaffold(
        modifier = modifier,
        topBar = {
            SearchTopBar(
                onNavIconPress = onNavIconPress
            )
        }
    ) {
        Column {
            SearchInputBar(
                onSearch = {
                    viewModel.search(it)
                },
            )
            Center(
                modifier = Modifier.fillMaxSize()
            ) {
                SearchContent(
                    uiState = uiState,
                    onFilterUpdate = { filterSet ->
                        viewModel.updateFilter(filterSet)
                    },
                    onSchoolSelect = {
                        viewModel.saveSchool(it)
                    },
                    onLoadMore = {
                        viewModel.loadMore()
                    }
                )
            }
        }
    }
}

@Composable
fun SearchContent(
    uiState: SearchUiState,
    onFilterUpdate: (Set<String>) -> Unit,
    onSchoolSelect: (SearchResponse.School) -> Unit,
    onLoadMore: () -> Unit,
) {
    when (uiState) {
        SearchUiState.Idle -> {
            Box { }
        }
        SearchUiState.Loading -> {
            CircularProgressIndicator()
        }
        is SearchUiState.Success -> {
            Column {
                FilterGroup(
                    modifier = Modifier.padding(
                        start = 16.dp,
                        end = 16.dp,
                        bottom = 16.dp
                    ),
                    orgList = uiState.orgList,
                    filterOrg = uiState.filterOrg,
                    onFilterUpdate = onFilterUpdate
                )
                SchoolList(
                    modifier = Modifier.fillMaxSize(),
                    items = uiState.schoolList,
                    onItemSelect = onSchoolSelect,
                    onLoadMore = onLoadMore
                )
            }
        }
        is SearchUiState.Error -> {
            val errorMessage = if (uiState.exception is HttpException) {
                stringResource(R.string.msg_search_error_code, uiState.exception.code())
            } else {
                stringResource(R.string.msg_search_error)
            }
            Text(errorMessage, style = MaterialTheme.typography.body1)
        }
    }
}

@Composable
fun SearchTopBar(
    onNavIconPress: () -> Unit = {}
) {
    TopAppBar(
        title = {
            Text(stringResource(R.string.activity_title_search))
        },
        navigationIcon = {
            IconButton(onClick = onNavIconPress) {
                Icon(
                    Icons.Rounded.ArrowBack,
                    contentDescription = null
                )
            }
        },
        backgroundColor = Color.Transparent,
        elevation = 0.dp
    )
}

@Preview(showBackground = true)
@Composable
fun SearchTopBar_Preview() {
    MealViewerTheme {
        SearchTopBar()
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun SearchInputBar(
    onSearch: (String) -> Unit
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    var textFieldState by remember { mutableStateOf("") }

    EditableUserInput(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        hint = stringResource(R.string.hint_search_school),
        onValueChange = {
            textFieldState = it
        },
        keyboardOptions = KeyboardOptions(
            imeAction = ImeAction.Search
        ),
        keyboardActions = KeyboardActions(
            onSearch = {
                onSearch(textFieldState)
                keyboardController?.hide()
            }
        ),
        icon = {
            Icon(Icons.Default.Search, null)
        },
    )
}

@Preview(showBackground = true)
@Composable
fun SearchInputBar_Preview() {
    MealViewerTheme {
        SearchInputBar() {}
    }
}

@Composable
fun SchoolList(
    modifier: Modifier = Modifier,
    items: List<SearchResponse.School> = listOf(),
    onLoadMore: () -> Unit,
    onItemSelect: (SearchResponse.School) -> Unit,
) {
    PagingColumn(
        modifier = modifier,
        content = items,
        onLoadMore = onLoadMore
    ) {
        SchoolItem(
            modifier = Modifier.fillMaxWidth(),
            school = it
        ) {
            onItemSelect(it)
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun SchoolItem(
    modifier: Modifier = Modifier,
    school: SearchResponse.School,
    onClick: () -> Unit,
) {
    Surface(
        onClick = onClick,
        modifier = modifier,
        shape = MaterialTheme.shapes.medium
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(school.name, fontWeight = FontWeight.Bold)
            CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
                Text(school.orgName, style = MaterialTheme.typography.body2)
            }
        }
    }
}

@Preview
@Composable
fun SchoolItem_Preview() {
    MealViewerTheme {
        SchoolItem(
            school = SearchResponse.School(
                orgCode = "",
                orgName = "Org name",
                code = "",
                name = "School name",
                kind = "",
                location = ""
            )
        ) {
        }
    }
}

@Composable
fun FilterGroup(
    modifier: Modifier = Modifier,
    orgList: List<String>,
    filterOrg: Set<String>,
    onFilterUpdate: (Set<String>) -> Unit
) {
    var filterState by rememberSaveable { mutableStateOf(filterOrg) }

    LazyRow(modifier = modifier) {
        item {
            FilterChip(
                text = stringResource(R.string.filter_all),
                isSelected = filterState.isEmpty()
            ) {
                filterState = emptySet()
                onFilterUpdate(filterState)
            }
            Spacer(modifier = Modifier.width(8.dp))
        }
        items(orgList) { orgName ->
            val isSelected = filterState.contains(orgName)

            FilterChip(
                text = orgName,
                isSelected = isSelected
            ) {
                filterState = if (isSelected) {
                    filterState.filter { it != orgName }.toSet()
                } else {
                    setOf(orgName, *filterState.toTypedArray())
                }
                onFilterUpdate(filterState)
            }
            Spacer(modifier = Modifier.width(8.dp))
        }
    }
}

@Preview
@Composable
fun FilterGroup_Preview() {
    MealViewerTheme {
        FilterGroup(orgList = listOf("Test1", "Test2", "Test3"), filterOrg = setOf("Test1")) {
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun FilterChip(
    text: String,
    isSelected: Boolean = false,
    onSelected: () -> Unit,
) {
    Card(
        onClick = onSelected,
        modifier = Modifier
            .height(32.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = 2.dp
    ) {
        val padding = if (isSelected) {
            Modifier.padding(start = 4.dp, end = 12.dp)
        } else {
            Modifier.padding(horizontal = 12.dp)
        }

        Row(
            modifier = padding,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (isSelected) {
                Icon(Icons.Rounded.Check, null, modifier = Modifier.size(24.dp))
                Spacer(modifier = Modifier.width(8.dp))
            }
            Text(text)
        }
    }
}

@Preview
@Composable
fun FilterChip_Preview() {
    MealViewerTheme {
        Row {
            FilterChip(text = "Test") {
            }
            FilterChip(text = "Test", isSelected = true) {
            }
        }
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun FilterChip_Preview_Dark() {
    MealViewerTheme {
        Row {
            FilterChip(text = "Test") {
            }
            FilterChip(text = "Test", isSelected = true) {
            }
        }
    }
}
