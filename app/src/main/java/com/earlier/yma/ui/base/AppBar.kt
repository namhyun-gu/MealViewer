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
package com.earlier.yma.ui.base

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.ContentAlpha
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ProvideTextStyle
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.earlier.yma.ui.theme.MealViewerTheme

private val AppBarHeight = 56.dp

private val AppBarHorizontalPadding = 4.dp

private val TitleInset = Modifier.width(16.dp - AppBarHorizontalPadding)

private val TitleIconModifier = Modifier
    .padding(
        start = AppBarHorizontalPadding,
        end = AppBarHorizontalPadding
    )
    .size(AppBarHeight)

@Composable
fun AppBar(
    modifier: Modifier = Modifier,
    backgroundColor: Color = Color.Transparent,
    navigationIcon: @Composable () -> Unit = {},
    expandSpace: @Composable (() -> Unit)? = null,
    title: @Composable () -> Unit = {},
    subtitle: @Composable (() -> Unit)? = null,
    actions: @Composable RowScope.() -> Unit = {},
) {
    Surface(
        modifier = modifier,
        color = backgroundColor
    ) {
        Column {
            Box(
                TitleIconModifier,
                contentAlignment = Alignment.CenterStart
            ) {
                navigationIcon()
            }
            if (expandSpace != null) {
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(
                            start = AppBarHorizontalPadding,
                            end = AppBarHorizontalPadding,
                        ),
                    verticalAlignment = Alignment.Bottom
                ) {
                    Spacer(TitleInset)
                    expandSpace()
                }
            }
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(
                        start = AppBarHorizontalPadding,
                        end = AppBarHorizontalPadding,
                        bottom = 8.dp
                    ),
                verticalAlignment = Alignment.Bottom
            ) {
                Spacer(TitleInset)
                Column(
                    Modifier
                        .weight(1f),
                    verticalArrangement = Arrangement.Bottom,
                ) {
                    ProvideTextStyle(
                        value = MaterialTheme.typography.h4.copy(
                            fontWeight = FontWeight.Black
                        )
                    ) {
                        CompositionLocalProvider(
                            LocalContentAlpha provides ContentAlpha.high,
                            content = title
                        )
                    }
                    if (subtitle != null) {
                        ProvideTextStyle(value = MaterialTheme.typography.h6) {
                            CompositionLocalProvider(
                                LocalContentAlpha provides ContentAlpha.medium,
                                content = subtitle
                            )
                        }
                    }
                }
                Row(
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.Bottom,
                    content = actions
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AppBar_Preview() {
    MealViewerTheme {
        Column {
            AppBar(
                navigationIcon = {
                    IconButton(onClick = { /*TODO*/ }) {
                        Icon(
                            Icons.Rounded.ArrowBack,
                            contentDescription = null
                        )
                    }
                },
                title = {
                    Text("Title")
                },
                actions = {
                    IconButton(onClick = { /*TODO*/ }) {
                        Icon(
                            Icons.Rounded.Settings,
                            null
                        )
                    }
                }
            )
            Divider()
            Spacer(modifier = Modifier.height(8.dp))
            Divider()
            AppBar(
                navigationIcon = {
                    IconButton(onClick = { /*TODO*/ }) {
                        Icon(
                            Icons.Rounded.ArrowBack,
                            contentDescription = null
                        )
                    }
                },
                title = {
                    Text("Title")
                },
                subtitle = {
                    Text("Subtitle")
                },
                actions = {
                    IconButton(onClick = { /*TODO*/ }) {
                        Icon(
                            Icons.Rounded.Settings,
                            null
                        )
                    }
                }
            )
        }
    }
}
