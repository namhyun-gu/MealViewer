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
package com.earlier.yma.ui.settings

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.earlier.yma.BuildConfig
import com.earlier.yma.R
import com.earlier.yma.ui.common.AppBar
import com.earlier.yma.ui.common.ClickableListItem
import com.earlier.yma.ui.common.ContentPanel
import com.earlier.yma.ui.theme.MealViewerTheme
import com.google.android.gms.oss.licenses.OssLicensesMenuActivity

@Composable
fun Settings(
    navController: NavController,
) {
    val context = LocalContext.current

    Scaffold(
        topBar = {
            AppBar(
                title = {
                    Text(stringResource(R.string.activity_title_setting))
                },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            navController.navigateUp()
                        }
                    ) {
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
                    ClickableListItem(
                        title = stringResource(R.string.subtitle_version),
                        subtitle = BuildConfig.VERSION_NAME
                    )
                    Divider(startIndent = 16.dp)
                    ClickableListItem(
                        title = stringResource(R.string.subtitle_open_source_license),
                    ) {
                        openOpenSourceLicense(context)
                    }
                    Divider(startIndent = 16.dp)
                    ClickableListItem(
                        title = stringResource(R.string.subtitle_feedback),
                    ) {
                        openSendFeedback(context)
                    }
                }
            }
        }
    }
}

private fun openOpenSourceLicense(context: Context) {
    val intent = Intent(context, OssLicensesMenuActivity::class.java)
    OssLicensesMenuActivity.setActivityTitle(
        context.getString(R.string.subtitle_open_source_license)
    )
    context.startActivity(intent)
}

private fun openSendFeedback(context: Context) {
    val intent = Intent(Intent.ACTION_SENDTO).apply {
        data = Uri.parse("mailto:mnhan0403@gmail.com")
    }
    context.startActivity(intent)
}

@Preview(showBackground = true)
@Composable
fun SettingActivityContent_Preview() {
    MealViewerTheme {
        Settings(rememberNavController())
    }
}
