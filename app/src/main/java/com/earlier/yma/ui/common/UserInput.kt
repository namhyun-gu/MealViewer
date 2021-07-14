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
package com.earlier.yma.ui.common

import android.content.res.Configuration
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Icon
import androidx.compose.material.LocalContentColor
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusEvent
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.earlier.yma.ui.theme.MealViewerTheme

@Composable
fun EditableUserInput(
    hint: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    icon: @Composable (() -> Unit)? = null,
) {
    var textFieldState by remember { mutableStateOf(TextFieldValue(text = hint)) }
    val isHint = { textFieldState.text == hint }

    Surface(
        modifier = modifier,
        shape = MaterialTheme.shapes.medium
    ) {
        BasicTextField(
            value = textFieldState,
            onValueChange = {
                textFieldState = it
                if (!isHint()) onValueChange(textFieldState.text)
            },
            modifier = Modifier
                .padding(16.dp)
                .onFocusEvent {
                    if (it.isFocused && isHint()) {
                        textFieldState = TextFieldValue("")
                    }
                },
            textStyle = if (isHint()) {
                MaterialTheme.typography.body1.copy(
                    color = LocalContentColor.current.copy(alpha = 0.56f)
                )
            } else {
                MaterialTheme.typography.body1.copy(color = LocalContentColor.current)
            },
            singleLine = true,
            keyboardOptions = keyboardOptions,
            keyboardActions = keyboardActions,
            decorationBox = { innerTextField ->
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (icon != null) {
                        icon()
                        Spacer(modifier = Modifier.width(16.dp))
                    }
                    innerTextField()
                }
            },
            cursorBrush = SolidColor(LocalContentColor.current)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun EditableUserInput_Preview() {
    MealViewerTheme {
        EditableUserInput(
            hint = "Test",
            modifier = Modifier.padding(8.dp),
            onValueChange = {},
            icon = {
                Icon(Icons.Default.Search, null)
            }
        )
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Composable
fun EditableUserInput_Preview_Dark() {
    MealViewerTheme {
        EditableUserInput(
            hint = "Test",
            modifier = Modifier.padding(8.dp),
            onValueChange = {},
            icon = {
                Icon(Icons.Default.Search, null)
            }
        )
    }
}
