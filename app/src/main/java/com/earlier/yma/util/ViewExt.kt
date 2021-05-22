package com.earlier.yma.util

import android.view.inputmethod.EditorInfo
import android.widget.EditText

fun EditText.setOnActionSearch(listener: (CharSequence) -> Unit) {
    setOnEditorActionListener { _, actionId, _ ->
        if (actionId == EditorInfo.IME_ACTION_SEARCH) {
            listener.invoke(text)
            true
        } else {
            false
        }
    }
}