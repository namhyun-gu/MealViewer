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

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.ui.Modifier
import com.earlier.yma.R
import com.earlier.yma.ui.theme.MealViewerTheme
import com.google.accompanist.insets.navigationBarsPadding
import com.google.android.gms.oss.licenses.OssLicensesMenuActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SettingActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MealViewerTheme {
                SettingActivityContent(
                    modifier = Modifier.navigationBarsPadding(bottom = false),
                    onNavIconPress = {
                        finish()
                    },
                    onShowOpenSourceLicense = {
                        openOpenSourceLicense(this)
                    },
                    onSendFeedback = {
                        sendMail(this)
                    },
                )
            }
        }
    }

    fun sendMail() {
        val intent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:mnhan0403@gmail.com")
        }
        startActivity(intent)
    }

    fun openOpenSourceLicense() {
        OssLicensesMenuActivity.setActivityTitle(getString(R.string.subtitle_open_source_license))
        startActivity(Intent(this, OssLicensesMenuActivity::class.java))
    }
}

fun navigateSetting(context: Context) {
    context.startActivity(Intent(context, SettingActivity::class.java))
}
