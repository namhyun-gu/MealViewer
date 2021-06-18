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
package com.earlier.yma.ui.setting

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.ListItem
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.earlier.yma.BuildConfig
import com.earlier.yma.R
import com.earlier.yma.ui.base.AppBar
import com.earlier.yma.ui.base.ContentPanel
import com.earlier.yma.ui.theme.MealViewerTheme

@Composable
fun SettingActivityContent(
    modifier: Modifier = Modifier,
    onNavIconPress: () -> Unit = {},
    onShowOpenSourceLicense: () -> Unit = {},
    onSendFeedback: () -> Unit = {}
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            AppBar(
                title = {
                    Text(stringResource(R.string.activity_title_setting))
                },
                navigationIcon = {
                    IconButton(onClick = onNavIconPress) {
                        Icon(Icons.Rounded.ArrowBack, null)
                    }
                }
            )
        }
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            ContentPanel {
                Column {
                    SettingItem(
                        title = stringResource(R.string.subtitle_version),
                        subtitle = BuildConfig.VERSION_NAME
                    )
                    SettingItem(
                        title = stringResource(R.string.subtitle_open_source_license),
                    ) {
                        onShowOpenSourceLicense()
                    }
                    SettingItem(
                        title = stringResource(R.string.subtitle_feedback),
                    ) {
                        onSendFeedback()
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun SettingItem(
    modifier: Modifier = Modifier,
    title: String,
    subtitle: String? = null,
    onClick: () -> Unit = {}
) {
    var secondaryText: @Composable (() -> Unit)? = null
    if (subtitle != null) {
        secondaryText = { Text(subtitle) }
    }

    Surface(
        modifier = modifier,
        onClick = onClick,
        color = Color.Transparent
    ) {
        ListItem(
            text = {
                Text(title)
            },
            secondaryText = secondaryText,
        )
    }
}

@Preview(showBackground = true)
@Composable
fun SettingActivityContent_Preview() {
    MealViewerTheme {
        SettingActivityContent()
    }
}
