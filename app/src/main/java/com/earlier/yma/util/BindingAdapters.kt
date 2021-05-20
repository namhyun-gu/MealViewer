package com.earlier.yma.util

import android.view.inputmethod.EditorInfo
import android.widget.EditText
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.DividerItemDecoration.VERTICAL
import androidx.recyclerview.widget.RecyclerView

@BindingAdapter("setOnActionSearch")
fun EditText.setOnActionSearch(listener: () -> Unit) {
    setOnEditorActionListener { _, actionId, _ ->
        if (actionId == EditorInfo.IME_ACTION_SEARCH) {
            listener.invoke()
            true
        } else {
            false
        }
    }
}

@BindingAdapter("showDivider")
fun RecyclerView.showDivider(visible: Boolean) {
    if (visible) {
        this.addItemDecoration(DividerItemDecoration(context, VERTICAL))
    }
}